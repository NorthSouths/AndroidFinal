package com.example.myapplication2.data;
/*
    author : 2191110609 李俊霏
 */
public class UserAnswerResult {
    private int questionID;
    private String userAnswer;

    public UserAnswerResult() {

    }

    public UserAnswerResult(int questionID, String userAnswer) {
        this.questionID = questionID;
        this.userAnswer = userAnswer;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
