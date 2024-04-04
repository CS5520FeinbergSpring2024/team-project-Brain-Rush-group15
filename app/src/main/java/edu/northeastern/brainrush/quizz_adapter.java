package edu.northeastern.brainrush;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class quizz_adapter extends RecyclerView.Adapter<quizz_ViewHolder> {
    private List<Quizz> Quizzlist;//le quizz list
    private android.content.Context Context;

    public quizz_adapter(List<Quizz> quizzlist, Context context){
        this.Quizzlist = quizzlist;
        this.Context = context;
    }
    @NonNull
    @Override
    public quizz_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//create view
        return new quizz_ViewHolder(LayoutInflater.from(Context).inflate(R.layout.quizzlist_item, parent, false));//set parent n false to avoid two textview overlap

    }

    @Override
    public void onBindViewHolder(@NonNull quizz_ViewHolder holder, int position){
//binding data
        holder.bindtheData(Quizzlist.get(position));//idunno
    }

    @Override
    public int getItemCount() {
        return Quizzlist.size();

    }
}
