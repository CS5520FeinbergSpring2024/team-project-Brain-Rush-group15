package edu.northeastern.brainrush;

public class Quizz {

    private String title;
    private String Context;

    private int likeness;
    public Quizz(String t, int l){
        this.title = t;
        this.likeness = l;

    }
    //get likeness
    public int getlikeness(){
        return likeness;
    }
    //get title
    public String getTitle(){
        return title;
    }
}
