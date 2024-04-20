package edu.northeastern.brainrush;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

import edu.northeastern.brainrush.model.Question;
import edu.northeastern.brainrush.model.User;

public class DailyQuestionProblemPage extends AppCompatActivity {
    private LottieAnimationView lottie;
    private TextView dateHeader;
    private FirebaseDatabase mDatabase;
    private TextView explanationText;
    private RadioGroup optionContainer;
    private TextView questionHeader;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private TextView answerIndicator;
    private String correct_answer;
    private String question_id;
    private String date;
    private String expValue;
    private User user;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_question_question_page);

        // Setting up database
        mDatabase = FirebaseDatabase.getInstance();

        // Setting up the views
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        questionHeader = findViewById(R.id.question_header);
        answerIndicator = findViewById(R.id.correct_answer_indicator);
        optionContainer = findViewById(R.id.options_container);
        explanationText = findViewById(R.id.explanation_text);
        dateHeader = findViewById(R.id.date_header);
        answerIndicator.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Setting up the date selected from the previous page
            date = extras.getString("date");
            user = (User) extras.get("user");
            id = extras.getString("id");
            dateHeader.setText(date);
            getQuestionId(date);
            getQuestionContext();
        }

        lottie = findViewById(R.id.lottieAnimationView);
        lottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Animation has ended, perform any actions here if needed
                playAnimation();
            }
        });
    }

    private void getQuestionId(String date){
        DatabaseReference dailyQuestionReference = FirebaseDatabase.getInstance().getReference("DailyQuestion");

        dailyQuestionReference.addValueEventListener(new  ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("target_date").getValue(String.class).equals(date)){
                        question_id =  snapshot.child("question_id").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });
    }

    private void getQuestionContext(){
        DatabaseReference questionReference = FirebaseDatabase.getInstance().getReference("Question");
        questionReference.addValueEventListener(new  ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("id").getValue(String.class).equals(question_id)){
                        // Found the matching id
                        expValue = snapshot.child("expValue").getValue(String.class);
                        questionHeader.setText(snapshot.child("context").getValue(String.class));
                        option1.setText(snapshot.child("choice1").getValue(String.class));
                        option2.setText(snapshot.child("choice2").getValue(String.class));
                        option3.setText(snapshot.child("choice3").getValue(String.class));
                        option4.setText(snapshot.child("choice4").getValue(String.class));
                        correct_answer = snapshot.child("correctAnswer").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });
    }

    public void setCorrect_answer_view(){
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        if(!user.getDaily_question_answered().contains(date)){
            user.add_daily_question_answered(date);
            user.add_questions_answered(question_id);
            user.addExperience(Integer.parseInt(expValue));
        }
        userReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                userReference.child(id).setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
        answerIndicator.setVisibility(View.VISIBLE);
        answerIndicator.setText("Correct!");
        answerIndicator.setTextColor(Color.parseColor("#4CAF50"));
        answerIndicator.setBackgroundColor(Color.parseColor("#DDFFDD"));
        if (!lottie.isAnimating()) {
            // Play the animation once
            lottie.playAnimation();
        }
    }

    public void setIncorrect_answer_view(){
        answerIndicator.setVisibility(View.VISIBLE);
        answerIndicator.setText("Inorrect!");
        answerIndicator.setTextColor(Color.parseColor("#FFFFFF"));
        answerIndicator.setBackgroundColor(Color.parseColor("#B00020"));
    }

    public void dailyQuestionSubmit(View view){
        if(option1.isChecked() && Objects.equals(correct_answer, "1")){
            setCorrect_answer_view();
        }
        else if(option2.isChecked() && Objects.equals(correct_answer, "2")){
            setCorrect_answer_view();
        }
        else if(option3.isChecked() && Objects.equals(correct_answer, "3")){
            setCorrect_answer_view();
        }
        else if(option4.isChecked() && Objects.equals(correct_answer, "4")){
            setCorrect_answer_view();
        }
        else{
            setIncorrect_answer_view();
        }
    }

    public void playAnimation(){
        LottieAnimationView lottie = findViewById(R.id.lottieAnimationView);
        if (!lottie.isAnimating()) {
            // Play the animation once
            lottie.playAnimation();
        }
        lottie.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }
}
