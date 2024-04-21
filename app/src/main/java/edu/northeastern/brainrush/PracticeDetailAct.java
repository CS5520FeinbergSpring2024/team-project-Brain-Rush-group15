package edu.northeastern.brainrush;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.brainrush.model.Question;
import edu.northeastern.brainrush.model.User;

public class PracticeDetailAct extends AppCompatActivity {
    private Question quizz;//le quizz

    private String uid;
    private User user;//le user


    private TextView Question_body_TV;//the question prompt

    private TextView correct_indicator_TV;//only appear when correct answer is selected
    private TextView review_TV;//only appear when correct answer is selected

    //le choices group is defined be;pw
    private RadioButton Choice_A_TV;
    private RadioButton Choice_B_TV;
    private RadioButton Choice_C_TV;
    private RadioButton Choice_D_TV;

    private RadioGroup Question_radio;

    private Button backButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_radio);

        backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra("removedQuestionId", quizz.id); // Assuming 'quizz' has an 'id' field
            setResult(RESULT_OK, data);
            finish();
        });

        Intent intent = getIntent();
        uid = intent.getStringExtra("userid");
        Log.d("read_intent", "Userid is " + uid);

        String qid = intent.getStringExtra("questionId");
        Log.d("read_intent", "Questionname is " + qid);

        //user = intent.getParcelableExtra("user");


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference questionRef = database.child("Question").child(qid);
        DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();
        Log.d("Useref_before_connection", "uid is " + uid);
        DatabaseReference Useref = database2.child("User").child(uid);
        questionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                quizz = dataSnapshot.getValue(Question.class);
                Log.d("FirebaseQuestion", "now fetching questions");
                if (quizz != null) {
                    Log.d("FirebaseQuestion", " fetching questions succeed!");
                    Log.d("FirebaseQuestion", " fetchied questionid is: " + quizz.id);
                    Log.d("FirebaseQuestion", " fetchied q correctAnswer is: " + quizz.correctAnswer);


                    updateUIWithQuestionData(); // Update all question related UI elements here
                }
                else{
                    Log.d("FirebaseQuestion", " wtf, question is null now");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadQuestion:onCancelled", databaseError.toException());
            }
        });

        Useref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Log.d("Firebase_User", "Thanks god, user loaded");
                    Log.d("Firebase_User", "And user name is " + user.getName());
                    Log.d("Firebase_User", "And user id is " + user.getDate_created());


                    updateUIWithUserData(); // Update all user related UI elements here
                }
                else{
                    Log.e("Firebase_User", "WTF user not loading");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadUser:onCancelled", databaseError.toException());
            }
        });

        Question_body_TV = findViewById(R.id.Question_Body);
        correct_indicator_TV = findViewById(R.id.correct_answer_indicator);
        review_TV = findViewById(R.id.Review_text);
        Question_radio = findViewById(R.id.Question_group);
        Choice_A_TV = findViewById(R.id.Question_choice_A);
        Choice_B_TV = findViewById(R.id.Question_choice_B);
        Choice_C_TV = findViewById(R.id.Question_choice_C);
        Choice_D_TV = findViewById(R.id.Question_choice_D);

        correct_indicator_TV.setVisibility(View.GONE);//gone by default
        review_TV.setVisibility(View.GONE);//stay tuned for future implementation
        checkIfQuestionCompleted(uid, qid);



        Question_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Assuming the correct answer index is 0-based and matches the order of choices
                int correctAnswerIndex;
                String correctAnswerIndex_string = quizz.correctAnswer;//with their infinite wisdom, the type is favored to string than int, I hope oneday I will know why
                Log.d("Question", "the correct answer is" + correctAnswerIndex_string);
                correctAnswerIndex = Integer.parseInt(correctAnswerIndex_string)-1;


                RadioButton correctButton = (RadioButton) findViewById(group.getChildAt(correctAnswerIndex).getId());

                if (checkedId == correctButton.getId()) {
                    correct_indicator_TV.setVisibility(View.VISIBLE);
                    // Disable all RadioButton elements, lock once the correct answer is selected
                    //here to add exp to user
                    backButton.setVisibility(View.VISIBLE); // Make the button visible


                    for (int i = 0; i < group.getChildCount(); i++) {
                        group.getChildAt(i).setEnabled(false);
                    }
                    onCorrectAnswerSelected(); // Trigger the callback
                    completedQ(qid, uid);//do the putting
                } else {
                    correct_indicator_TV.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUIWithQuestionData() {
        Question_body_TV.setText(quizz.context);
        Choice_A_TV.setText(quizz.choice1);
        Choice_B_TV.setText(quizz.choice2);
        Choice_C_TV.setText(quizz.choice3);
        Choice_D_TV.setText(quizz.choice4);
    }

    private void updateUIWithUserData() {
        // let see if i can do anything about it...
    }

    public void completedQ(String questionId,String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
            // Get a reference to the user's "CompleteList"
            DatabaseReference userRef = database.getReference("User").child(userId).child("questions_answered");

            // Add the question ID to the "CompleteList"
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a list to hold the current data
                List<String> questionsAnswered;
                if (dataSnapshot.exists()) {
                    // If the data exists, deserialize it to a List object
                    questionsAnswered = dataSnapshot.getValue(new GenericTypeIndicator<List<String>>() {});
                } else {
                    // Otherwise, initialize an empty list
                    questionsAnswered = new ArrayList<>();
                }

                // Add the new question ID to the list
                questionsAnswered.add(questionId);

                // Save the updated list back to Firebase
                userRef.setValue(questionsAnswered).addOnSuccessListener(aVoid -> {
                    Log.d("UpdateCompleteList", "Question ID added to CompleteList successfully.");
                }).addOnFailureListener(e -> {
                    Log.e("UpdateCompleteList", "Failed to add Question ID to CompleteList.", e);
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "loadUser:onCancelled", databaseError.toException());
            }
        });
    }


    private void checkIfQuestionCompleted(String userId, String questionId) {
        DatabaseReference completeListRef = FirebaseDatabase.getInstance().getReference("User")
                .child(userId)
                .child("questions_answered");

        // Fetch the entire list of answered questions
        completeListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isCompleted = false;

                if (dataSnapshot.exists()) {
                    // Deserialize the snapshot into a List of Strings
                    List<String> completedQuestions = dataSnapshot.getValue(new GenericTypeIndicator<List<String>>() {});
                    // Check if the list contains the specific questionId
                    if (completedQuestions != null && completedQuestions.contains(questionId)) {
                        isCompleted = true;
                    }
                }

                // Update the visibility of reviewText based on whether the question is completed
                TextView reviewText = findViewById(R.id.Review_text);
                if (isCompleted) {
                    reviewText.setVisibility(View.VISIBLE);
                } else {
                    reviewText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("checkIfCompleted", "Error checking complete list: " + databaseError.getMessage());
                // Optionally handle the cancellation here, e.g., by showing an error message
            }
        });
    }

    public void onCorrectAnswerSelected() {//animation related code
        if (user == null) {
            Log.e("PracticeDetailAct", "Attempt to use null User object.");
            return;  // Stop further execution if user is null
        }
        Log.d("Question", "Current User expo, before answer " + user.getExperience());

        user.addExperience(5);
        Log.d("Question", "Current User expo, afyer answer " + user.getExperience());
        //time for database
        String path = "User/" + uid + "/experience";
        Log.d("Userpath", "Current User path is" + path);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(path);
        userRef.setValue(user.getExperience());

    }
}
//        ImageView animationView = findViewById(R.id.animation_view);
//        animationView.setVisibility(View.VISIBLE); // Make the ImageView visible
//        AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
//        animation.start(); // Start the animation
//        int totalDuration = 0;
//        for (int i = 0; i < animation.getNumberOfFrames(); i++) {
//            totalDuration += animation.getDuration(i);
//        }

//        new Handler().postDelayed(() -> {
//            animationView.setVisibility(View.INVISIBLE);
//        }, totalDuration);


