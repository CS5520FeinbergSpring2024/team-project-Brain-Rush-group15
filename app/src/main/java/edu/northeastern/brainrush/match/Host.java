package edu.northeastern.brainrush.match;

public class Host {
    private String status;
    private String subject;

    public Host(){

    }
    public Host(String subject){
        this.status = "Waiting";
        this.subject = subject;
    }

    public Host(String status, String subject){
        this.status = status;
        this.subject = subject;
    }

    public String getStatus(){
        return this.status;
    }

    public String getSubject(){
        return this.subject;
    }
}
