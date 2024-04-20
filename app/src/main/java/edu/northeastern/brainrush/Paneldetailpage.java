package edu.northeastern.brainrush;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.brainrush.model.Question;
import edu.northeastern.brainrush.model.User;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Paneldetailpage extends AppCompatActivity {
    private Question question;
    private String userid;
    private TextView QSubjectTV, QbodyTV, C1TV, C2TV, C3TV, C4TV, CA;
    private Button likeButton, noLikeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        initializeViews();
        setupButtons();

        // Retrieve question ID from intent and fetch question details
        String questionId = getIntent().getStringExtra("questionId");
        if (questionId != null) {
            Log.d("Paneldetailpage_yes", "question ID provided");

            fetchQuestionDetails(questionId);
        } else {
            Log.e("Paneldetailpage", "No question ID provided");
        }

        userid = getIntent().getStringExtra("userid");
        if (userid != null) {
            Log.d("Paneldetailpage_yes", "user ID provided" + userid);

        } else {
            Log.e("Paneldetailpage", "No user ID provided");
        }
    }

    private void initializeViews() {
        QSubjectTV = findViewById(R.id.Subject);
        QbodyTV = findViewById(R.id.Qbody);
        C1TV = findViewById(R.id.Choice1);
        C2TV = findViewById(R.id.Choice2);
        C3TV = findViewById(R.id.Choice3);
        C4TV = findViewById(R.id.Choice4);
        CA = findViewById(R.id.CorrectAnswer);
        likeButton = findViewById(R.id.Like);
        noLikeButton = findViewById(R.id.Nolike);
    }

    private void setupButtons() {
        likeButton.setOnClickListener(v -> {
            if (question != null) {
                setResultAndFinish(question.id, true, userid);
                updatereviews(userid, question.id);//code regard of like
            } else {
                Toast.makeText(this, "Question data not loaded yet.", Toast.LENGTH_SHORT).show();
            }
        });

        noLikeButton.setOnClickListener(v -> {
            if (question != null) {
                setResultAndFinish(question.id, false, userid);
                updatereviews(userid, question.id);//code regard of like

            } else {
                Toast.makeText(this, "Question data not loaded yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatereviews(String userId, String questionId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User")
                .child(userId)
                .child("reviewed").child(questionId);
        // Fetch the current list, then update it
        ref.setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Question added to reviewed successfully.");
            } else {
                Log.e("Firebase", "Failed to add question to reviewed.", task.getException());
            }
        });
    }

    private void fetchQuestionDetails(String questionId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PendingQuestion").child(questionId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    question = dataSnapshot.getValue(Question.class);
                    updateUI();
                } else {
                    Log.d("fetchQuestionById", "No question found with ID: " + questionId);
                    Toast.makeText(Paneldetailpage.this, "Question not found", Toast.LENGTH_SHORT).show();
                    finish();  // Consider closing the activity if essential data isn't available
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("fetchQuestionById", "Error fetching data", databaseError.toException());
                Toast.makeText(Paneldetailpage.this, "Error loading question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (question != null) {
            QSubjectTV.setText("Subject: "+ question.subject);
            QbodyTV.setText("Question body: " + question.context);
            C1TV.setText("1. " + question.choice1);
            C2TV.setText("2. " + question.choice2);
            C3TV.setText("3. " + question.choice3);
            C4TV.setText("4. " + question.choice4);
            CA.setText("Correct answer: " + question.correctAnswer);
        }
    }

    private void setResultAndFinish(String questionId, boolean liked, String uid) {
        //put them out of pending if likes >=2
        if (liked) {
            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("PendingQuestion")
                    .child(questionId)
                    .child("likes");

            // Set the user's like as true at the specific uid node
            likesRef.child(uid).setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("UpdateLikes", "User " + uid + " liked question " + questionId);

                    // Check all likes after updating
                    likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long count = dataSnapshot.getChildrenCount();
                            if (count > 1) {  // More than one like
                                DatabaseReference pendingQuestionRef = FirebaseDatabase.getInstance().getReference("PendingQuestion").child(questionId);
                                DatabaseReference officialQuestionsRef = FirebaseDatabase.getInstance().getReference("Question");
                                moveQuestionToOfficial(pendingQuestionRef, officialQuestionsRef, questionId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", "Error checking likes", databaseError.toException());
                        }
                    });
                } else {
                    Log.e("UpdateLikes", "Failed to like question: " + task.getException().getMessage());
                }
            });
        }
