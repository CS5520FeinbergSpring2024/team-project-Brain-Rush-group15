package edu.northeastern.brainrush;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.List;

import edu.northeastern.brainrush.model.Question;
import edu.northeastern.brainrush.model.User;

public class learn_page extends AppCompatActivity implements OnCorrectAnswerListener {//animation related code

    RecyclerView practice_view;//

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
        user = intent.getParcelableExtra("user");

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String selectedCategory = extras.getString("subjects");
//            //Setting up the date selected from the previous page
//            user = (User) extras.get("user");
//        }
//        else{
//            Log.d("NoExtraexception", "Didn't received extras!");
//
//        }


//        Intent intent = getIntent();
//        String selectedCategory = intent.getStringExtra("selectedCategory");
//        user = intent.getParcelableExtra("user");

        questionList = new ArrayList<>();//value holder for the data fetched from the query, now not gonna use
        questionList2 = new ArrayList<>();//value holder for the data fetched from the query

        practice_view = findViewById(R.id.practice_list);
        practice_view.setHasFixedSize(true);
        practice_view.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter
        final practice_quizz_adapter adapter = new practice_quizz_adapter(questionList2, this, this);//animation related code

        //the database query step, looking for questions
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference questionsRef = databaseRef.child("Questions");//questions --> Questions

        DatabaseReference questionsRef = databaseRef.child("Question");//questions --> Questions

        //String desiredSubject = selectedCategory; // The subject that is wanted by the user
        //Query query = questionsRef.orderByChild("subjects").equalTo(selectedCategory);

        Query query = questionsRef.orderByChild("subject").equalTo(selectedCategory);//subjects --> subject
        //------------------------- add condition here,only like >=2 will be added-----------------------------------
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Question question = snapshot.getValue(Question.class);
//                    if (question != null && question.getLike() >= 2) {
//                        // Add to your list or handle the question object as needed
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("Questionload", "The read failed: " + databaseError.getCode());
//            }

        //------------------------------process is competed above ----------------------------------------------------
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
    }

    @Override
    public void onCorrectAnswerSelected() {//animation related code
        Log.e("Question", "Current User expo, before answer"+ user.getExperience());

        user.addExperience(5);
        Log.e("Question", "Current User expo, afyer answer"+ user.getExperience());
        //time for database
        String path = "User/"+user.getName()+"/experience";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(path);
        userRef.setValue(user.getExperience());


        ImageView animationView = findViewById(R.id.animation_view);
        animationView.setVisibility(View.VISIBLE); // Make the ImageView visible
        AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
        animation.start(); // Start the animation
        int totalDuration = 0;
        for (int i = 0; i < animation.getNumberOfFrames(); i++) {
            totalDuration += animation.getDuration(i);
        }

        new Handler().postDelayed(() -> {
            animationView.setVisibility(View.INVISIBLE);
        }, totalDuration);
    }
}

//        quizzlist = new ArrayList<>();
//
//        int dummylikeness = 5;
//        List<String> sampleTitle = new ArrayList<>();
//        sampleTitle.add("Biology");
//        sampleTitle.add("zig zig");
//        sampleTitle.add("mophrism");
//        sampleTitle.add("Nlists");
//        sampleTitle.add("Meme");
//
//        for(String titles: sampleTitle){
//            quizzlist.add(new Quizz(titles, dummylikeness++));
//        }
        //this should set up the basic list for quizz





//        practice_view = findViewById(R.id.practice_list);
//
//        practice_view.setHasFixedSize(true);
//
//        practice_view.setLayoutManager(new LinearLayoutManager(this));
//
//        practice_view.setAdapter(new practice_quizz_adapter(questionList, this));

