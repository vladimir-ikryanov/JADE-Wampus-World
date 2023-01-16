package ua.nure;

import ua.nure.agents.NavigatorAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WumpusWorld {

    public boolean isWumpusAlive = true;
    public int wumpusRoomCount = 0;

    public final Map<Location, RoomStatus> grid = new HashMap<>();

    public WumpusWorld() {
    }

    public Location wumpusLocation() {
        int x = 0;
        int y = 0;

        Set<Location> keys = grid.keySet();
        for (Location roomLocation : keys) {
            RoomStatus room = grid.get(roomLocation);
            if (room.wumpus == NavigatorAgent.ROOM_STATUS_POSSIBLE) {
                x += roomLocation.row;
                y += roomLocation.column;
            }
        }
        x /= wumpusRoomCount;
        y /= wumpusRoomCount;
        return new Location(x, y);
    }
}
