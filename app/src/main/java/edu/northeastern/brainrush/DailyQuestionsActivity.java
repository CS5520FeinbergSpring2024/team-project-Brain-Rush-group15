package edu.northeastern.brainrush;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import edu.northeastern.brainrush.model.User;

public class DailyQuestionsActivity extends AppCompatActivity {
    private ImageView dailyPic;
    private MaterialCalendarView calendar;
    private String dateSelected;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Date now = new Date();
        dateSelected = String.valueOf(now.getMonth()+1) + "-" + (now.getDate()) + "-" + (now.getYear()+1900);

        //Set the user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Setting up the date selected from the previous page
            user = (User) extras.get("user");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_question_main_page);
        dailyPic = findViewById(R.id.dailyPicture);
        calendar = findViewById(R.id.calendarView);
        calendar.setBackgroundColor(Color.parseColor("#4CAF50"));
        HashSet<CalendarDay> h = new HashSet<>();
        for(String date:user.getDaily_question_answered()){
            String dateSplit[] = date.split("-");
            h.add(CalendarDay.from(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1])));
        }
        calendar.addDecorator(new CalendarDecorator(Color.BLUE, h));
        calendar.setSelectedDate(CalendarDay.today());
        calendar.state().edit()
                .setMaximumDate(CalendarDay.today())
                .commit();

        //calendar.setMaxDate(System.currentTimeMillis());


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
    }

    public void backToMainPage(){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void backButtonClick(View view){
        backToMainPage();
    }

    public void goButtonClick(View view){
        //Log.v("Click", dateSelected);
        int month = calendar.getSelectedDate().getMonth();
        int dayOfMonth = calendar.getSelectedDate().getDay();
        int year = calendar.getSelectedDate().getYear();
        dateSelected = month  + "-" + dayOfMonth + "-" + year;
        Intent intent = new Intent(this, DailyQuestionProblemPage.class);
        intent.putExtra("date", dateSelected);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
