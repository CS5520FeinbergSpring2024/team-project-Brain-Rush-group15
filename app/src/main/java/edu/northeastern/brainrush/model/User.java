package edu.northeastern.brainrush.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Parcelable {
    private final String defaultUrl = "https://i.imgur.com/DvpvklR.png";
    private long id;
    private String name;
    private String picture;
    private int experience;
    private int score;
    private Date date_created;
    private int no_of_likes;
    private int no_of_dislikes;
    private Set<Long> questions_created;
    private Set<Long> questions_answered;
    private Set<Long> daily_question_answered;

    public User(String name) {
        this.name = name;
        this.picture = defaultUrl;
        this.experience = 0;
        this.score = 0;
        this.date_created = new Date();
        this.no_of_dislikes = 0;
        this.no_of_likes = 0;
        this.questions_answered = new HashSet<>();
        this.questions_created = new HashSet<>();
        this.daily_question_answered = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public int getExperience() {
        return experience;
    }

    public int getScore() {
        return score;
    }

    public Date getDate_created() {
        return date_created;
    }

    public int getNo_of_likes() {
        return no_of_likes;
    }

    public int getNo_of_dislikes() {
        return no_of_dislikes;
    }

    public Set<Long> getQuestions_created() {
        return questions_created;
    }

    public Set<Long> getQuestions_answered() {
        return questions_answered;
    }

    public Set<Long> getDaily_question_answered() {
        return daily_question_answered;
    }

    public void add_questions_created(long id){
        this.questions_created.add(id);
    }

    public void add_questions_answered(long id){
        this.questions_answered.add(id);
    }

    public void add_daily_question_answered(long id){
        this.daily_question_answered.add(id);
    }

    public int increment_likes(){
        this.no_of_likes++;
        return this.no_of_likes;
    }

    public int increment_dislikes(){
        this.no_of_dislikes++;
        return this.no_of_dislikes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}
