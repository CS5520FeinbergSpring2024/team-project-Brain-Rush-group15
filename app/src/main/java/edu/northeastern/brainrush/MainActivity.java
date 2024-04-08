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

import edu.northeastern.brainrush.model.User;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private Picasso picasso;
    private User user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Setting up the date selected from the previous page
            uid = extras.getString("id");
            user = (User) extras.get("user");
        }

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
        } catch (IllegalStateException e) {
            // Handle the exception if the singleton instance was already set
            Log.w("Picasso", "Picasso instance was already set. It's safe to ignore this.");
        }
    }

    public void openUserFile(View v){
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", uid);
        startActivity(intent);
        Toast.makeText(this, "user file clicked", Toast.LENGTH_SHORT).show();
    }

    public void openMail(View v){
        Intent intent = new Intent(MainActivity.this, panelactivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", uid);
        startActivity(intent);
        Toast.makeText(this, "mail clicked", Toast.LENGTH_SHORT).show();
    }

    public void openDashboard(View v){
        Intent intent = new Intent(MainActivity.this, DailyQuestionsActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", uid);
        startActivity(intent);
        Toast.makeText(this, "dashboard clicked", Toast.LENGTH_SHORT).show();
    }

    public void openLearn(View v){
        Intent intent = new Intent(MainActivity.this, LearnActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", uid);
        startActivity(intent);
        Toast.makeText(this, "learn clicked", Toast.LENGTH_SHORT).show();
    }

    public void openMake(View v){
        Intent intent = new Intent(MainActivity.this, MakeActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", uid);
        startActivity(intent);
        Toast.makeText(this, "make clicked", Toast.LENGTH_SHORT).show();
    }

    public void openCompete(View v){
        Intent intent = new Intent(MainActivity.this, CompeteActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("id", uid);
        startActivity(intent);
        Toast.makeText(this, "compete clicked", Toast.LENGTH_SHORT).show();
    }

}