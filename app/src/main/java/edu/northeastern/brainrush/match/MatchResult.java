package edu.northeastern.brainrush.match;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MatchResult {
    long guest_score;
    long host_score;
    long guest_start;
    long host_start;
    long guest_end;
    long host_end;

    public MatchResult(){

    }

    public MatchResult(long guest_score, long host_score, long guest_start, long host_start, long guest_end, long host_end) {
        this.guest_score = guest_score;
        this.host_score = host_score;
        this.guest_start = guest_start;
        this.host_start = host_start;
        this.guest_end = guest_end;
        this.host_end = host_end;
    }

    public boolean checkComplete(){
        return this.guest_end !=0 && this.host_end != 0;
    }

    public long getGuest_score() {
        return guest_score;
    }

    public long getHost_score() {
        return host_score;
    }

    public long getGuest_start() {
        return guest_start;
    }

    public long getHost_start() {
        return host_start;
    }

    public long getGuest_end() {
        return guest_end;
    }

    public long getHost_end() {
        return host_end;
    }

    public void setGuest_score(long guest_score) {
        this.guest_score = guest_score;
    }

    public void setHost_score(long host_score) {
        this.host_score = host_score;
    }

    public void setGuest_start(long guest_start) {
        this.guest_start = guest_start;
    }

    public void setHost_start(long host_start) {
        this.host_start = host_start;
    }

    public void setGuest_end(long guest_end) {
        this.guest_end = guest_end;
    }

    public void setHost_end(long host_end) {
        this.host_end = host_end;
    }
}
