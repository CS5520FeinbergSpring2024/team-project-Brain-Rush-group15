package edu.northeastern.brainrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.northeastern.brainrush.model.User;

public class Leaderboard extends AppCompatActivity {
    private DatabaseReference mDatabase;
    ArrayList<User> leaders;

    TextView first, second, third, fourth, fifth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        fifth = findViewById(R.id.fifth);

        leaders = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    User user1 = snapshot.getValue(User.class);
                    leaders.add(user1);
//                    Log.d("Leaderboard", "Value: " + user1.getExperience());
                }
                leaders.sort(new UserExperienceComparator());
                for (User leader : leaders) {
                    Log.d("Leaderboard", "map: " + leader.getName());
                }
                first.setText("1 - " + leaders.get(0).getName());
                second.setText("2 - " + leaders.get(1).getName());
                third.setText("3 - " + leaders.get(2).getName());
                fourth.setText("4 - " + leaders.get(3).getName());
                fifth.setText("5 - " + leaders.get(4).getName());

//                Log.d("Leaderboard",  leaders.get(0).getName());
//                for (User leader : leaders) {
//                    Log.d("Leaderboard", "map: " + leader.getName());
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class UserExperienceComparator implements Comparator<User> {
        @Override
        public int compare(User user1, User user2) {
            // Compare the experience fields of the two users
            // Return a negative value if user1's experience is less than user2's
            // Return a positive value if user1's experience is greater than user2's
            // Return 0 if user1's experience is equal to user2's

            return Integer.compare(user2.getExperience(), user1.getExperience());
        }
    }
}