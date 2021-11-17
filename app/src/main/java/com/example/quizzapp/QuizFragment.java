package com.example.quizzapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class QuizFragment extends Fragment {
    private final static String TAG="QUIZ_FRAGMENT_LOG";
    private  FirebaseFirestore firebaseFirestore;
    private String quizId;
    //UI Element
    private TextView title;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private TextView questionProgress;
    private TextView questionNumber;
    private Button optionOneBtn;
    private Button optionTwoBtn;
    private Button optionThreeBtn;
    private Button nextBtn;
    private ImageButton closeBtn;
    // Firebase Data
    private List<QuestionsModel> allQuestionList = new ArrayList<>();
    private long totalQuestionsToAnswer = 10;
    private List<QuestionsModel> questionToAnswer=new ArrayList<>();
    private CountDownTimer countDownTimer;


    public QuizFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       //UI initialize
         title =view.findViewById(R.id.quiz_title);
         questionFeedback =view.findViewById(R.id.quiz_question_answer);
         questionNumber =view.findViewById(R.id.quiz_question_number);
         questionTime =view.findViewById(R.id.quiz_question_time);
         questionText =view.findViewById(R.id.quiz_question);
         optionOneBtn=view.findViewById(R.id.quiz_option_one);
         optionTwoBtn=view.findViewById(R.id.quiz_option_two);
         optionThreeBtn=view.findViewById(R.id.quiz_option_three);
         nextBtn=view.findViewById(R.id.quiz_next_btn);

        //Initialize
         firebaseFirestore = FirebaseFirestore.getInstance();
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
       totalQuestionsToAnswer =  QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();
        firebaseFirestore.collection("QuizList").document(quizId)
                .collection("Questions")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allQuestionList = Objects.requireNonNull(task.getResult()).toObjects(QuestionsModel.class);
                        Log.d(TAG, "Question List :" + allQuestionList.get(0).getQuestion());


                    pickQuestions();
                      LoadUI();
                }
                    else{
                        //Error
                        title.setText("Error");

                    }
                });


    }
   @SuppressLint("SetTextI18n")
   private void LoadUI() {
        //Quiz Data Loaded
            title.setText("Quiz data Loaded");
            questionText.setText("load First Question");

            //enable options

       enableBtn();
        // load
        loadQuestions(1);

    }




   private void loadQuestions(int questionNum) {
        questionText.setText(questionToAnswer.get(questionNum).getQuestion());
          //questionNumber.setText(questionNum);
        //Load options

        optionOneBtn.setText(questionToAnswer.get(questionNum).getOption_a());
        optionTwoBtn.setText(questionToAnswer.get(questionNum).getOption_b());
        optionThreeBtn.setText(questionToAnswer.get(questionNum).getOption_c());
        //start question timer
        startTimer(questionNum);
    }

    @SuppressLint("SetTextI18n")
    private void startTimer(int questionNumber) {

        Long timeToAnswer = questionToAnswer.get(questionNumber).getTimer();
        questionTime.setText(timeToAnswer.toString());
        //start count down time

       countDownTimer=  new CountDownTimer(timeToAnswer * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                questionTime.setText(millisUntilFinished/1000+"");
            }

            @Override
            public void onFinish() {

            }
        };
       countDownTimer.start();
    }


    private void enableBtn() {
        optionOneBtn.setVisibility(View.VISIBLE);
        optionTwoBtn.setVisibility(View.VISIBLE);
        optionThreeBtn.setVisibility(View.VISIBLE);

        optionOneBtn.setEnabled(true);
        optionTwoBtn.setEnabled(true);
        optionThreeBtn.setEnabled(true);

        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);

    }

    private void pickQuestions() {
        for (int i=0; i<totalQuestionsToAnswer;i++){
            int randomNumber=getRandomInt(allQuestionList.size(),0);
            questionToAnswer.add(allQuestionList.get(randomNumber));
            allQuestionList.remove(randomNumber);
            Log.d("QUESTION LOG", "Questions:  " +i+ " : "+questionToAnswer.get(i).getQuestion());
        }
    }
    public static int getRandomInt(int max ,int mini){
        return ((int)(Math.random()*(max - mini)))+mini;
    }



}