package edu.northeastern.brainrush.match;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.brainrush.R;

public class QuestionDemo extends AppCompatActivity {

    DatabaseReference roomRef;
    List<String> questions;
    int currentQuestion;
    TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_question);

        question = findViewById(R.id.question);
        questions = new ArrayList<>();
        String roomId = getIntent().getStringExtra("roomId");
        roomRef = FirebaseDatabase.getInstance().getReference("MatchPool").child("Match").child(roomId);
        fetchQuestions();
        currentQuestion = 0;


    }

    private void fetchQuestions(){
        roomRef.child("Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot data: snapshot.getChildren()){
                        if(data.getValue() != null) {
                            String content = data.getValue().toString();
                            questions.add(content);
                        }
                    }
                    if(questions.size() > 0) {
                        question.setText(questions.get(0));
                    }
                    roomRef.child("Questions").removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void submitClicked(View v){
        if(currentQuestion == questions.size() - 1){
            // check and conclude and exit
            question.setText("Completed");
            processResult();
            finish();
        }
        else{
            currentQuestion++;
            question.setText(questions.get(currentQuestion));
        }
    }

    public void processResult(){

    }
}
