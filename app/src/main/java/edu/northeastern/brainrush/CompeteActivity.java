package edu.northeastern.brainrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import edu.northeastern.brainrush.match.Host;
import edu.northeastern.brainrush.match.MatchRoom;
import edu.northeastern.brainrush.match.QuestionDemo;
import edu.northeastern.brainrush.match.UserRole;
import edu.northeastern.brainrush.model.Question;

public class CompeteActivity extends AppCompatActivity {

    Spinner subjectSpinner;
    Button match_button;
    boolean matching;
    DatabaseReference matchRef;
    DatabaseReference questionRef;
    String roomId;
    String currentName = "asd"; // temporary, change to intent extra later
    String subject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compete);
        subjectSpinner = findViewById(R.id.category_dropdown);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_values,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        subjectSpinner.setAdapter(adapter);

        matching = false;
        roomId = null;
        match_button = findViewById(R.id.match_button);
        match_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matching){
                    match_button.getBackground().setTint(Color.CYAN);
                    match_button.setText("Match");
                    exitClicked();
                }
                else{
                    match_button.getBackground().setTint(Color.RED);
                    match_button.setText("Cancel");
                    subject = subjectSpinner.getSelectedItem().toString();
                    matchClicked();
                }
            }
        });

        matchRef = FirebaseDatabase.getInstance().getReference().child("MatchPool");
        questionRef = FirebaseDatabase.getInstance().getReference().child("Questions");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(roomId != null){
            // when the match is correctly finished
            match_button.getBackground().setTint(Color.CYAN);
            match_button.setText("Match");
            exitClicked();
            roomId = null;
            matching = false;
        }
    }

    public void matchClicked(){
        // check if have host
        matching = true;
        matchRef.child("Host").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // yes, find host, transactional change, create room
                    String hostName = null;
                    for(DataSnapshot host: snapshot.getChildren()){
                        if(Objects.equals((String)host.child("status").getValue(), "Waiting")) {
                            hostName = host.getKey();
                            break;
                        }
                    }

                    if(hostName == null){
                        // enter queue
                        addHost(currentName);
                        return;
                    }
                    String finalHostName = hostName;
                    matchRef.child("Host").child(finalHostName).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            if(mutableData == null){
                                addHost(currentName);
                                return Transaction.abort();
                            }

                            MatchRoom matchingRoom = new MatchRoom(finalHostName, currentName);
                            String newId = finalHostName + "_" + currentName;
                            matchRef.child("Match").child(newId).setValue(matchingRoom);
                            getAndSetQuestions();
                            mutableData.child("status").setValue(newId);
                            roomId = newId;
                            matchRef.child("Match").child(newId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Check if data exists
                                    if (!dataSnapshot.exists()) {
                                        exitClicked();
                                        Log.d("Guest", "Data has been removed");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle errors
                                    Log.d("TAG", "Error: " + databaseError.getMessage());
                                }
                            });

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            Log.v("trans", "Transaction completed");
                            Intent intent = new Intent(CompeteActivity.this, QuestionDemo.class);
                            intent.putExtra("roomId", roomId);
                            intent.putExtra("role", UserRole.Guest.getValue());
                            intent.putExtra("username", currentName);
                            startActivity(intent);
                        }
                    });


                } else {
                    // no, add to host, listen to change
                    addHost(currentName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("Check host list", "failed");
            }
        });
    }

    public void addHost(String key){
        Host host = new Host();
        matchRef.child("Host").child(key).setValue(host, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Write was successful!
                    matching = true;
                    matchRef.child("Host").child(key).child("status").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String status = (String) snapshot.getValue();
                                if(status == null || status.equals("Waiting")){ return;}
                                roomId = snapshot.getValue().toString();
                                matchRef.child("Match").child(roomId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Check if data exists
                                        if (!dataSnapshot.exists()) {
                                            exitClicked();
                                            Log.d("Host", "Data has been removed");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors
                                        Log.d("TAG", "Error: " + databaseError.getMessage());
                                    }
                                });
                                matchRef.child("Host").child(key).removeValue();

                                Intent intent = new Intent(CompeteActivity.this, QuestionDemo.class);
                                intent.putExtra("roomId", roomId);
                                intent.putExtra("role", UserRole.Host.getValue());
                                intent.putExtra("username", currentName);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    public void exitClicked(){
        if(roomId == null){
            matchRef.child("Host").child(currentName).removeValue();
        }
        else {
            matchRef.child("Match").child(roomId).removeValue();
            roomId = null;
        }
        matching = false;
    }

    public void getAndSetQuestions(){
        int num_q = 5;
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Question> questions = new ArrayList<>();
                if (!snapshot.exists()) {
                    questions = getDummyQuestions();
                }
                else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if(questions.size() >= num_q){ break;}
                        Question q = dataSnapshot.getValue(Question.class);
                        if(q.subject.equals(subject)) {
                            questions.add(q);
                        }
                    }
                }
                for(int i = 0; i < questions.size(); i++) {
                    matchRef.child("Match")
                            .child(roomId).child("Questions").child(String.valueOf(i))
                            .setValue(questions.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public List<Question> getDummyQuestions(){
        List<Question> questions = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Question q = new Question(subject, "dummy " + String.valueOf(i + 1), "1", "2", "3", "4",
                    String.valueOf((i % 5) + 1), (new Date()).toString(), "MultipleChoices", null, null, "dummy", "dummy");
            questions.add(q);
        }
        return questions;
    }
}