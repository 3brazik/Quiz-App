package com.example.quizzapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import java.util.List;


public class ListFragment extends Fragment implements QuizListAdapter.OnQuizItemClicked {
    private RecyclerView listview;
    private QuizListViewModel quizListViewModel;
    private QuizListAdapter quizListAdapter;
    private ProgressBar listProgress;
    private Animation fadeInAnim;
    private Animation fadeOutAnime;
    private NavController navController;




    public ListFragment() {
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
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listProgress=view.findViewById(R.id.list_progress);
        listview = view.findViewById(R.id.list_view);
        quizListAdapter = new QuizListAdapter(this );
        listview.setLayoutManager(new LinearLayoutManager(getContext()));
        listview.setHasFixedSize(true);
        listview.setAdapter(quizListAdapter);
        fadeInAnim= AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
        fadeOutAnime =AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                listview.startAnimation(fadeInAnim);
                listProgress.startAnimation(fadeOutAnime);
                quizListAdapter.setQuizListModels(quizListModels);
                quizListAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        ListFragmentDirections.ActionListFragmentToDetailsFragment action =ListFragmentDirections.actionListFragmentToDetailsFragment();
        action.setPosition(position);
        navController.navigate(action);

    }
}