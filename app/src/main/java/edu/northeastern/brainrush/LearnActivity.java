package edu.northeastern.brainrush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Random;

import edu.northeastern.brainrush.model.User;

public class LearnActivity extends AppCompatActivity {
    private Button start_learn;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Spinner spinner = findViewById(R.id.category_dropdown);
    // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_values,
                android.R.layout.simple_spinner_item
        );
    // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);

        //load user here
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Setting up the date selected from the previous page
            user = (User) extras.get("user");
        }


        //adding button for open new learning poge;
        start_learn = findViewById(R.id.learn_button);
        start_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to quizz page
                //pass down user id
                //pass down user choice
                String selectedCategory = spinner.getSelectedItem().toString();
                //if == random, random subjects from that 3
                //maybe r = r.array?
                if(selectedCategory.equals("Random")){
                    String[] subjects = {"Math", "History", "Science", "Language", "Geography", "Art", "Music", "Computer"};
                    Random random = new Random();
                    int index = random.nextInt(subjects.length);
                    selectedCategory = subjects[index];

                }
                Intent intent = new Intent(LearnActivity.this, learn_page.class);
                intent.putExtra("selectedCategory", selectedCategory);
                intent.putExtra("user", user);//send user info to the next intent

                startActivity(intent);
            }
        });


    }
}