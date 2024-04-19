package edu.northeastern.brainrush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.brainrush.model.Question;

public class quizz_adapter extends RecyclerView.Adapter<quizz_adapter.quizz_ViewHolder> {
    private List<Question> Quizzlist;//le quizz list
    private Context context;//keep for avoid unexpected issue
    private ActivityResultLauncher<Intent> startActivityLauncher;

    private String userid;



    public quizz_adapter(List<Question> quizzlist, Context context, ActivityResultLauncher<Intent> startActivityLauncher, String uid) {
        this.context = context;
        this.Quizzlist = quizzlist;
        this.startActivityLauncher = startActivityLauncher;
        this.userid = uid;
    }
//    public quizz_adapter(List<Question> quizzlist, Context context) {
//        this.Quizzlist = quizzlist;
//        this.context = context;
//    }

    @NonNull
    @Override
    public quizz_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//create view
        return new quizz_ViewHolder(LayoutInflater.from(context).inflate(R.layout.quizzlist_item, parent, false));//set parent n false to avoid two textview overlap

    }

    @Override
    public void onBindViewHolder(@NonNull quizz_ViewHolder holder, int position) {
//binding data
        Log.d("Adapter", "Position: " + position + " Size: " + (Quizzlist != null ? Quizzlist.size() : "null list"));
        if (Quizzlist != null && position < Quizzlist.size()) {
            Question question = Quizzlist.get(position);
            Log.d("Adapter", "Question at position " + position + ": " + (question == null ? "null" : "not null"));
            if (question != null) {
                holder.bindtheData(question);
            } else {
                // Log or handle null question
            }
        }
    }

    @Override
    public int getItemCount() {
        return Quizzlist.size();

    }


    public class quizz_ViewHolder extends RecyclerView.ViewHolder {
        public TextView quizz_titleTV;
        public TextView quizz_likenessTV;

        //an indicater that verify if the user opened/access the quizz, in another work, seened

        public quizz_ViewHolder(@NonNull View itemsView) {
            super(itemsView);//idunno
            quizz_titleTV = itemsView.findViewById(R.id.quizz_title);
            quizz_likenessTV = itemsView.findViewById(R.id.quizz_likes);

            quizz_titleTV.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Question question = Quizzlist.get(position);
                    if (question.id != null) {
                        Log.d("quizz_adapter", "question ID provided: " + question.id );
                        Intent intent = new Intent(context, Paneldetailpage.class);
                        intent.putExtra("questionId", question.id);
                        intent.putExtra("userid", userid);

                        startActivityLauncher.launch(intent);

                    } else {
                        Log.e("quizz_adapter", "No question ID provided");
                    }

                }
            });
//            quizz_titleTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Ensure 'context' is not null
//                    if (context != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            Question question = Quizzlist.get(position);
//                            Intent intent = new Intent(context, Paneldetailpage.class);
//                            intent.putExtra("questionid", question.id);
//                            context.startActivity(intent);
//                        }
//                    } else {
//                        Log.e("Adapter", "Context is null");
//                    }
//                }
//            });
        }

        public void bindtheData(Question quizzOnarrival) {
            quizz_titleTV.setText(quizzOnarrival.context); // Assuming there is a getText method.
            quizz_likenessTV.setText(Integer.toString(0)); // Assuming there is a getLikes method returning an int.
        }
    }
}
//        public void bindtheData(Question quizzOnarrival) {
//            quizz_titleTV.setText(quizzOnarrival.context);
//            quizz_likenessTV.setText(Integer.toString(0));
//        }


//    public void updateData(List<Question> newQuestions) {
//        Quizzlist.clear();
//        Quizzlist.addAll(newQuestions);
//        notifyDataSetChanged();
//    }
