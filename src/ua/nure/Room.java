package ua.nure;

import ua.nure.agents.WumpusWorldAgent;

import java.util.ArrayList;
import java.util.List;

public final class Room {

    public final List<String> codes;

    public Room(int... codes) {
        this.codes = new ArrayList<>();
        for (int code : codes) {
            this.codes.add(WumpusWorldAgent.ROOM_CODES.get(code));
        }
    }

    @Override
    public String toString() {
        return codes.toString();
    }
}
