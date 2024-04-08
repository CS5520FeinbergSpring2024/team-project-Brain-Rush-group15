package edu.northeastern.brainrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.northeastern.brainrush.model.Question;

public class MakeActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Spinner subjectSpinner, answerSpinner;
    EditText contextEditText;
    EditText choice1EditText, choice2EditText,choice3EditText,choice4EditText;
    String subjectSpin,contextString,choice1String,choice2String,choice3String,choice4String,answerSpin;

    Button makeButton;
    Date currentTime;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);
        // date created;

        currentTime = Calendar.getInstance().getTime();
        animationView = findViewById(R.id.checkAnimation);
        makeDropdown();
        subjectSpinner = findViewById(R.id.subjectSpinner);
        contextEditText = findViewById(R.id.contextEdittext);
        choice1EditText = findViewById(R.id.choice1Edittext);
        choice2EditText = findViewById(R.id.choice2Edittext);
        choice3EditText = findViewById(R.id.choice3Edittext);
        choice4EditText = findViewById(R.id.choice4Edittext);
        answerSpinner = findViewById(R.id.correctAnswerSpinner);
        makeButton = findViewById(R.id.makeButton);



        makeButtonListener();
        checkAnimationListener();

    }

    public void makeDropdown() {
        Spinner spinner = findViewById(R.id.subjectSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_values_noRandom,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);

        Spinner spinner1 = findViewById(R.id.correctAnswerSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.category_value_choice,
                android.R.layout.simple_spinner_item
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
    }

    public void makeButtonListener() {
        makeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectSpin = subjectSpinner.getSelectedItem().toString();
                contextString = contextEditText.getText().toString();
                choice1String = choice1EditText.getText().toString();
                choice2String = choice2EditText.getText().toString();
                choice3String = choice3EditText.getText().toString();
                choice4String = choice4EditText.getText().toString();
                answerSpin = answerSpinner.getSelectedItem().toString();
                ArrayList<String> likes = new ArrayList<>();
                ArrayList<String> dislikes = new ArrayList<>();
                Question question = new Question(subjectSpin, contextString, choice1String,
                        choice2String, choice3String, choice4String, answerSpin, currentTime.toString(),
                        "MultipleChoice", likes, dislikes, "1","5");
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Question").child(String.valueOf(currentTime) + " " + question.creatorId).setValue(question);

                // play the animation after the user make the question
                checkAnimation();
            }
        });
    }

    public void checkAnimation() {
        animationView.setVisibility(View.VISIBLE);
        animationView.setAnimation(R.raw.checkanimation);
        animationView.playAnimation();

    }

    public void checkAnimationListener() {
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                animationView.setVisibility(View.GONE);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                Toast.makeText(getBaseContext(), "Thank you for the contribution!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

}