package edu.northeastern.brainrush;

public class Quizz {//dummy class for question data class, will be replaced later

    private String title;
    private String Context;

    private int Answer;

    //choices;
    //answer

    private int likeness;
    public Quizz(String t, int l){
        this.title = t;
        this.likeness = l;
        this.Answer = 0;

    }
    //get likeness
    public int getlikeness(){
        return likeness;
    }
    //get title
    public String getTitle(){
        return title;
    }

    public int getAnswer(){
        return Answer;
    }
}
