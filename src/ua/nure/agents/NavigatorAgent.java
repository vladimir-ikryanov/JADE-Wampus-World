package ua.nure.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ua.nure.Location;
import ua.nure.RoomStatus;
import ua.nure.WumpusWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;

public final class NavigatorAgent extends Agent {

    public static final String START = "start";
    public static final String EMPTY = "empty";
    public static final String WUMPUS = "wumpus";
    public static final String PIT = "pit";
    public static final String BREEZE = "breeze";
    public static final String STENCH = "stench";
    public static final String SCREAM = "scream";
    public static final String GOLD = "gold";
    public static final String BUMP = "bump";

    private static final int ROOM_EXIST = 1;
    private static final int ROOM_STENCH = 2;
    private static final int ROOM_BREEZE = 3;
    private static final int ROOM_PIT = 4;
    private static final int ROOM_WUMPUS = 5;
    private static final int ROOM_EMPTY = 6;
    private static final int ROOM_GOLD = 7;

    public static int ROOM_STATUS_TRUE = 1;
    public static int ROOM_STATUS_FALSE = 2;
    public static int ROOM_STATUS_POSSIBLE = 3;
    public static int ROOM_STATUS_NO_GOLD_WAY = 4;
    public static int ROOM_STATUS_NO_STATUS = -1;

    private static final String SERVICE_DESCRIPTION = "NAVIGATOR_AGENT";

    String nickname = "ua.nure.agents.NavigatorAgent";
    AID id = new AID(nickname, AID.ISLOCALNAME);
    private Hashtable<AID, Location> agentLocation;
    private Hashtable<AID, LinkedList<int[]>> agentWayStory;

    private boolean moveRoom = false;
    private int agentRow;
    private int agentColumn;

    WumpusWorld world;

    @Override
    protected void setup() {
        world = new WumpusWorld();
        agentWayStory = new Hashtable<>();
        agentLocation = new Hashtable<>();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(SpeleologistAgent.NAVIGATOR_AGENT_TYPE);
        sd.setName(SERVICE_DESCRIPTION);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new LocationRequestsServer());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Navigator Agent " + getAID().getName() + " terminating.");
    }

    private class LocationRequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                AID requestAgent = msg.getSender();
                if (!agentWayStory.contains(requestAgent)) {
                    agentWayStory.put(requestAgent, new LinkedList<>());
                }
                Location location = NavigatorAgent.this.agentLocation.get(requestAgent);
                if (location == null) {
                    location = new Location();
                    NavigatorAgent.this.agentLocation.put(requestAgent, location);
                }
                String content = msg.getContent();
                content = content.substring(1, content.length() - 1);
                String[] roomInfo = content.split(", ");
