package edu.northeastern.brainrush.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@IgnoreExtraProperties
public class Question {

    public String subject;
    public String context;
    public String choice1;
    public String choice2;
    public String choice3;
    public String choice4;
    public String correctAnswer;
    public String dateCreated;
    public String type;
    public ArrayList<String> likes;
    public ArrayList<String> dislikes;
    public String creatorId;
    public String expValue;
    public String id;


    public Question(){

    }
    public Question(String subject, String context, String choice1, String choice2, String choice3,
                    String choice4, String correctAnswer, String dateCreated,
                    String type, ArrayList<String> likes, ArrayList<String> dislikes,
                    String creatorId, String expValue){
        this.subject = subject;
        this.context = context;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.correctAnswer = correctAnswer;
        this.dateCreated = dateCreated;
        this.type = type;
        this.likes = likes;
        this.dislikes = dislikes;
        this.creatorId = creatorId;
        this.expValue = expValue;
        this.id = this.dateCreated + " " + creatorId;
    }
}
