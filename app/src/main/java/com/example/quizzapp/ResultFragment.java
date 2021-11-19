package com.example.quizzapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ResultFragment extends Fragment {
    private NavController navController;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String quizId;
    private String currentUserId;
    private TextView resultCorrect;
    private TextView resultWrong;
    private TextView resultMissed;

    private TextView resultPercent;
    private ProgressBar resultProgress;
    private Button resultHomeBtn;


    public ResultFragment() {
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
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseFirestore=FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        //get User id
        if (firebaseAuth.getCurrentUser() != null) {
             currentUserId = firebaseAuth.getCurrentUser().getUid();
        } else {

            //Go To Home Page
        }
        quizId=ResultFragmentArgs.fromBundle(getArguments()).getQuizId();
        //initialize UI Elements
        resultCorrect=view.findViewById(R.id.result_correct_text);
        resultWrong=view.findViewById(R.id.result_wrong_text);
        resultMissed=view.findViewById(R.id.result_missed_text);
        resultHomeBtn=view.findViewById(R.id.result_home_btn);
        resultProgress=view.findViewById(R.id.result_progress);
        resultPercent=view.findViewById(R.id.result_percent);

        resultHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        });

        //get result
         firebaseFirestore.collection("QuizList").document(quizId).collection("Result").document(currentUserId)
                 .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @SuppressLint("SetTextI18n")
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()){
                     DocumentSnapshot result=task.getResult();
                     Long correct=result.getLong("correct");
                     Long wrong=result.getLong("wrong");
                     Long missed=result.getLong("unAnswered");
                     resultCorrect.setText(correct != null ? correct.toString() : null);
                     resultWrong.setText(wrong !=null? wrong.toString() :null);
                     resultMissed.setText(missed != null ? missed.toString() : null);

                     //Calculate progress
                     Long total=correct + missed + wrong;
                     Long percent=(correct*100)/total;


                     resultProgress.setProgress(percent.intValue());
                     resultPercent.setText(percent +"%");

                 }


             }
         });

    }
}