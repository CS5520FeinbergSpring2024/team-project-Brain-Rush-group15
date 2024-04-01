package edu.northeastern.brainrush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openUserFile(View v){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        Toast.makeText(this, "user file clicked", Toast.LENGTH_SHORT).show();
    }

    public void openMail(View v){
        Toast.makeText(this, "mail clicked", Toast.LENGTH_SHORT).show();
    }

    public void openDashboard(View v){
        startActivity(new Intent(MainActivity.this, DailyQuestionsActivity.class));
        Toast.makeText(this, "dashboard clicked", Toast.LENGTH_SHORT).show();
    }

    public void openLearn(View v){
        startActivity(new Intent(MainActivity.this, LearnActivity.class));
        Toast.makeText(this, "learn clicked", Toast.LENGTH_SHORT).show();
    }

    public void openMake(View v){
        startActivity(new Intent(MainActivity.this, MakeActivity.class));
        Toast.makeText(this, "make clicked", Toast.LENGTH_SHORT).show();
    }

    public void openCompete(View v){
        startActivity(new Intent(MainActivity.this, CompeteActivity.class));
        Toast.makeText(this, "compete clicked", Toast.LENGTH_SHORT).show();
    }
}