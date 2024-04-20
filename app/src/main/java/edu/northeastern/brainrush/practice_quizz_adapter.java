package edu.northeastern.brainrush;

import static androidx.core.app.ActivityCompat.startActivityForResult;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.brainrush.model.Question;

public class practice_quizz_adapter extends RecyclerView.Adapter<practice_quizz_adapter.practice_quizz_ViewHolder> {

    //private List<Question_dummy> Quizzlist;//le quizz list
    private List<Question> Quizzlist;//le quizz list
    private Context context;
    private String userid;

    public interface AdapterDataObserver {
        void onListEmpty();
    }
    private OnItemClickListener listener;
    private AdapterDataObserver dataObserver;


    public practice_quizz_adapter(List<Question> quizzlist, Context context, String uid, OnItemClickListener listener) {
        this.Quizzlist = quizzlist;
        this.context = context;
        this.userid = uid;
        this.listener = listener;


    }


    @NonNull
    @Override
    public practice_quizz_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//create view
        //return new practice_quizz_ViewHolder(LayoutInflater.from(Context).inflate(R.layout.practice_quizzlist_item, parent, false));//set parent n false to avoid two textview overlap
        return new practice_quizz_ViewHolder(LayoutInflater.from(context).inflate(R.layout.quizzlist_item, parent, false));//set parent n false to avoid two textview overlap

    }

    @Override
    public void onBindViewHolder(@NonNull practice_quizz_ViewHolder holder, int position){
        //binding data
        holder.bindtheData(Quizzlist.get(position));
        // Check if list is empty after binding, which could occur if items are being removed elsewhere
//        holder.itemView.setOnClickListener(v -> {
//            // Example of safely removing an item with a delay
//            new Handler(Looper.getMainLooper()).post(() -> {
//                Quizzlist.remove(position);
//                notifyItemRemoved(position);
//                if (Quizzlist.isEmpty() && dataObserver != null) {
//                    dataObserver.onListEmpty();
//                }
//            });
//        });
    }

    public void setAdapterDataObserver(AdapterDataObserver observer) {
        this.dataObserver = observer;
    }

    @Override
    public int getItemCount() {
        Log.d("Question", "return the size of the question list" + Quizzlist.size());

        return Quizzlist.size();


    }

    public class practice_quizz_ViewHolder extends RecyclerView.ViewHolder  {
        public TextView quizz_titleTV;
        public TextView quizz_likenessTV;

        //an indicater that verify if the user opened/access the quizz, in another work, seened

        public practice_quizz_ViewHolder(@NonNull View itemsView) {
            super(itemsView);
            quizz_titleTV = itemsView.findViewById(R.id.quizz_title);
            quizz_likenessTV = itemsView.findViewById(R.id.quizz_likes);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Question question = Quizzlist.get(position);
                    if (question.id != null && listener != null) {
                        listener.onItemClick(question.id, userid);
                    }
                }
            });
        }

        public void setLimitedWordsText(TextView textView, String text, int maxWords) {
            String[] words = text.split("\\s+"); // Split by whitespace
            maxWords = Math.min(words.length, maxWords); // Ensure not to exceed the number of available words

            String resultraw = Arrays.stream(words, 0, maxWords).collect(Collectors.joining(" "));
            String result = resultraw + "...";
            textView.setText(result);
        }



        public void bindtheData(Question quizzOnarrival) {
            setLimitedWordsText(quizz_titleTV, quizzOnarrival.context, 5);
            int likers = 0;
            if(quizzOnarrival.likes!=null){
                likers = quizzOnarrival.likes.size();}
            quizz_likenessTV.setText("Like: " +Integer.toString(likers)); // Assuming there is a getLikes method returning an int.
        }
    }
}
