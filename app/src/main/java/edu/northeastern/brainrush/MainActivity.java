package edu.northeastern.brainrush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, getIntent().getStringExtra("username"), Toast.LENGTH_SHORT).show();
        //Construct singleton instance for Picasso
        File httpCacheDirectory = new File(this.getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024); // 10 MiB

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttpClientBuilder.build());
        try {
            picasso = new Picasso.Builder(this)
                    .downloader(okHttp3Downloader)
                    .build();
            Picasso.setSingletonInstance(picasso);
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException e) {
            // Handle the exception if the singleton instance was already set
            Log.w("Picasso", "Picasso instance was already set. It's safe to ignore this.");
        }
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