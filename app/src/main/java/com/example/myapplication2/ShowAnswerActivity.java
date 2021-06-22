package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.dao.QuestionDao;
import com.example.myapplication2.dao.RecordDao;
import com.example.myapplication2.data.Question;
import com.example.myapplication2.data.QuestionAndUserAnswer;
import com.example.myapplication2.data.Record;
import com.example.myapplication2.data.UserAnswerResult;

import java.util.ArrayList;
import java.util.List;

public class ShowAnswerActivity extends AppCompatActivity {
    private TextView questionTextView, title;
    private TextView answerA, answerB, answerC, answerD;
    private TextView correctAnswer, yourAnswer;
    private Button next, prev;

    private int groupID; //标识组号，用这个来查数据库
    private int realQuestionNum; //到底有几道题
    private int index = 0;//当前题号

    private RecordDao recordDao;
    private QuestionDao questionDao;
    private List<QuestionAndUserAnswer> questionAndUserAnswerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answer);

        questionTextView = (TextView) findViewById(R.id.show_answer_question_content);
        title = (TextView) findViewById(R.id.show_answer_title);
        answerA = (TextView) findViewById(R.id.show_answer_answerA);
        answerB = (TextView) findViewById(R.id.show_answer_answerB);
        answerC = (TextView) findViewById(R.id.show_answer_answerC);
        answerD = (TextView) findViewById(R.id.show_answer_answerD);

        correctAnswer = (TextView) findViewById(R.id.show_answer_correct);
        yourAnswer = (TextView) findViewById(R.id.show_answer_your);
        next = (Button) findViewById(R.id.show_answer_nextBtn);
        prev = (Button) findViewById(R.id.show_answer_prevBtn);

        // 获取题目ID。
        Intent intent = getIntent();
        groupID = intent.getIntExtra("groupID", -1);
        recordDao = new RecordDao(ShowAnswerActivity.this);
        questionDao = new QuestionDao(ShowAnswerActivity.this);
        // 我们根据groupID 查USerAnswer表，获取List<UserAnswerResult>
        List<UserAnswerResult> userAnswerResultLists = recordDao.getUserAnswerResultsByGroupID(groupID);
        questionAndUserAnswerList = new ArrayList<>();
        //遍历
        for (UserAnswerResult userAnswerResult : userAnswerResultLists) {
            Question question = questionDao.getQuestion(userAnswerResult.getQuestionID());
            if (question == null) {
                Log.w("xu", "题目不存在");
                Toast.makeText(ShowAnswerActivity.this, "错误，题目不存在", Toast.LENGTH_SHORT).show();
                finish();
            }
            QuestionAndUserAnswer temp = new QuestionAndUserAnswer(question, userAnswerResult);
            questionAndUserAnswerList.add(temp);
        }
        // questionAndUserAnswerList 就是我们这个页面最重要的数据结构。
        realQuestionNum = questionAndUserAnswerList.size();
        index = 0;
        //可以整活了。
        drowView();
        prev.setEnabled(false); //最开始，不能向前。

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index--;
                if (index == 0) {
                    prev.setEnabled(false);
                    next.setEnabled(true);
                } else {
                    prev.setEnabled(true);
                    next.setEnabled(true);
                }
                drowView();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if (index == realQuestionNum - 1) {
                    next.setEnabled(false);
                    prev.setEnabled(true);
                } else {
                    prev.setEnabled(true);
                    next.setEnabled(true);
                }
                drowView();
            }
        });
    }

    private void drowView() {
        // 先绘制初始的首界面。
        // 绘制title内容。
        String firstTitle = String.valueOf(index + 1) + "/" + String.valueOf(realQuestionNum);
        title.setText(firstTitle);
        // 绘制题目内容
        questionTextView.setText(questionAndUserAnswerList.get(index).getQuestion().getQuestionContent());

        //绘制四个选项的内容。
        answerA.setText("A. " + questionAndUserAnswerList.get(index).getQuestion().getChoiceA());
        answerB.setText("B. " + questionAndUserAnswerList.get(index).getQuestion().getChoiceB());
        answerC.setText("C. " + questionAndUserAnswerList.get(index).getQuestion().getChoiceC());
        answerD.setText("D. " + questionAndUserAnswerList.get(index).getQuestion().getChoiceD());

        //绘制答案
        String correct = questionAndUserAnswerList.get(index).getQuestion().getCorrectChoice();
        String user = questionAndUserAnswerList.get(index).getUserAnswerResult().getUserAnswer();
        correctAnswer.setTextColor(0xff0000ff);
        if (user.equals("")) {
            yourAnswer.setText("未作答");
            yourAnswer.setTextColor(0xff0000);
        } else {
            yourAnswer.setText(user);
        }
        correctAnswer.setText(correct);
        //做错了
        if (!user.equals(correct)) {
            yourAnswer.setTextColor(0xffff0000);
            //  yourAnswer.setText(user);
        } else {
            yourAnswer.setTextColor(0xff3f3f3f);
        }
    }

}
