package edu.northeastern.brainrush.match;

import java.util.List;

import edu.northeastern.brainrush.model.Question;

public class MatchRoom {
    String host;
    String guest;
    int host_heartBeat;
    int guest_heartBeat;

    public MatchRoom(String host, String guest){
        this.host = host;
        this.guest = guest;
        this.host_heartBeat = 0;
        this.guest_heartBeat = 0;
    }

    public String getHost() {
        return this.host;
    }

    public String getGuest() {
        return this.guest;
    }

}
