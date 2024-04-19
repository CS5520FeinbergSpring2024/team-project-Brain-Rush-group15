package edu.northeastern.brainrush;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.brainrush.model.Question;

//the viewholder handle the atomized quizz items

public class quizz_ViewHolder2 extends RecyclerView.ViewHolder {
    public TextView quizz_titleTV;
    public TextView quizz_likenessTV;

    //an indicater that verify if the user opened/access the quizz, in another work, seened

    public quizz_ViewHolder2(@NonNull View itemsView){
        super(itemsView);//idunno
        quizz_titleTV = itemsView.findViewById(R.id.quizz_title);
        quizz_likenessTV = itemsView.findViewById(R.id.quizz_likes);


    }



    public void bindtheData(Question quizzOnarrival){
        quizz_titleTV.setText(quizzOnarrival.context);
        quizz_likenessTV.setText(Integer.toString(0));
    }

}