package com.example.quizzapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseRepository {
    private onFirestoreTaskComplete onFirestoreTaskComplete;
 private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
 private Query quizRef =firebaseFirestore.collection("QuizList").whereEqualTo("visibility","public");
public FirebaseRepository (onFirestoreTaskComplete onFirestoreTaskComplete){
    this.onFirestoreTaskComplete=onFirestoreTaskComplete;

}
 public void getQuizData (){
      quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if(task.isSuccessful()){
                  onFirestoreTaskComplete.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));

              }else {
                  onFirestoreTaskComplete.onError(task.getException());
              }
          }
      });
 }

public interface onFirestoreTaskComplete {
     void quizListDataAdded(List<QuizListModel> quizListModelList);
     void onError (Exception e) ;
}
}
