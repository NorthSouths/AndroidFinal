package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication2.dao.QuestionDao;
import com.example.myapplication2.data.Question;

public class InputQuestionActivity extends AppCompatActivity {
    private QuestionDao questionDao;
    private String questionContent;
    private int questionChapter;
    private String choiceA, choiceB, choiceC, choiceD, correctChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_question);
        questionDao = new QuestionDao(InputQuestionActivity.this);

        // 禁止使用constraint layout
        // 绘制界面，绑定按钮，输入框，
        // 点击按钮
        // 输入题目的信息（id随意取，应该不影响 ）
        // 之后调用questionDao.insertQuestion(question); 即可
        //  Question question = new Question(0, questionContent, questionChapter, choiceA, choiceB, choiceC, choiceD, correctChoice);
        //   questionDao.insertQuestion(question);
        //要求点击插入之后Toast输出信息，清空屏幕，用户继续输入
        // 直到点击返回按钮或返回键，调用finish()
    }
}