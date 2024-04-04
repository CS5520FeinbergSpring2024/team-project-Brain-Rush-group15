package edu.northeastern.brainrush;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class panelactivity extends AppCompatActivity {
    RecyclerView quizzview;
    List<Quizz> quizzlist;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        quizzlist = new ArrayList<>();

        int dummylikeness = 5;
        List<String> sampleTitle = new ArrayList<>();
        sampleTitle.add("Biology");
        sampleTitle.add("zig zig");
        sampleTitle.add("mophrism");
        sampleTitle.add("Nlists");
        sampleTitle.add("Meme");

        for(String titles: sampleTitle){
            quizzlist.add(new Quizz(titles, dummylikeness++));
        }
        //this should set up the basic list for quizz

        quizzview = findViewById(R.id.quizzlist);

        quizzview.setHasFixedSize(true);

        quizzview.setLayoutManager(new LinearLayoutManager(this));

        quizzview.setAdapter(new quizz_adapter(quizzlist, this));
//main activity basic view complete

    }
}


