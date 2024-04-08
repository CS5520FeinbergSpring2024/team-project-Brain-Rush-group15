package edu.northeastern.brainrush;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class practice_quizz_ViewHolder extends RecyclerView.ViewHolder  {
    private final TextView Question_body_TV;//the question prompt

    private final TextView correct_indicator_TV;//only appear when correct answer is selected

    //le choices group is defined be;pw
    private RadioButton Choice_A_TV;
    private RadioButton Choice_B_TV;
    private RadioButton Choice_C_TV;
    private RadioButton Choice_D_TV;

    private RadioGroup  Question_radio;


    //private Button seeAnswerBN;

    //an indicater that verify if the user opened/access the quizz, in another work, seened

    public practice_quizz_ViewHolder(@NonNull View itemsView){
        super(itemsView);//idunno
        Question_body_TV = itemsView.findViewById(R.id.Question_Body);
        correct_indicator_TV = itemsView.findViewById(R.id.correct_answer_indicator);

        Question_radio = itemsView.findViewById(R.id.Question_group);
        Choice_A_TV = itemsView.findViewById(R.id.Question_choice_A);
        Choice_B_TV = itemsView.findViewById(R.id.Question_choice_B);
        Choice_C_TV = itemsView.findViewById(R.id.Question_choice_C);
        Choice_D_TV = itemsView.findViewById(R.id.Question_choice_D);

        //seeAnswerBN = itemsView.findViewById(R.id.reveal_answer);

        correct_indicator_TV.setVisibility(View.GONE);//gone by default

//        Question_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//when correct
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // Check if the first option is selected
//                if (checkedId == Choice_A_TV.getId()) {
//                    correct_indicator_TV.setVisibility(View.VISIBLE);
//                } else {
//                    correct_indicator_TV.setVisibility(View.GONE);
//                }
//            }
//        });


    }

    public void bindtheData(Question_dummy quizzOnarrival, OnCorrectAnswerListener listener){
        Question_body_TV.setText(quizzOnarrival.getQbody());
        Choice_A_TV.setText(quizzOnarrival.getQchoices().get(0));
        Choice_B_TV.setText(quizzOnarrival.getQchoices().get(1));
        Choice_C_TV.setText(quizzOnarrival.getQchoices().get(2));
        Choice_D_TV.setText(quizzOnarrival.getQchoices().get(3));

        //for setting up the radio group
        Question_radio.clearCheck();
        correct_indicator_TV.setVisibility(View.GONE);

        Question_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Assuming the correct answer index is 0-based and matches the order of choices
                int correctAnswerIndex = quizzOnarrival.getQanswer();
                RadioButton correctButton = (RadioButton) itemView.findViewById(group.getChildAt(correctAnswerIndex).getId());

                if (checkedId == correctButton.getId()) {
                    correct_indicator_TV.setVisibility(View.VISIBLE);
                    // Disable all RadioButton elements, lock once the correct answer is selected
                    //here to add exp to user
                    for (int i = 0; i < group.getChildCount(); i++) {
                        group.getChildAt(i).setEnabled(false);
                    }
                    listener.onCorrectAnswerSelected(); // Trigger the callback

                } else {
                    correct_indicator_TV.setVisibility(View.GONE);
                }
            }
        });


    }
}
