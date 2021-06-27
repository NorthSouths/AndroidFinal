package com.example.myapplication2;
/*
    author : 181110514 徐佳辰
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.dao.QuestionDao;
import com.example.myapplication2.dao.RecordDao;
import com.example.myapplication2.data.Question;
import com.example.myapplication2.data.Record;
import com.example.myapplication2.data.UserAnswerResult;


import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class AnswerSelectActivity extends AppCompatActivity {
    private TextView questionTextView, title;
    private RadioGroup group;
    private RadioButton[] answerBtn = new RadioButton[4];
    private Button submit;
    private Button next;

    private int index = 1;
    private String currentRightAnswer = "";
    private Question currentQuestion = null;
    private int rightNum;
    private int wrongNum;
    private int checkedIndex;
    private String chosenType;
    private int chosenChapter;
    private int chosenQuestionNum = 5;
    private int realQuestionNum = 5; //测试用数据
    private QuestionDao questionDao; //DAO层
    private RecordDao recordDao;
    private List<Question> questions;
    private List<UserAnswerResult> userAnswerResults;
    private long exitTime; //"再按一次返回"
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exitTime = System.currentTimeMillis();
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("type")) {
                chosenType = (String) bundle.get("type");
            }
            if (bundle.containsKey("chapter")) {
                chosenChapter = (int) bundle.get("chapter");
            }
            if (bundle.containsKey("name")) {
                userName = (String) bundle.get("name");
            }
        }
        setContentView(R.layout.activity_answer_select);
        questionTextView = findViewById(R.id.challenge_question_content);
        group = findViewById(R.id.answerGroup);
        answerBtn[0] = findViewById(R.id.answerA);
        answerBtn[1] = findViewById(R.id.answerB);
        answerBtn[2] = findViewById(R.id.answerC);
        answerBtn[3] = findViewById(R.id.answerD);
        submit = findViewById(R.id.challenge_submit);
        next = findViewById(R.id.challenge_next);
        title = findViewById(R.id.challenge_title);

        questionDao = new QuestionDao(getBaseContext()); //实例化DAO层。
        recordDao = new RecordDao(getBaseContext());
        questions = new ArrayList<Question>();
        userAnswerResults = new ArrayList<UserAnswerResult>();
        // 我们依据传进来的Type，来进行随机测试或按章节测试。
        // 预定的测试类型为 “random” 或 “chapter”

        switch (chosenType) {
            case "random":
                //随机测试，首先要获得随机的题目数量。这是用文件来读取的。
                chosenQuestionNum = getRandomQuestionNumber();
                //下一步需要调用DAO层，获取题库中题目的数量。
                int totalQuestionNum = questionDao.getTotalQuestionCount();
                if (chosenQuestionNum > totalQuestionNum) {
                    Log.w("xu", "题目不足，希望不要这样。");
                    chosenQuestionNum = totalQuestionNum;
                    questions = questionDao.getAllQuestion();
                } else {
                    // 生成不重复的随机数。

                    Set<Integer> randomSet = new HashSet<>();
                    Random r = new Random();
                    r.setSeed(System.currentTimeMillis());
                    String testNum = "全部：" + String.valueOf(totalQuestionNum) + "\n";
                    while (randomSet.size() != chosenQuestionNum) {
                        int number = r.nextInt(totalQuestionNum) + 1;
                        randomSet.add(number);
                        testNum += String.valueOf(number) + " ";
                    }
                    List<Integer> randomList = new ArrayList<Integer>(randomSet);
                 //   Toast.makeText(AnswerSelectActivity.this, testNum, Toast.LENGTH_SHORT).show(); //输出题目ID。注意，由于set是无序的，顺序不可能跟输出的顺序一致。
                    questions = questionDao.getQuestionList(randomList);
                    if (questions == null) {
                        Toast.makeText(AnswerSelectActivity.this, "严重错误，没有取到题目", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        realQuestionNum = questions.size();
                    }
                }

                break;
            case "chapter":
                questions = questionDao.getQuestionListByChapter(chosenChapter);
                chosenQuestionNum = realQuestionNum = questions.size();
                break;
            default:
        }

        // 现在，我们有了如下内容：
        // 这次要测试的题目列表——questions
        // 这次题目的总个数 —— realQuestionNum
        // 还有吗？
        // 开始整活
        if (questions.size() == 0) {
            Toast.makeText(AnswerSelectActivity.this, "错误，未获取到题目", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 此时 我们的Questions里边就应该有了内容。

        rightNum = 0;
        wrongNum = 0;

        updateQuestion();
        // 绑定按钮变化的事件。实时监测用户的选择
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == answerBtn[0].getId()) {
                    checkedIndex = 0;
                } else if (checkedId == answerBtn[1].getId()) {
                    checkedIndex = 1;
                } else if (checkedId == answerBtn[2].getId()) {
                    checkedIndex = 2;
                } else if (checkedId == answerBtn[3].getId()) {
                    checkedIndex = 3;
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击提交进行警告。

                AlertDialog.Builder builder = new AlertDialog.Builder(AnswerSelectActivity.this);
                builder.setTitle("警告");
                builder.setMessage("确定要提交嘛？");

                builder.setPositiveButton("确定提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        continueSubmit();//继续提交就行了
                        return;
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index > realQuestionNum) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerSelectActivity.this);
                    builder.setTitle("您已做完全部题目");
                    builder.setMessage("是否提交？");

                    builder.setPositiveButton("确定提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            continueSubmit();//继续提交就行了
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            return;
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                boolean isChecked = false;
                for (RadioButton button : answerBtn) {
                    if (button.isChecked()) isChecked = true;
                }
                if (!isChecked) {
                    Toast.makeText(AnswerSelectActivity.this, "请选择一个选项", Toast.LENGTH_SHORT).show();
                } else {
                    nextAnswer();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        exitTime = System.currentTimeMillis();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出答题", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private void continueSubmit() {
        String userAnswer = answerBtn[checkedIndex].getText().toString();
        // 最后一道题的内容，是在提交按钮判别的。
        boolean isChecked = false;
        for (RadioButton button : answerBtn) {
            if (button.isChecked()) isChecked = true;
        }
        if (!isChecked) {
            userAnswer = "";
        }
        if (userAnswer.compareTo(currentRightAnswer) == 0) {
            rightNum++;
            Toast.makeText(AnswerSelectActivity.this, "回答正确", Toast.LENGTH_SHORT).show();
            Log.i("xu", "right");
        } else {
            wrongNum++;
            Toast.makeText(AnswerSelectActivity.this, "回答错误，正确答案为" + currentRightAnswer, Toast.LENGTH_SHORT).show();
            Log.i("xu", "wrong");
        }
        UserAnswerResult userAnswerResult = new UserAnswerResult(currentQuestion.getQuestionID(), userAnswer);
        userAnswerResults.add(userAnswerResult);
        if (index != realQuestionNum) {
            // 把没答题的也加进去
            for (int i = index - 1; i < realQuestionNum; i++) {
                userAnswerResults.add(new UserAnswerResult(questions.get(i).getQuestionID(), "")); //不要用null当数据库字段，用空字符串代表没有内容。
            }
        }


        int score = (int) (rightNum / (double) realQuestionNum * 100);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = format.format(date);
        Record record = new Record(userName, time, score);
        recordDao.storageRecord(record);

        int currentGroupID = recordDao.getAutpIncreatedRecordGroupID();
        if (currentGroupID == 0) {
            Toast.makeText(AnswerSelectActivity.this, "严重错误，自增ID不正确", Toast.LENGTH_SHORT).show();
        }
        recordDao.storageUserAnswer(currentGroupID, userName, userAnswerResults);
        String msg = userName + ", 你已经做完了该练习中的全部内容，你做出了 "
                + rightNum + " 个正确答案和 " + wrongNum + " 个错误答案 \n 本次成绩：" + score + " 分！！！";
        new AlertDialog.Builder(AnswerSelectActivity.this).setTitle("Tip!").setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnswerSelectActivity.this.finish();
                    }
                }).show();
    }

    //此函数负责绘制界面，还有更新正确答案？
    private void updateQuestion() {
        String titleStr = String.valueOf(index) + "/" + String.valueOf(realQuestionNum);
        title.setText(titleStr); //设置标题
        this.currentQuestion = questions.get(index - 1);
        questionTextView.setText(currentQuestion.getQuestionContent());
        answerBtn[0].setText(currentQuestion.getChoiceA());
        answerBtn[1].setText(currentQuestion.getChoiceB());
        answerBtn[2].setText(currentQuestion.getChoiceC());
        answerBtn[3].setText(currentQuestion.getChoiceD());
        this.currentRightAnswer = currentQuestion.getCorrectChoice();
        index++;
        group.clearCheck();
    }

    private void nextAnswer() {
        if (index > realQuestionNum) {
            //小心点，双保险，别越界
            return;
        }
        String userAnswer = answerBtn[checkedIndex].getText().toString();
        if (userAnswer.compareTo(this.currentRightAnswer) == 0) {
            rightNum++;
            Toast.makeText(AnswerSelectActivity.this, "回答正确", Toast.LENGTH_SHORT).show();
            Log.i("qoc", "right");
        } else {
            wrongNum++;
            Toast.makeText(AnswerSelectActivity.this, "回答错误，正确答案为" + currentRightAnswer, Toast.LENGTH_SHORT).show();
            Log.i("qoc", "wrong");
        }
        UserAnswerResult userAnswerResult = new UserAnswerResult(currentQuestion.getQuestionID(), userAnswer);
        this.userAnswerResults.add(userAnswerResult);
        updateQuestion();
    }

    private int getRandomQuestionNumber() {
        int result = 5; //默认5个题。
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            fis = openFileInput("questionNum");
            buffer = new byte[fis.available()];
            fis.read(buffer);
        } catch (Exception e) {
            Toast.makeText(AnswerSelectActivity.this, "可以在“用户”中设置随机题目的数量", Toast.LENGTH_SHORT).show();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (buffer != null) {
            String data = new String(buffer);
            String datatype = data.split(" ")[0];
            String num = data.split(" ")[1];
            //randomQuestionNum 随机题目的个数。
            if (datatype.trim().equals("randomQuestionNum")) {
                result = Integer.parseInt(num.trim());
            }
        } else {
            result = 5;
        }
        if (result >= 50) {
            result = 50;
        }
        return result;
    }


}

