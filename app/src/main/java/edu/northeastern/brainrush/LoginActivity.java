package edu.northeastern.brainrush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.brainrush.model.TestUser;
import edu.northeastern.brainrush.model.User;

public class LoginActivity extends AppCompatActivity {

    EditText userName;

    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myRef = FirebaseDatabase.getInstance().getReference().child("User");
        userName = findViewById(R.id.username);
    }

    public void loginClicked(View v){
        String name = userName.getText().toString();
        myRef.equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String username = userSnapshot.getKey();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                } else {
                    register(name);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", name);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void register(String userName){
        User new_user = new User(userName);
        myRef.child(userName).setValue(new_user)
                .addOnSuccessListener(s -> {
                    // Handle success
                    Log.d("AddNewUser", "User added successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.w("AddNewUser", "Error adding user", e);
                });
    }
}
