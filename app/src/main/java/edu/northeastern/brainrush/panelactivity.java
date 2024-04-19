package edu.northeastern.brainrush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Collections;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.northeastern.brainrush.model.Question;
import edu.northeastern.brainrush.model.User;

public class panelactivity extends AppCompatActivity {
    RecyclerView quizzview;
    List<Question> quizzlist;
    private quizz_adapter adapter;
    private ActivityResultLauncher<Intent> detailActivityResultLauncher;

    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");
        uid = user.getName();
        quizzview = findViewById(R.id.quizzlist);
        quizzlist = new ArrayList<>();
        adapter = new quizz_adapter
                (quizzlist, this, detailActivityResultLauncher, uid);
        quizzview.setHasFixedSize(true);
        quizzview.setLayoutManager(new LinearLayoutManager(this));
        quizzview.setAdapter(adapter);

        // Initialize the ActivityResultLauncher
        detailActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String questionId = result.getData().getStringExtra("questionId");
                        boolean liked = result.getData().getBooleanExtra("liked", false);
                        //if (!liked) {
                            removeQuestionFromList(questionId);
                        //}
                    }
                }
        );

        fetchQuestionsFromFirebase();
    }

    private void fetchQuestionsFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(uid).child("reviewed");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot reviewedSnapshot) {
                Set<String> reviewedIds = new HashSet<>();
                for (DataSnapshot snapshot : reviewedSnapshot.getChildren()) {
                    reviewedIds.add(snapshot.getKey());
                }
                Log.d("SetDebug", "Current state of the set: " + reviewedIds.toString());

                fetchUnreviewedRandomQuestions(reviewedIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching reviewed questions", databaseError.toException());
            }
        });
    }

//    private void fetchQuestionsFromFirebase() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Question");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<String> keys = new ArrayList<>();
//                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
//                    keys.add(keyNode.getKey());
//                }
//                fetchRandomQuestions(keys);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle possible errors
//            }
//        });
//    }


    private void removeQuestionFromList(String questionId) {
        Iterator<Question> iterator = quizzlist.iterator();
        while (iterator.hasNext()) {
            Question question = iterator.next();
            if (question.id.equals(questionId)) {
                iterator.remove();
                quizzview.getAdapter().notifyDataSetChanged();
                break;
            }
        }
    }

    private void fetchUnreviewedRandomQuestions(Set<String> reviewedIds) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Question");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> unreviewedKeys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    if (!reviewedIds.contains(keyNode.getKey())) {
                        unreviewedKeys.add(keyNode.getKey());
                    }
                }
                selectRandomQuestions(unreviewedKeys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void selectRandomQuestions(List<String> unreviewedKeys) {
        Collections.shuffle(unreviewedKeys);
        int count = Math.min(unreviewedKeys.size(), 5); // Fetch up to 5 questions
        quizzlist.clear();

        for (int i = 0; i < count; i++) {
            DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference("Question").child(unreviewedKeys.get(i));
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null) {
                        quizzlist.add(question);
                    }
                    if (quizzlist.size() == count) {
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors just in case
                }
            });
        }

        if (quizzlist.isEmpty()) {
            updateUI(); // Update UI even if no questions were added to handle empty state
        }
    }


    private void fetchRandomQuestions(List<String> keys) {
        Collections.shuffle(keys);
        int count = Math.min(keys.size(), 5); // Fetch up to 5 questions
        quizzlist.clear();

        for (int i = 0; i < count; i++) {
            DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference("Question").child(keys.get(i));
            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);
                    quizzlist.add(question);
                    if (quizzlist.size() == count) {
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors just in case
                }
            });
        }
    }
//    private void updateUI() {
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        } else {
//            adapter = new quizz_adapter(quizzlist, this, detailActivityResultLauncher, uid);
//            quizzview.setAdapter(adapter);
//        }
//    }
    private void updateUI() {
        quizzview.setHasFixedSize(true);

        quizzview.setLayoutManager(new LinearLayoutManager(this));

        quizzview.setAdapter(new quizz_adapter(quizzlist, this, detailActivityResultLauncher, uid));
    }
}


