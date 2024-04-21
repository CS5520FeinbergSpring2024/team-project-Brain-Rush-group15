package edu.northeastern.brainrush;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
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
import java.util.Iterator;
import java.util.List;

import edu.northeastern.brainrush.model.Question;
import edu.northeastern.brainrush.model.User;

public class learn_page extends AppCompatActivity implements OnItemClickListener {//animation related code
    private static final int REQUEST_CODE_REMOVE_QUESTION = 1;
    RecyclerView practice_view;//

    private String uid;
    private User user;
    private String selectedCategory;

    public List<Question_dummy> questionList;//value holder for the data fetched from the query
    public List<Question> questionList2;//the true value holder for the data fetched from the query



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_page);

        //retrieved data from the previous activity
        Intent intent = getIntent();
        String selectedCategory = intent.getStringExtra("selectedCategory");
        uid =intent.getStringExtra("id");
        user = intent.getParcelableExtra("user");


        questionList = new ArrayList<>();//value holder for the data fetched from the query, now not gonna use
        questionList2 = new ArrayList<>();//value holder for the data fetched from the query

        practice_view = findViewById(R.id.practice_list);
        practice_view.setHasFixedSize(true);
        practice_view.setLayoutManager(new LinearLayoutManager(this));

        // ----------------------------------------Initialize the adapter---------------------------------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        final practice_quizz_adapter adapter = new practice_quizz_adapter(questionList2, this, user.getName(), this);
        adapter.setAdapterDataObserver(new practice_quizz_adapter.AdapterDataObserver() {
            @Override
            public void onListEmpty() {
                showCompletionDialog();
            }
        });
        //the database query step, looking for questions
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference questionsRef = databaseRef.child("Question");//questions --> Questions


        Query query = questionsRef.orderByChild("subject").equalTo(selectedCategory).limitToFirst(5); ;//subjects --> subject

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionList2.clear(); // Clear existing data to avoid duplicates
                int questionCount = (int) dataSnapshot.getChildrenCount(); // Get the count of questions
                Log.d("QuestionCount", "Number of questions fetched: " + questionCount);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Question_dummy question = snapshot.getValue(Question_dummy.class);
                    Question question = snapshot.getValue(Question.class);
                    //questionList.add(question); // Add fetched question to the list
                    questionList2.add(question); // Add fetched question to the list
                }
                Log.d("Question", "questionlist2 had been made");
                Log.d("Question", "return the size of the question list" + questionList2.size());


                // Set adapter here and notify data set changed
                practice_view.setAdapter(adapter); // Set the adapter with the updated questionList
                adapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log or handle database access errors
                Log.e("Question", "onCancelled: ", databaseError.toException());
            }
        });


        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!questionList2.isEmpty()) {
                    // Show alert if  still questions left
                    new AlertDialog.Builder(learn_page.this)
                            .setTitle("Exit Confirmation")
                            .setMessage("Are you sure you want to exit? All progress will be lost.")
                            .setPositiveButton("Exit", (dialog, which) -> {
                                // if still User chooses to exit, call finish
                                finish();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                // if User =cancel, dismiss the dialog and do nothing
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    // If the list is empty, just perform the normal back action
                    setEnabled(false);  // Disable the callback
                    onBackPressed();
                }
            }
        });
    }


    private void removeQuestionFromList(String questionId) {
        Iterator<Question> iterator = questionList2.iterator();
        while (iterator.hasNext()) {
            Question question = iterator.next();
            if (question.id.equals(questionId)) {
                iterator.remove();
                practice_view.getAdapter().notifyDataSetChanged();
                break;
            }
        }
        practice_view.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onItemClick(String questionId, String userId) {
        Intent intent = new Intent(this, PracticeDetailAct.class);
        intent.putExtra("questionId", questionId);
        intent.putExtra("userid", uid);
        intent.putExtra("user", user);

        startActivityForResult(intent, REQUEST_CODE_REMOVE_QUESTION);
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You have finished all the questions.")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REMOVE_QUESTION && resultCode == RESULT_OK) {
            String removedQuestionId = data.getStringExtra("removedQuestionId");
            removeQuestionFromList(removedQuestionId);
            if (questionList2.isEmpty()) {
                showCompletionDialog();
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (!questionList2.isEmpty()) {
//            // Show an alert dialog if there are still questions left
//            new AlertDialog.Builder(this)
//                    .setTitle("Exit Confirmation")
//                    .setMessage("Are you sure you want to exit? All progress will be lost.")
//                    .setPositiveButton("Exit", (dialog, which) -> {
//                        // User chooses to exit, call super to handle the back press and exit
//                        super.onBackPressed();
//                    })
//                    .setNegativeButton("Cancel", (dialog, which) -> {
//                        // User chooses to cancel, dismiss the dialog and stay in the activity
//                        dialog.dismiss();
//                    })
//                    .show();
//        } else {
//            // If the list is empty, just perform the normal back action
//            super.onBackPressed();
//        }
//    }

}



