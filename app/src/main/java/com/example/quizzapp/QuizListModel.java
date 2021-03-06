package com.example.quizzapp;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {
    @DocumentId
    private String quiz_id;
    private String name , desc , image,visibility ,level ;
    private long question;


    public QuizListModel() {
    }

    public QuizListModel(String name, String desc, String image, String visibility, String level, long question, String quiz_id) {
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.visibility = visibility;
        this.level = level;
        this.question = question;
        this.quiz_id = quiz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }
}
