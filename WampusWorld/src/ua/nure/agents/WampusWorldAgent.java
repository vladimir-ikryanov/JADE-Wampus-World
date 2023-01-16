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
import ua.nure.Room;

import java.util.*;

public final class WampusWorldAgent extends Agent {

    public static String SERVICE_DESCRIPTION = "WAMPUS-WORLD";

    public static final Map<Integer, String> ROOM_CODES = new HashMap<Integer, String>() {{
        put(START, NavigatorAgent.START);
        put(GOLD, NavigatorAgent.GOLD);
        put(PIT, NavigatorAgent.PIT);
        put(BUMP, NavigatorAgent.BUMP);
        put(BREEZE, NavigatorAgent.BREEZE);
        put(STENCH, NavigatorAgent.STENCH);
        put(WAMPUS, NavigatorAgent.WAMPUS);
        put(SCREAM, NavigatorAgent.SCREAM);
    }};

    private static final int START = -1;
    private static final int WAMPUS = 1;
    private static final int PIT = 2;
    private static final int BREEZE = 3;
    private static final int STENCH = 4;
    private static final int SCREAM = 5;
    private static final int GOLD = 6;
    private static final int BUMP = 7;

    // The Wampus world dimension.
    private static final int NUM_OF_ROWS = 4;
    private static final int NUM_OF_COLUMNS = 4;

    private Room[][] wampusMap;
    private HashMap<AID, Location> speleologists;

    String nickname = "WampusWorld";
    AID id = new AID(nickname, AID.ISLOCALNAME);

    @Override
    protected void setup() {
        System.out.println("[" + getAID().getName() + "] agent is ready.");
        speleologists = new HashMap<>();

        initMap();
        printMap();

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(SpeleologistAgent.WAMPUS_WORLD_TYPE);
        serviceDescription.setName(SERVICE_DESCRIPTION);
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new SpeleologistConnectPerformer());
        addBehaviour(new SpeleologistShootPerformer());
        addBehaviour(new SpeleologistTakeGoldPerformer());
        addBehaviour(new SpeleologistMovePerformer());
    }

    private void initMap() {
        wampusMap = new Room[NUM_OF_ROWS][NUM_OF_COLUMNS];
        wampusMap[0][0] = new Room();
        wampusMap[0][1] = new Room(BREEZE);
        wampusMap[0][2] = new Room(PIT);
        wampusMap[0][3] = new Room(BREEZE);

        wampusMap[1][0] = new Room(STENCH);
        wampusMap[1][1] = new Room();
        wampusMap[1][2] = new Room(BREEZE);
        wampusMap[1][3] = new Room();

        wampusMap[2][0] = new Room(WAMPUS);
        wampusMap[2][1] = new Room(STENCH, GOLD);
        wampusMap[2][2] = new Room();
        wampusMap[2][3] = new Room(BREEZE);

        wampusMap[3][0] = new Room(STENCH);
        wampusMap[3][1] = new Room();
        wampusMap[3][2] = new Room(BREEZE);
        wampusMap[3][3] = new Room(PIT);
    }

    private void printMap() {
        System.out.println("Wampus world map: ");
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            System.out.println(Arrays.toString(wampusMap[i]));
        }
    }

    private class SpeleologistConnectPerformer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String message = msg.getContent();
                if (Objects.equals(message, SpeleologistAgent.GO_INSIDE)) {
                    AID speleologist = msg.getSender();
                    speleologists.put(speleologist, new Location(0, 0));
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    String content = wampusMap[0][0].codes.toString();
                    System.out.println("World: Speleologist went inside at 0:0 location");
                    reply.setContent(content);
                    myAgent.send(reply);
                }
            } else {
                block();
            }
        }
    }

    private class SpeleologistShootPerformer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(SpeleologistAgent.SHOOT_ARROW);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(SpeleologistAgent.SHOOT_ARROW);

                String message = msg.getContent();
                AID speleologist = msg.getSender();
                Location speleologistLocation = speleologists.get(speleologist);

                int row = speleologistLocation.row;
                int column = speleologistLocation.column;
                String answer = "";
                if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_DOWN))) {
                    for (int i = 0; i < row; ++i) {
                        if (wampusMap[i][column].codes.contains(WampusWorldAgent.ROOM_CODES.get(WAMPUS))) {
                            answer = NavigatorAgent.SCREAM;
                        }
                    }
                } else if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_UP))) {
                    for (int i = row + 1; i < NUM_OF_ROWS; ++i) {
                        if (wampusMap[i][column].codes.contains(WampusWorldAgent.ROOM_CODES.get(WAMPUS))) {
                            answer = NavigatorAgent.SCREAM;
                        }
                    }
                } else if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_LEFT))) {
                    for (int i = 0; i < column; ++i) {
                        if (wampusMap[row][i].codes.contains(WampusWorldAgent.ROOM_CODES.get(WAMPUS))) {
                            answer = NavigatorAgent.SCREAM;
                        }
                    }
                } else if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_RIGHT))) {
                    for (int i = column + 1; i < NUM_OF_COLUMNS; ++i) {
                        if (wampusMap[row][i].codes.contains(WampusWorldAgent.ROOM_CODES.get(WAMPUS))) {
                            answer = NavigatorAgent.SCREAM;
                        }
                    }
                }

                reply.setContent(answer);
                System.out.println("World: Speleologist made a shoot.");
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    private class SpeleologistMovePerformer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(SpeleologistAgent.MOVE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(SpeleologistAgent.MOVE);

                String message = msg.getContent();
                AID speleologist = msg.getSender();
                Location speleologistLocation = speleologists.get(speleologist);
                if (speleologistLocation == null) {
                    speleologists.put(speleologist, new Location());
                    speleologistLocation = speleologists.get(speleologist);
                }
                int row = speleologistLocation.row;
                int column = speleologistLocation.column;
                if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_DOWN))) {
                    row += 1;
                } else if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_UP))) {
                    row -= 1;
                } else if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_LEFT))) {
                    column -= 1;
                } else if (message.equals(SpeleologistAgent.actionCodes.get(SpeleologistAgent.LOOK_RIGHT))) {
                    column += 1;
                }
                if (row > -1 && column > -1 && row < NUM_OF_ROWS && column < NUM_OF_COLUMNS) {
                    speleologistLocation.row = row;
                    speleologistLocation.column = column;
                    String replyMessage = wampusMap[row][column].codes.toString();
                    reply.setContent(replyMessage);
                } else {
                    String replyMessage = String.valueOf(new ArrayList<String>() {{
                        add(NavigatorAgent.BUMP);
                    }});
                    reply.setContent(replyMessage);
                }
                System.out.println("World: Speleologist is in room: " + reply.getContent());
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    private class SpeleologistTakeGoldPerformer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(SpeleologistAgent.TAKE_GOLD);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                AID speleologist = msg.getSender();
                Location speleologistLocation = speleologists.get(speleologist);
                if (speleologistLocation == null) {
                    speleologists.put(speleologist, new Location());
                } else {
                    if (wampusMap[speleologistLocation.row][speleologistLocation.column].codes.contains(WampusWorldAgent.ROOM_CODES.get(GOLD))) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(SpeleologistAgent.TAKE_GOLD);
                        reply.setContent("GOLD");
                        System.out.println("World: Speleologist took gold! Finish.");
                        myAgent.send(reply);
                    }
                }
            } else {
                block();
            }
        }
    }
}
