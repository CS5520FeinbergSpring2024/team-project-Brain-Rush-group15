package edu.northeastern.brainrush;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class practice_quizz_adapter extends RecyclerView.Adapter<practice_quizz_ViewHolder> {

    private final OnCorrectAnswerListener listener;
    private List<Question_dummy> Quizzlist;//le quizz list
    private Context Context;
    public practice_quizz_adapter(List<Question_dummy> quizzlist, Context context, OnCorrectAnswerListener listener) {
        this.Quizzlist = quizzlist;
        this.Context = context;
        this.listener = listener;

    }


    @NonNull
    @Override
    public practice_quizz_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//create view
        //return new practice_quizz_ViewHolder(LayoutInflater.from(Context).inflate(R.layout.practice_quizzlist_item, parent, false));//set parent n false to avoid two textview overlap
        return new practice_quizz_ViewHolder(LayoutInflater.from(Context).inflate(R.layout.practice_radio, parent, false));//set parent n false to avoid two textview overlap

    }

    @Override
    public void onBindViewHolder(@NonNull practice_quizz_ViewHolder holder, int position){
//binding data
        holder.bindtheData(Quizzlist.get(position), listener);//animation related code
    }

    @Override
    public int getItemCount() {
        Log.d("Question", "return the size of the question list" + Quizzlist.size());

        return Quizzlist.size();


    }
}
