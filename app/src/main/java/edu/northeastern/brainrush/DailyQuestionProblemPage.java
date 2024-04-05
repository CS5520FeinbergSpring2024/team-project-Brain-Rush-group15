package edu.northeastern.brainrush;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import edu.northeastern.brainrush.model.User;

public class DailyQuestionProblemPage extends AppCompatActivity {
    private LottieAnimationView lottie;
    private TextView dateHeader;
    private FirebaseDatabase mDatabase;
    private TextView explanationText;
    private RadioGroup optionContainer;
    private TextView answerIndicator;
    private int question_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_question_question_page);

        // Setting up database
        mDatabase = FirebaseDatabase.getInstance();

        // Setting up the views
        answerIndicator = findViewById(R.id.correct_answer_indicator);
        optionContainer = findViewById(R.id.options_container);
        explanationText = findViewById(R.id.explanation_text);
        dateHeader = findViewById(R.id.date_header);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Setting up the date selected from the previous page
            String date = extras.getString("date");
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
                backToMainPage();
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
                        question_id =  snapshot.child("question_id").getValue(Integer.class);
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
        DatabaseReference dailyQuestionReference = FirebaseDatabase.getInstance().getReference("Question");
        User user = new User("nick");
        user.add_questions_created(1);
        user.add_questions_created(2);
        user.add_daily_question_answered(1);
        dailyQuestionReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                dailyQuestionReference.child(user.getName()).setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("Daily Question", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void dailyQuestionSubmit(View view){

    }

    public void backToMainPage(){
        LottieAnimationView lottie = findViewById(R.id.lottieAnimationView);
        if (!lottie.isAnimating()) {
            // Play the animation once
            lottie.playAnimation();
        }
        startActivity(new Intent(this, MainActivity.class));
    }
}
