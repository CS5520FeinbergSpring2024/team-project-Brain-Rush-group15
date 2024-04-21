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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Random;

import edu.northeastern.brainrush.match.Host;
import edu.northeastern.brainrush.match.MatchRoom;
import edu.northeastern.brainrush.match.QuestionPage;
import edu.northeastern.brainrush.match.UserRole;
import edu.northeastern.brainrush.model.Question;

public class CompeteActivity extends AppCompatActivity {

    private Spinner subjectSpinner;
    private Button match_button;
    private boolean matching;
    private DatabaseReference matchRef;
    private DatabaseReference questionRef;
    private DatabaseReference userRef;
    private String roomId;
    private String currentId;
    private String subject;


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

        currentId = getIntent().getExtras().getString("id");

        matching = false;
//        Log.v("onCreate", "roomid is set to null");
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
                    matchClicked(subject);
                }
            }
        });

        matchRef = FirebaseDatabase.getInstance().getReference().child("MatchPool");
        questionRef = FirebaseDatabase.getInstance().getReference().child("Question");
        userRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentId);

        userRef.child("Matching").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.getValue() != null) {
                        matchRef.child("Match").child(snapshot.getValue().toString()).removeValue();
                        userRef.child("Matching").removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("ActivityLifecycle", "onResume() called " + String.valueOf(roomId==null) + String.valueOf(!matching));
        if(roomId != null){
            // when the match is correctly finished
            match_button.getBackground().setTint(Color.CYAN);
            match_button.setText("Match");
            exitClicked();
            roomId = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(matching){
            exitClicked();
        }
    }


    public void matchClicked(String subject){
        // check if have host
        matching = true;
        matchRef.child("Host").child(currentId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                matchRef.child("Host").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // yes, find host, transactional change, create room
                            String hostName = null;
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                Host host = dataSnapshot.getValue(Host.class);
                                if(host !=null && host.getStatus().equals("Waiting") && host.getSubject().equals(subject)) {
                                    hostName = dataSnapshot.getKey();
                                    break;
                                }
                            }

                            if(hostName == null || hostName.equals(currentId)){
                                // enter queue
                                addHost(currentId, subject);
                                return;
                            }
                            String finalHostName = hostName;
                            matchRef.child("Host").child(finalHostName).runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    if(mutableData == null){
                                        addHost(currentId, subject);
                                        return Transaction.abort();
                                    }

                                    MatchRoom matchingRoom = new MatchRoom(finalHostName, currentId);
                                    String newId = finalHostName + "_" + currentId;
                                    matchRef.child("Match").child(newId).setValue(matchingRoom);
                                    getAndSetQuestions();
                                    mutableData.child("status").setValue(newId);
                                    roomId = newId;
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                    Log.v("trans", "Transaction completed");
                                    Intent intent = new Intent(CompeteActivity.this, QuestionPage.class);
                                    intent.putExtra("roomId", roomId);
                                    intent.putExtra("role", UserRole.Guest.getValue());
                                    intent.putExtra("userId", currentId);
                                    startActivity(intent);
                                }
                            });


                        } else {
                            // no, add to host, listen to change
                            addHost(currentId, subject);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.v("Check host list", "failed");
                    }
                });
            }
        });
    }

    public void addHost(String key, String subject){
        Host host = new Host(subject);
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
                                matchRef.child("Match").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                userRef.child("Matching").setValue(roomId);
                                matchRef.child("Host").child(key).removeValue();
                                Intent intent = new Intent(CompeteActivity.this, QuestionPage.class);
                                intent.putExtra("roomId", roomId);
                                intent.putExtra("role", UserRole.Host.getValue());
                                intent.putExtra("userId", currentId);
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
            matchRef.child("Host").child(currentId).removeValue();
        }
        else {
            userRef.child("Matching").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.getValue() != null) {
                            matchRef.child("Match").child(snapshot.getValue().toString()).removeValue();
                            userRef.child("Matching").removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
                Log.v("q", String.valueOf(snapshot.exists()));
                if (!snapshot.exists()) {
                    questions = getDummyQuestions();
                }
                else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Question q = dataSnapshot.getValue(Question.class);
                        if(subject.equals("Random") || q.subject.equals(subject)) {
                            questions.add(q);
                            Log.v("q", "added");
                        }
                    }
                }
                Random random = new Random();
                for (int i = questions.size() - 1; i > 0; i--) {
                    int j = random.nextInt(i + 1);
                    Question temp = questions.get(i);
                    questions.set(i, questions.get(j));
                    questions.set(j, temp);
                }
                for(int i = 0; i < Math.min(num_q, questions.size()); i++) {
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