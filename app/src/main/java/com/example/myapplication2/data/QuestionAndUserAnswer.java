package com.example.myapplication2.data;
/*
    author : 2191110609 李俊霏
 */
public class QuestionAndUserAnswer {
    private Question question;
    private UserAnswerResult userAnswerResult;

    public QuestionAndUserAnswer(Question question, UserAnswerResult userAnswerResult) {
        this.question = question;
        this.userAnswerResult = userAnswerResult;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public UserAnswerResult getUserAnswerResult() {
        return userAnswerResult;
    }

    public void setUserAnswerResult(UserAnswerResult userAnswerResult) {
        this.userAnswerResult = userAnswerResult;
    }
}
