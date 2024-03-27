package edu.northeastern.brainrush;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DailyQuestionsActivity extends AppCompatActivity {
    private ImageView dailyPic;
    private CalendarView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_daily_questions);
        dailyPic = findViewById(R.id.dailyPicture);
        calendar = findViewById(R.id.calendarView);
        calendar.setMaxDate(System.currentTimeMillis());
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.google.com";

        getFileFromFirebaseStorage(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.v("Receive", "Response is: " + response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", "That didn't work!");
            }
        });

        queue.add(stringRequest);

        ///Picasso.get().load("gs://group15-91f85.appspot.com/cool.png").into(dailyPic);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // month is 0-based, add 1 for the correct month
                String selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                Log.d("SelectedDate", selectedDate);
                // Use the selectedDate string as needed
            }
        });
    }

    public void goButtonClick(View view){
        Log.v("Click", String.valueOf(calendar.getDateTextAppearance()));
    }

    public void getFileFromFirebaseStorage(Context context){
        FirebaseApp.initializeApp(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl("gs://brain-rush-db21a.appspot.com/cool.png");
        gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri).into(dailyPic);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
