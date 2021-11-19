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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class QuizFragment extends Fragment implements View.OnClickListener {
    private final static String TAG="QUIZ_FRAGMENT_LOG";
    private NavController navController;
    private  FirebaseFirestore firebaseFirestore;
    private String quizId;
    private String currentUserId;
    FirebaseAuth firebaseAuth;
    private String quizName;


    //UI Element
    private TextView title;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private ProgressBar questionProgress;
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
    private boolean canAnswer=false;
    private int currentQuestion=0;
    private int correctAnswer =0;
    private int wrongAnswer =0;
    private int notAnswered=0;




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
        navController= Navigation.findNavController(view);

        firebaseAuth=FirebaseAuth.getInstance();
        //get User id
        if (firebaseAuth.getCurrentUser()!=null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }else{

        }
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
         questionProgress=view.findViewById(R.id.quiz_question_progress);

         optionOneBtn.setOnClickListener(this);
         optionTwoBtn.setOnClickListener(this);
         optionThreeBtn.setOnClickListener(this);
         nextBtn.setOnClickListener(this);
        //Initialize
         firebaseFirestore = FirebaseFirestore.getInstance();
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        quizName=QuizFragmentArgs.fromBundle(getArguments()).getQuizName();

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
            title.setText(quizName);
            questionText.setText("load First Question");

            //enable options

       enableBtn();
        // load
        loadQuestions(1);

    }




   private void loadQuestions(int questionNum) {
       questionNumber.setText(questionNum + "");
       questionText.setText(questionToAnswer.get(questionNum-1).getQuestion());
          //questionNumber.setText(questionNum);
        //Load options

        optionOneBtn.setText(questionToAnswer.get(questionNum-1).getOption_a());
        optionTwoBtn.setText(questionToAnswer.get(questionNum-1).getOption_b());
        optionThreeBtn.setText(questionToAnswer.get(questionNum-1).getOption_c());
        //set can answer
       canAnswer=true;
       currentQuestion=questionNum;

        //start question timer
        startTimer(questionNum);
    }

    @SuppressLint("SetTextI18n")
    private void startTimer(int questionNumber) {

        Long timeToAnswer = questionToAnswer.get(questionNumber-1).getTimer();
        questionTime.setText(timeToAnswer.toString());

        questionProgress.setVisibility(View.VISIBLE);
        //start count down time

       countDownTimer=  new CountDownTimer(timeToAnswer * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {

                questionTime.setText(millisUntilFinished/1000+"");
                Long percent =millisUntilFinished/(timeToAnswer*10);
                questionProgress.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {

                canAnswer=false;
                questionFeedback.setText("time up");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                notAnswered++;
                showNextButton();

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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.quiz_option_one:
                verfiyAnswer(optionOneBtn);
                break;
            case R.id.quiz_option_two:
                verfiyAnswer(optionTwoBtn);
                break;
            case R.id.quiz_option_three:
                verfiyAnswer(optionThreeBtn);
                break;
            case R.id.quiz_next_btn:
                if (currentQuestion==totalQuestionsToAnswer){

                    //load Results
                    submitResults();

                }else {
                    currentQuestion++;
                    loadQuestions(currentQuestion);
                    resetOptions();
                }
                 break;

        }

    }

    private void submitResults() {
        HashMap<String,Object> resultMap =new HashMap<>();
        resultMap.put("correct ",correctAnswer);
        resultMap.put("wrong",wrongAnswer);
        resultMap.put("unAnswered",notAnswered);

        firebaseFirestore.collection("QuizList").document(quizId).collection("Result").document(currentUserId).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //go to Result page
                    QuizFragmentDirections .ActionQuizFragmentToResultFragment action =QuizFragmentDirections.actionQuizFragmentToResultFragment();
                    action.setQuizId(quizId);
                    navController.navigate(action);
                    

                }else{
                    //show error
                    title.setText(task.getException().getMessage());

                }
            }
        });
    }

    private void resetOptions() {

        optionOneBtn.setTextColor(getResources().getColor(R.color.colorLightText,null));
        optionTwoBtn.setTextColor(getResources().getColor(R.color.colorLightText,null));
        optionThreeBtn.setTextColor(getResources().getColor(R.color.colorLightText,null));
        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }

    private void verfiyAnswer(Button selectedAnswerBtn) {

        selectedAnswerBtn.setTextColor(getResources().getColor(R.color.colorDark,null));
        if (canAnswer){
            selectedAnswerBtn.setTextColor(getResources().getColor(R.color.colorDark,null));

            if (questionToAnswer.get(currentQuestion-1).getAnswer().equals(selectedAnswerBtn.getText())){
                //CorrectAnswer
                correctAnswer++;


                questionFeedback.setText("correct answer");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary,null));

            }
            else {
                //wrong answer
                wrongAnswer++;
                questionFeedback.setText("wrong answer\n correct Answer is : "+questionToAnswer.get(currentQuestion-1).getAnswer() );
                questionFeedback.setTextColor(getResources().getColor(R.color.colorAccent,null));
            }
            //set can answer to false
            canAnswer=false;

            //Stop the timer
            countDownTimer.cancel();

            // Show next Button
            showNextButton();

        }

    }

    private void showNextButton() {
        if(currentQuestion==totalQuestionsToAnswer){
            nextBtn.setText("submit Result");
        }
        questionFeedback.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setEnabled(true);

    }


}