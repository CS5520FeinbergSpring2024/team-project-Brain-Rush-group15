package edu.northeastern.brainrush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.brainrush.model.User;

public class SignupActivity extends AppCompatActivity {
    private EditText userName;
    private EditText passWord;
    private EditText email;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        myRef = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.username);
        passWord = findViewById(R.id.password);
        email = findViewById(R.id.email);
    }

    public void createClicked(View view){
        String name = userName.getText().toString().trim();
        String password = passWord.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name/password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        signupUser(name, emailAddress, password);

    }

    private void signupUser(String name, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is successfully registered
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) {
                            storeNewUserToTheDB(name, user.getUid());
                        }
                        sendVerificationEmail(user);
                    } else {
                        // Handle errors
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void storeNewUserToTheDB(String userName, String id){
        User user = new User(userName);
        myRef.child(id).setValue(user)
                .addOnSuccessListener(s -> {
                    // Handle success
                    Log.d("AddNewUser", "User added successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.w("AddNewUser", "Error adding user", e);
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification email sent to " + user.getEmail() + ". Please verify before you log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                        } else {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
