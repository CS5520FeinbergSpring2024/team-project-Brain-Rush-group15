package edu.northeastern.brainrush.match;

import android.animation.Animator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import edu.northeastern.brainrush.R;
import edu.northeastern.brainrush.model.Question;

public class QuestionPage extends AppCompatActivity {

    private DatabaseReference roomRef;
    private DatabaseReference heartBeatRef;
    private DatabaseReference userRef;
    private DatabaseReference opponentHeartBeatRef;
    private List<Question> questions;
    private int currentQuestion;
    private TextView question;
    private RadioGroup choices;

    private Button submit;

    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private TextView countDownHeader;
    private LottieAnimationView animationView;

    private AlertDialog resultDialog = null;
    private AlertDialog disconnectDialog = null;

    private String userId;
    private UserRole role;
    private int heartbeat = 0;
    private int opponentHeartBeat = 0;
    private Timer timer;
    private int num_correct;

    private Long start_time;
    private Long finish_time;
    private Long count_down = 180L + 10L;

    private ValueEventListener opponentlistenr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_question);

        role = UserRole.fromValue(getIntent().getIntExtra("role", -1));
        String roomId = getIntent().getStringExtra("roomId");
        userId = getIntent().getStringExtra("userId");

        question = findViewById(R.id.question_header);
        choices = findViewById(R.id.options_container);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        countDownHeader = findViewById(R.id.countdown_header);

        findViewById(R.id.correct_answer_indicator).setVisibility(View.INVISIBLE);
        submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitClicked(v);
            }});
        submit.setClickable(false);

        userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
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
        num_correct = 0;

        opponentlistenr = opponentHeartBeatRef.addValueEventListener(new ValueEventListener() {
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

                if(Math.abs(opponentHeartBeat - heartbeat) > 7){
                    handleDisconnect();
                    opponentHeartBeatRef.removeEventListener(opponentlistenr);
                    timer.cancel();
                }
                if(resultDialog == null) {
                    if (heartbeat >= count_down - 10) {
                        opponentHeartBeatRef.removeEventListener(opponentlistenr);
                        timer.cancel();
                        finish_time = System.currentTimeMillis();
                        if (role.equals(UserRole.Guest)) {
                            roomRef.child("result").child("guest_score").setValue(num_correct);
                            roomRef.child("result").child("guest_end").setValue(finish_time);
                        } else {
                            roomRef.child("result").child("host_score").setValue(num_correct);
                            roomRef.child("result").child("host_end").setValue(finish_time);
                        }

                        if (role.equals(UserRole.Host)) {
                            processResult();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showResultDialog();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countDownHeader.setText(String.format("Time Left: %d", count_down - 10 - heartbeat));
                                if (count_down - 10 - heartbeat < 15) {
                                    countDownHeader.setTextColor(Color.RED);
                                }
                            }
                        });
                    }
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(heartBeatTask, 0, 1000);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(resultDialog != null){
            resultDialog.dismiss();
        }
        if(disconnectDialog != null){
            disconnectDialog.dismiss();
        }
    }

    private void fetchQuestions(){
        roomRef.child("Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot data: snapshot.getChildren()){
                        if(data.getValue() != null) {
                            Question content = data.getValue(Question.class);
                            questions.add(content);
                        }
                    }
                    if(questions.size() > 0) {
                        question.setText(questions.get(0).context);
                        option1.setText(questions.get(0).choice1);
                        option2.setText(questions.get(0).choice2);
                        option3.setText(questions.get(0).choice3);
                        option4.setText(questions.get(0).choice4);
                    }

                    start_time = System.currentTimeMillis();
                    if(role.equals(UserRole.Host)) {
                        roomRef.child("result").child("host_start").setValue(start_time);
                    }
                    else {
                        roomRef.child("result").child("guest_start").setValue(start_time);
                    }

                    submit.setClickable(true);
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
            finish_time = System.currentTimeMillis();
            if(role.equals(UserRole.Guest)) {
                roomRef.child("result").child("guest_score").setValue(num_correct);
                roomRef.child("result").child("guest_end").setValue(finish_time);
            }
            else{
                roomRef.child("result").child("host_score").setValue(num_correct);
                roomRef.child("result").child("host_end").setValue(finish_time);
            }

            if(role.equals(UserRole.Host)) {
                processResult();
            }
            showResultDialog();
        }
        else if(currentQuestion < questions.size() - 1){
            if(option1.isChecked() && questions.get(currentQuestion).correctAnswer.equals("1")){
                num_correct++;
                option1.setChecked(false);
            }
            else if(option2.isChecked() && questions.get(currentQuestion).correctAnswer.equals("2")){
                num_correct++;
                option2.setChecked(false);
            }
            else if(option3.isChecked() && questions.get(currentQuestion).correctAnswer.equals("3")){
                num_correct++;
                option3.setChecked(false);
            }
            else if(option4.isChecked() && questions.get(currentQuestion).correctAnswer.equals("4")){
                num_correct++;
                option4.setChecked(false);
            }
            else if(choices.getCheckedRadioButtonId() == -1){
                return;
            }
            currentQuestion++;
            question.setText(questions.get(currentQuestion).context);
            option1.setText(questions.get(currentQuestion).choice1);
            option2.setText(questions.get(currentQuestion).choice2);
            option3.setText(questions.get(currentQuestion).choice3);
            option4.setText(questions.get(currentQuestion).choice4);
        }
    }

    public void processResult(){
        roomRef.child("result").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    MatchResult result = snapshot.getValue(MatchResult.class);
                    if(result != null && result.checkComplete()){
                        if(result.guest_score > result.host_score ){
                            roomRef.child("winner").setValue(String.valueOf(UserRole.Guest.getValue()));
                        }
                        else if(result.guest_start < result.host_score){
                            roomRef.child("winner").setValue(String.valueOf(UserRole.Host.getValue()));
                        }
                        else{
                            long guest_time = result.guest_end - result.guest_start;
                            long host_time = result.host_end - result.host_start;
                            if(guest_time < host_time){
                                roomRef.child("winner").setValue(String.valueOf(UserRole.Guest.getValue()));
                            }
                            else{
                                roomRef.child("winner").setValue(String.valueOf(UserRole.Host.getValue()));
                            }
                        }
                        roomRef.child("result").removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.match_result, null);

        builder.setView(dialogView).setCancelable(false);

        TextView header = dialogView.findViewById(R.id.result_header_2);
        TextView questionResult = dialogView.findViewById(R.id.question_result);
        TextView timeCost = dialogView.findViewById(R.id.time_consumed);
        header.setText("Wait for another Player");
        animationView = dialogView.findViewById(R.id.lottieAnimationView);
        if(heartbeat >= count_down){
            header.setText("Time out");
        }

        Button finish = dialogView.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        finish.setVisibility(View.INVISIBLE);
        finish.setClickable(false);

        roomRef.child("winner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String winner = (String) snapshot.getValue();
                    if(winner != null && !Objects.equals(winner, "Not Over")){
                        opponentHeartBeatRef.removeEventListener(opponentlistenr);
                        timer.cancel();
                        questionResult.setText(String.format("Correct Answers: %d/5", num_correct));
                        long seconds = (finish_time - start_time) / 1000;
                        long minutes = (seconds % 3600) / 60;
                        long remainingSeconds = seconds % 60;
                        timeCost.setText(String.format("Time: %dmin %ds", minutes, remainingSeconds));
                        if(winner.equals(String.valueOf(role.getValue()))){
                            header.setText("You win!");
                            checkAnimation(true);
                            userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        int score = snapshot.getValue(Integer.class);
                                        score += 20;
                                        userRef.child("score").setValue(score);
                                        finish.setVisibility(View.VISIBLE);
                                        finish.setClickable(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    finish.setVisibility(View.VISIBLE);
                                    finish.setClickable(true);
                                }
                            });
                        }
                        else{
                            checkAnimation(false);
                            header.setText("You Lose.");
                            finish.setVisibility(View.VISIBLE);
                            finish.setClickable(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        resultDialog = builder.create();
        resultDialog.show();
    }

    public void showDisconnectedDialog(){
        if(resultDialog != null){
            resultDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.match_disconnected, null);

        builder.setView(dialogView).setCancelable(false);

        Button finish = dialogView.findViewById(R.id.exit);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentHeartBeatRef.removeEventListener(opponentlistenr);
                timer.cancel();
                finish();
            }
        });

        disconnectDialog = builder.create();
        disconnectDialog.show();
    }

    public void checkAnimation(boolean win) {
        animationView.setVisibility(View.VISIBLE);
        if(win){
            animationView.setAnimation(R.raw.win);
        }
        else{
            animationView.setAnimation(R.raw.fail);
        }
        animationView.playAnimation();

    }

}
