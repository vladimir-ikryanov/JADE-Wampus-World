package ua.nure;

import ua.nure.agents.NavigatorAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WampusWorld {

    public boolean isWampusAlive = true;
    public int wampusRoomCount = 0;

    public final Map<Location, RoomStatus> grid = new HashMap<>();

    public WampusWorld() {
    }

    public Location wampusLocation() {
        int x = 0;
        int y = 0;

        Set<Location> keys = grid.keySet();
        for (Location roomLocation : keys) {
            RoomStatus room = grid.get(roomLocation);
            if (room.wampus == NavigatorAgent.ROOM_STATUS_POSSIBLE) {
                x += roomLocation.row;
                y += roomLocation.column;
            }
        }
        x /= wampusRoomCount;
        y /= wampusRoomCount;
        return new Location(x, y);
    }
}
