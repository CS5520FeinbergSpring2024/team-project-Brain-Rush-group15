package edu.northeastern.brainrush.model;

public class Profile {
    private final String defaultUrl = "https://i.imgur.com/DvpvklR.png";
    private String name;
    private String picture;
    private int level;
    private int score;


    public Profile(String name, String picture, int level, int score) {
        this.name = name;
        this.picture = picture;
        this.level = level;
        this.score = score;
    }

    public Profile(String name) {
        this.name = name;
        this.level = 1;
        this.score = 0;
        this.picture = defaultUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