//        if (liked) {
//            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("PendingQuestion")
//                    .child(questionId)
//                    .child("likes");
//
//            // Set the user's like as true at the specific uid node
//// Add the user ID to the likes list
//            likesRef.push().setValue(uid).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Log.d("UpdateLikes", "User " + uid + " liked question " + questionId);
//
//                    // Check all likes after updating
//                    likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            long count = dataSnapshot.getChildrenCount();
//                            if (count > 1) {  // More than one like
//                                DatabaseReference pendingQuestionRef = FirebaseDatabase.getInstance().getReference("PendingQuestion").child(questionId);
//                                DatabaseReference officialQuestionsRef = FirebaseDatabase.getInstance().getReference("Question");
//                                moveQuestionToOfficial(pendingQuestionRef, officialQuestionsRef, questionId);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Log.e("Firebase", "Error checking likes", databaseError.toException());
//                        }
//                    });
//                } else {
//                    Log.e("UpdateLikes", "Failed to like question: " + task.getException().getMessage());
//                }
//            });
//        }



        Intent data = new Intent();
        data.putExtra("questionId", questionId);
        data.putExtra("liked", liked);
        setResult(RESULT_OK, data);
        finish();
    }


    private void moveQuestionToOfficial(DatabaseReference fromRef, DatabaseReference toRef, String questionId) {
        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Take the whole question object
                Object question = dataSnapshot.getValue();
                // Set the question object to the new location
                toRef.child(questionId).setValue(question).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Remove from pending questions once successfully added to official questions
                        fromRef.removeValue().addOnCompleteListener(removeTask -> {
                            if (removeTask.isSuccessful()) {
                                Log.d("Firebase", "Question moved to official and removed from pending successfully.");
                            } else {
                                Log.e("Firebase", "Failed to remove question from pending: " + removeTask.getException().getMessage());
                            }
                        });
                    } else {
                        Log.e("Firebase", "Failed to move question to official: " + task.getException().getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error moving question to official", databaseError.toException());
            }
        });
    }
}

//public class Paneldetailpage extends AppCompatActivity {
//    private Question question;
//    private TextView QSubjectTV, QbodyTV, C1TV, C2TV, C3TV, C4TV;
//    private Button likeButton, noLikeButton;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_question_details);
//
//        Button likeButton = findViewById(R.id.Like);
//        Button noLikeButton = findViewById(R.id.Nolike);
//
//        likeButton.setOnClickListener(v -> {
//            setResultAndFinish(question.id, true);
//        });
//
//        noLikeButton.setOnClickListener(v -> {
//            setResultAndFinish(question.id, false);
//        });
//
//        TextView QSubjectTV = findViewById(R.id.Subject);
//        TextView QbodyTV = findViewById(R.id.Qbody);
//        TextView C1TV = findViewById(R.id.Choice1);
//        TextView C2TV = findViewById(R.id.Choice2);
//        TextView C3TV = findViewById(R.id.Choice3);
//        TextView C4TV = findViewById(R.id.Choice4);
//
//        // Get the passed question object
//        Intent intent = getIntent();
//        String questionId = intent.getStringExtra("questionid");
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Question").child(questionId);
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    question = dataSnapshot.getValue(Question.class);
//
//                    // Move the set text methods inside onDataChange
//                    if (question != null) {
//                        QSubjectTV.setText(question.subject);
//                        QbodyTV.setText(question.context);
//                        C1TV.setText(question.choice1);
//                        C2TV.setText(question.choice2);
//                        C3TV.setText(question.choice3);
//                        C4TV.setText(question.choice4);
//                    }
//                } else {
//                    Log.d("fetchQuestionById", "No question found with ID: " + questionId);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("fetchQuestionById", "Error fetching data", databaseError.toException());
//            }
//        });
//    }
//
//    private void setResultAndFinish(String questionId, boolean liked) {
//        Intent data = new Intent();
//        data.putExtra("questionId", questionId);
//        data.putExtra("liked", liked);
//        setResult(RESULT_OK, data);
//        finish();
//    }
//}