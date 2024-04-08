package edu.northeastern.brainrush;

import java.util.List;

public class Question_dummy {
    public int id;
    public String subjects;

    public String creater_id;

    public String question_body;

    public List<String> choices;

    public int answer;

    public Question_dummy(){
        this.id = 0;
        this.subjects = "Biology";
        this.creater_id = "dummy creater id";
        this.question_body = "Dummy dummy dummy dummy dummy dummy?";
//        this.choices = new String[4];
//        choices[0] = "dummy";
//        choices[1] = "dummy";
//        choices[2] = "dummy";
//        choices[3] = "dummy";
        answer = 0;
    }



    public Question_dummy(int no){
        this.id = no;
        this.subjects = "Biology";
        this.creater_id = "dummy creater id";
        this.question_body = "Dummy dummy dummy dummy dummy dummy?";
//        this.choices = new String[4];
//        choices[0] = "dummy";
//        choices[1] = "dummy";
//        choices[2] = "dummy";
//        choices[3] = "dummy";
        answer = 0;


    }

    public String getQbody(){
        return question_body;
    }

    public List<String> getQchoices(){
        return choices;
    }

    public int getQanswer(){
        return answer;
    }

}
