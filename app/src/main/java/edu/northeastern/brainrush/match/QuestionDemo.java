package edu.northeastern.brainrush.match;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.northeastern.brainrush.R;

public class QuestionDemo extends AppCompatActivity {

    DatabaseReference roomRef;
    DatabaseReference heartBeatRef;
    DatabaseReference opponentHeartBeatRef;
    List<String> questions;
    int currentQuestion;
    TextView question;
    RadioGroup choices;

    String userName;
    UserRole role;
    int heartbeat = 0;
    int opponentHeartBeat = 0;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_question_question_page);

        role = UserRole.fromValue(getIntent().getIntExtra("role", -1));
        String roomId = getIntent().getStringExtra("roomId");
        userName = getIntent().getStringExtra("userName");

        question = findViewById(R.id.question_header);
        choices = findViewById(R.id.options_container);
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitClicked(v);
            }});


        roomRef = FirebaseDatabase.getInstance().getReference("MatchPool").child("Match").child(roomId);
        if(role.equals(UserRole.Host)){
            heartBeatRef = roomRef.child("host_heartBeat");
            opponentHeartBeatRef = roomRef.child("guest_heartBeat");
        }
        else{
            heartBeatRef = roomRef.child("guest_heartBeat");
            opponentHeartBeatRef = roomRef.child("host_heartBeat");
        }

        questions = new ArrayList<>();
        fetchQuestions();
        currentQuestion = 0;

        opponentHeartBeatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null){
                    Log.v("timer", snapshot.toString());
                    return;
                }
                opponentHeartBeat = Integer.parseInt(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TimerTask heartBeatTask = new TimerTask() {
            @Override
            public void run() {
                heartbeat++;
                heartBeatRef.setValue(heartbeat, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                             Log.e("Firebase", "Error updating counter value: " + error.getMessage());
                        }
                    }
                });

                if(Math.abs(opponentHeartBeat - heartbeat) > 10){
                    handleDisconnect();
                    timer.cancel();
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(heartBeatTask, 0, 1000);
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
                            //host should record correct data
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
        int selectedRadioButtonId = choices.getCheckedRadioButtonId();
        Toast.makeText(this, "Option " + selectedRadioButtonId + "is selected", Toast.LENGTH_SHORT).show();
        if(currentQuestion == questions.size() - 1){
            // check and conclude and exit
            question.setText("Completed");
            if(role.equals(UserRole.Host)) {
                processResult();
            }
            showResultDialog();
        }
        else if(currentQuestion < questions.size() - 1){
            currentQuestion++;
            // set answer to database
            question.setText(questions.get(currentQuestion));
        }
    }

    public void processResult(){

    }

    public void handleDisconnect(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDisconnectedDialog();
            }
        });
    }

    public void showResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ConstraintLayout layout = new ConstraintLayout (this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.match_result, null);

        builder.setView(dialogView).setCancelable(false);

        Button finish = dialogView.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDisconnectedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ConstraintLayout layout = new ConstraintLayout (this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.match_disconnected, null);

        builder.setView(dialogView).setCancelable(false);

        Button finish = dialogView.findViewById(R.id.exit);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
