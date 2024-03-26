package edu.northeastern.brainrush;

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
import com.squareup.picasso.Picasso;

public class DailyQuestionsActivity extends AppCompatActivity {
    private ImageView dailyPic;
    private CalendarView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_questions);
        dailyPic = findViewById(R.id.dailyPicture);
        calendar = findViewById(R.id.calendarView);
        calendar.setMaxDate(System.currentTimeMillis());
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.google.com";

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


        calendar.getDate();

        Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(dailyPic);

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
}
