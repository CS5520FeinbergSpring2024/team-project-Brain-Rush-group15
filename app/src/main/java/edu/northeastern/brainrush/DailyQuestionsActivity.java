package edu.northeastern.brainrush;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DailyQuestionsActivity extends AppCompatActivity {
    private ImageView dailyPic;
    private CalendarView calendar;
    private String dateSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_questions);
        dailyPic = findViewById(R.id.dailyPicture);
        calendar = findViewById(R.id.calendarView);
        calendar.setMaxDate(System.currentTimeMillis());

        /*
        Setting the season picture:
            Spring: March-May, Summer: June-August, Fall: September-November, Winter: December-February
         */
        Calendar rightNow = Calendar.getInstance();
        int month = rightNow.get(Calendar.MONTH);
        if(month > 1 && month < 5){
            dailyPic.setImageResource(R.drawable.spring);
        }
        else if(month > 4 && month < 8){
            dailyPic.setImageResource(R.drawable.summer);
        }
        else if(month > 7 && month < 11){
            dailyPic.setImageResource(R.drawable.fall);
        }
        else {
            dailyPic.setImageResource(R.drawable.winter);
        }

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // month is 0-based, add 1 for the correct month
                dateSelected = (month + 1) + "-" + dayOfMonth + "-" + year;
            }
        });
    }

    public void goButtonClick(View view){
        Log.v("Click", dateSelected);
    }
}
