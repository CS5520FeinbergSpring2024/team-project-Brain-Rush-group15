package edu.northeastern.brainrush.match;

public class MatchRoom {
    String host;

    String guest;

    public MatchRoom(String host, String guest){
        this.host = host;
        this.guest = guest;
    }

    public String getHost() {
        return this.host;
    }

    public String getGuest() {
        return this.guest;
    }
}
