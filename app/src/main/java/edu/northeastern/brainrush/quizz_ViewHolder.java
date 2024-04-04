package edu.northeastern.brainrush;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//the viewholder handle the atomized quizz items

public class quizz_ViewHolder extends RecyclerView.ViewHolder {
    public TextView quizz_titleTV;
    public TextView quizz_likenessTV;

    //an indicater that verify if the user opened/access the quizz, in another work, seened

    public quizz_ViewHolder(@NonNull View itemsView){
        super(itemsView);//idunno
        quizz_titleTV = itemsView.findViewById(R.id.quizz_title);
        quizz_likenessTV = itemsView.findViewById(R.id.quizz_likes);



    }

    public void bindtheData(Quizz quizzOnarrival){
        quizz_titleTV.setText(quizzOnarrival.getTitle());
        quizz_likenessTV.setText(Integer.toString(quizzOnarrival.getlikeness()));
    }

}