//                System.out.println("Navigator: Room info: " + Arrays.toString(roomInfo));
//                System.out.println("Navigator: Agent location: " + location.x + ":" + location.y);
                String[] actions = getActions(requestAgent, location, roomInfo);
                location.row = agentRow;
                location.column = agentColumn;
                ACLMessage reply = msg.createReply();

                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(Arrays.toString(actions));
                System.out.println("Navigator: Propose action: " + reply.getContent());
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    private String[] getActions(AID requestAgent, Location currentAgentLocation, String[] roomInfo) {
//        System.out.println("Agent prev location: " + currentAgentLocation.x + ":" + currentAgentLocation.y);
        int[] actions;
        RoomStatus checkingRoom = world.grid.get(currentAgentLocation);
        if (checkingRoom == null) {
            checkingRoom = new RoomStatus();
            world.grid.put(currentAgentLocation, checkingRoom);
        }

        if (!Arrays.asList(roomInfo).contains(BUMP)) {
            LinkedList<int[]> agentStory = agentWayStory.get(requestAgent);
            agentStory.add(new int[]{currentAgentLocation.row, currentAgentLocation.column});
            currentAgentLocation.row = agentRow;
            currentAgentLocation.column = agentColumn;
            moveRoom = false;
        }
        checkingRoom = world.grid.get(currentAgentLocation);
        if (checkingRoom == null) {
            checkingRoom = new RoomStatus();
            world.grid.put(currentAgentLocation, checkingRoom);
        }

        if (checkingRoom.ok != NavigatorAgent.ROOM_STATUS_TRUE) {
            checkingRoom.ok = NavigatorAgent.ROOM_STATUS_TRUE;
        }
        for (String event : roomInfo) {
            checkingRoom.update(event);
        }
        System.out.println("Navigator: Speleologist location: " + currentAgentLocation);
        System.out.println("Navigator: Checking room status: " + checkingRoom);
        updateNeighbors(currentAgentLocation);
        if (checkingRoom.gold == ROOM_STATUS_TRUE) {
            actions = new int[] {SpeleologistAgent.TAKE_GOLD};
        } else if (checkingRoom.wumpus == ROOM_STATUS_TRUE) {
            actions = new int[] {SpeleologistAgent.SHOOT_ARROW};
        } else {
            Location[] nextOkRooms = getOkNeighbors(requestAgent, currentAgentLocation);
            int bestCandidate = -1;
            int candidateStatus = -1;
            for (int i = 0; i < nextOkRooms.length; ++i) {
                Location candidateRoom = nextOkRooms[i];
                if (candidateRoom.row > currentAgentLocation.row) {
                    bestCandidate = i;
                    break;
                } else if (candidateRoom.column > currentAgentLocation.column) {
                    if (candidateStatus < 3) {
                        candidateStatus = 3;
                    } else continue;
                } else if (candidateRoom.row < currentAgentLocation.row) {
                    if (candidateStatus < 2) {
                        candidateStatus = 2;
                    } else continue;
                } else {
                    if (candidateStatus < 1) {
                        candidateStatus = 1;
                    } else continue;
                }
                bestCandidate = i;
            }
            actions = getNextRoomAction(currentAgentLocation, nextOkRooms[bestCandidate], SpeleologistAgent.MOVE);
        }

        String[] languageActions = new String[actions.length];
        for (int i = 0; i < actions.length; ++i) {
            languageActions[i] = SpeleologistAgent.actionCodes.get(actions[i]);
        }
        return languageActions;
    }

    private int[] getNextRoomAction(Location agentLocation, Location proposedRoom, int action) {
        agentRow = agentLocation.row;
        agentColumn = agentLocation.column;
        int look;
        if (agentLocation.column < proposedRoom.column) {
            agentColumn += 1;
            look = SpeleologistAgent.LOOK_RIGHT;
        } else if (agentLocation.column > proposedRoom.column) {
            agentColumn -= 1;
            look = SpeleologistAgent.LOOK_LEFT;
        } else if (agentLocation.row < proposedRoom.row) {
            agentRow += 1;
            look = SpeleologistAgent.LOOK_DOWN;
        } else {
            agentRow -= 1;
            look = SpeleologistAgent.LOOK_UP;
        }
        moveRoom = true;

        return new int[]{look, action};
    }

    private Location[] getOkNeighbors(AID requestAgent, Location requestAgentLocation) {
        Location[] okNeighbors = getNeighborsPosition(requestAgentLocation);
        ArrayList<Location> okLocations = new ArrayList<>();
        for (Location location : okNeighbors) {
            world.grid.putIfAbsent(location, new RoomStatus());
            if ((world.grid.get(location).ok == NavigatorAgent.ROOM_STATUS_TRUE
                    && world.grid.get(location).noWay != NavigatorAgent.ROOM_STATUS_TRUE
            ) ||
                    world.grid.get(location).ok == NavigatorAgent.ROOM_STATUS_NO_STATUS) {
                okLocations.add(location);
            }
        }
        if (okLocations.size() == 0) {
            int row = agentWayStory.get(requestAgent).getLast()[0];
            int column = agentWayStory.get(requestAgent).getLast()[1];
            okLocations.add(new Location(row, column));
            world.grid.get(requestAgentLocation).noWay = ROOM_STATUS_TRUE;
        }
        return okLocations.toArray(new Location[0]);
    }

    private RoomStatus[] getNeighborsImaginaryRoom(Location requestAgentLocation) {
        Location rightNeighbor = new Location(requestAgentLocation.row + 1, requestAgentLocation.column);
        Location upNeighbor = new Location(requestAgentLocation.row, requestAgentLocation.column + 1);
        Location leftNeighbor = new Location(requestAgentLocation.row - 1, requestAgentLocation.column);
        Location bottomNeighbor = new Location(requestAgentLocation.row, requestAgentLocation.column - 1);
        RoomStatus rightRoom = world.grid.get(rightNeighbor);
        if (rightRoom == null) {
            rightRoom = new RoomStatus();
            world.grid.put(rightNeighbor, rightRoom);
        }
        RoomStatus upRoom = world.grid.get(upNeighbor);
        if (upRoom == null) {
            upRoom = new RoomStatus();
            world.grid.put(rightNeighbor, upRoom);
        }
        RoomStatus leftRoom = world.grid.get(leftNeighbor);
        if (leftRoom == null) {
            leftRoom = new RoomStatus();
            world.grid.put(rightNeighbor, leftRoom);
        }
        RoomStatus bottomRoom = world.grid.get(bottomNeighbor);
        if (bottomRoom == null) {
            bottomRoom = new RoomStatus();
            world.grid.put(rightNeighbor, bottomRoom);
        }
        return new RoomStatus[]{rightRoom, upRoom, leftRoom, bottomRoom};
    }

    private Location[] getNeighborsPosition(Location requestAgentLocation) {
        Location rightNeighbor = new Location(requestAgentLocation.row + 1, requestAgentLocation.column);
        Location upNeighbor = new Location(requestAgentLocation.row, requestAgentLocation.column + 1);
        Location leftNeighbor = new Location(requestAgentLocation.row - 1, requestAgentLocation.column);
        Location bottomNeighbor = new Location(requestAgentLocation.row, requestAgentLocation.column - 1);
        return new Location[]{rightNeighbor, upNeighbor, leftNeighbor, bottomNeighbor};
    }

    private void updateNeighbors(Location requestAgentLocation) {
        RoomStatus currentRoom = world.grid.get(requestAgentLocation);
        RoomStatus[] roomList = getNeighborsImaginaryRoom(requestAgentLocation);

        if (currentRoom.stench == NavigatorAgent.ROOM_STATUS_TRUE) {
            world.wumpusRoomCount = world.wumpusRoomCount + 1;
            for (RoomStatus room : roomList) {
                if (room.wumpus == NavigatorAgent.ROOM_STATUS_NO_STATUS) {
                    room.ok = NavigatorAgent.ROOM_STATUS_POSSIBLE;
                    room.wumpus = NavigatorAgent.ROOM_STATUS_POSSIBLE;
                }
            }
        }
        if (currentRoom.breeze == NavigatorAgent.ROOM_STATUS_TRUE) {
            for (RoomStatus room : roomList) {
                if (room.pit == NavigatorAgent.ROOM_STATUS_NO_STATUS) {
                    room.ok = NavigatorAgent.ROOM_STATUS_POSSIBLE;
                    room.pit = NavigatorAgent.ROOM_STATUS_POSSIBLE;
                }
            }
        }
        if (currentRoom.breeze == NavigatorAgent.ROOM_STATUS_FALSE && currentRoom.stench == NavigatorAgent.ROOM_STATUS_FALSE) {
            for (RoomStatus room : roomList) {
                room.ok = NavigatorAgent.ROOM_STATUS_TRUE;
                room.wumpus = NavigatorAgent.ROOM_STATUS_FALSE;
                room.pit = NavigatorAgent.ROOM_STATUS_FALSE;
            }
        }
    }
}
