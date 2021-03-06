package com.example.quizzapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private int position;
    private ImageView detailsImage;
    private TextView detailsTitle;
    private TextView detailsDesc;
    private TextView detailsDiff ;
    private TextView detailsQues;
    private Button detailsButton;
    private long totalQuestions = 0 ;


    private String quizId;


    public DetailsFragment() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        position=   DetailsFragmentArgs.fromBundle(getArguments()).getPosition();
        //Initialize UI Elements
        detailsImage=view.findViewById(R.id.details_image);
        detailsTitle=view.findViewById(R.id.details_title);
        detailsDesc=view.findViewById(R.id.details_desc);
        detailsDiff=view.findViewById(R.id.details_diffculty);
        detailsQues=view.findViewById(R.id.details_questions);
        detailsButton=view.findViewById(R.id.details_start_btn);

detailsButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                Glide
                        .with(getContext())
                        .load(quizListModels.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(detailsImage);
                detailsTitle.setText(quizListModels.get(position).getName());
                detailsDesc.setText(quizListModels.get(position).getDesc());
                detailsQues.setText(quizListModels.get(position).getQuestion()+"");
                detailsDiff.setText(quizListModels.get(position).getLevel());
                quizId =quizListModels.get(position).getQuiz_id();
                 totalQuestions = quizListModels.get(position).getQuestion();



            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case R.id.details_start_btn:

               DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
               action.setTotalQuestions(totalQuestions);
               action.setQuizId(quizId);

               navController.navigate(action);
            break;
        }

    }
}