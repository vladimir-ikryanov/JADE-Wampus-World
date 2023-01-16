package ua.nure;

import ua.nure.agents.NavigatorAgent;

public final class RoomStatus {

    public int stench;
    public int breeze;
    public int pit;
    public int wampus;
    public int ok;
    public int gold;
    public int noWay;

    public RoomStatus() {
        stench = NavigatorAgent.ROOM_STATUS_NO_STATUS;
        breeze = NavigatorAgent.ROOM_STATUS_NO_STATUS;
        pit = NavigatorAgent.ROOM_STATUS_NO_STATUS;
        wampus = NavigatorAgent.ROOM_STATUS_NO_STATUS;
        ok = NavigatorAgent.ROOM_STATUS_NO_STATUS;
        gold = NavigatorAgent.ROOM_STATUS_NO_STATUS;
        noWay = NavigatorAgent.ROOM_STATUS_NO_STATUS;
    }

    public void update(String status) {
        switch (status) {
            case NavigatorAgent.WAMPUS:
                wampus = NavigatorAgent.ROOM_STATUS_TRUE;
                break;
            case NavigatorAgent.PIT:
                pit = NavigatorAgent.ROOM_STATUS_TRUE;
                break;
            case NavigatorAgent.BREEZE:
                breeze = NavigatorAgent.ROOM_STATUS_TRUE;
                break;
            case NavigatorAgent.STENCH:
                stench = NavigatorAgent.ROOM_STATUS_TRUE;
                break;
            case NavigatorAgent.GOLD:
                gold = NavigatorAgent.ROOM_STATUS_TRUE;
                break;
            case NavigatorAgent.EMPTY:
                ok = NavigatorAgent.ROOM_STATUS_TRUE;
                break;
            case NavigatorAgent.START:
            case NavigatorAgent.SCREAM:
            case NavigatorAgent.BUMP:
                break;
        }
    }

    @Override
    public String toString() {
        return "RoomStatus{" +
                "stench=" + stench +
                ", breeze=" + breeze +
                ", pit=" + pit +
                ", wampus=" + wampus +
                ", ok=" + ok +
                ", gold=" + gold +
                ", noWay=" + noWay +
                '}';
    }
}
