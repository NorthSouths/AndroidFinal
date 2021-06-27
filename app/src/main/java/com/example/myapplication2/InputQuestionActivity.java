package com.example.myapplication2;
/*
    author : 181110515 徐禹萌
 */
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        Button bt_check = (Button) findViewById(R.id.bt_inputquestion_check);
        Button bt_cancel = (Button) findViewById(R.id.bt_inputquestion_cancel);
        bt_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputQuestion();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputCancel();
            }
        });
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

    //检测输入是否为空，空则返回true
    public boolean isInputEmpty() {
        //获取各个控件
        EditText ev_title = (EditText) findViewById(R.id.ev_inputquestion_questiontitle);           //问题标题
        EditText ev_ChoiceA = (EditText) findViewById(R.id.ev_inputquestion_choicea);                //选项A
        EditText ev_ChoiceB = (EditText) findViewById(R.id.ev_inputquestion_choiceb);                //选项B
        EditText ev_ChoiceC = (EditText) findViewById(R.id.ev_inputquestion_choicec);                //选项C
        EditText ev_ChoiceD = (EditText) findViewById(R.id.ev_inputquestion_choiced);                //选项D
        EditText ev_Chapter = (EditText) findViewById(R.id.ev_inputquestion_chapter);                //正确选项
        EditText ev_CorrectAnswer = (EditText) findViewById(R.id.ev_inputquestion_correctAnswer);    //问题章节

        if ("".equals(ev_title.getText().toString())) {                                              //依此判断各项是否为空
            Toast.makeText(InputQuestionActivity.this, "请输入问题的题目！", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("".equals(ev_ChoiceA.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "请输入A选项的内容！", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("".equals(ev_ChoiceB.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "请输入B选项的内容！", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("".equals(ev_ChoiceC.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "请输入C选项的内容！", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("".equals(ev_ChoiceD.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "请输入D选项的内容！", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("".equals(ev_CorrectAnswer.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "请输入正确答案的内容！", Toast.LENGTH_SHORT).show();
            return true;
        } else if ("".equals(ev_Chapter.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "请输入问题的章节！", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    //检测输入的选项中是否有正确答案
    public boolean haveAnswer() {
        //获取所有选项和正确选项的内容
        EditText ev_ChoiceA = (EditText) findViewById(R.id.ev_inputquestion_choicea);                //选项A
        EditText ev_ChoiceB = (EditText) findViewById(R.id.ev_inputquestion_choiceb);                //选项B
        EditText ev_ChoiceC = (EditText) findViewById(R.id.ev_inputquestion_choicec);                //选项C
        EditText ev_ChoiceD = (EditText) findViewById(R.id.ev_inputquestion_choiced);                //选项D
        EditText ev_CorrectAnswer = (EditText) findViewById(R.id.ev_inputquestion_correctAnswer);    //正确选项

        String correct = ev_CorrectAnswer.getText().toString();                                     //获取正确选项的内容，与4个选项依次比较
        if (correct.equals(ev_ChoiceA.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "选项A是正确答案！", Toast.LENGTH_SHORT).show();
            return true;
        } else if (correct.equals(ev_ChoiceB.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "选项中B是正确答案！", Toast.LENGTH_SHORT).show();
            return true;
        } else if (correct.equals(ev_ChoiceC.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "选项中C是正确答案！", Toast.LENGTH_SHORT).show();
            return true;
        } else if (correct.equals(ev_ChoiceD.getText().toString())) {
            Toast.makeText(InputQuestionActivity.this, "选项中D是正确答案！", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(InputQuestionActivity.this, "选项中没有正确答案！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //录入问题
    public void InputQuestion() {
        //获取各个组件
        final EditText ev_title = (EditText) findViewById(R.id.ev_inputquestion_questiontitle);
        final EditText ev_ChoiceA = (EditText) findViewById(R.id.ev_inputquestion_choicea);
        final EditText ev_ChoiceB = (EditText) findViewById(R.id.ev_inputquestion_choiceb);
        final EditText ev_ChoiceC = (EditText) findViewById(R.id.ev_inputquestion_choicec);
        final EditText ev_ChoiceD = (EditText) findViewById(R.id.ev_inputquestion_choiced);
        final EditText ev_Chapter = (EditText) findViewById(R.id.ev_inputquestion_chapter);
        final EditText ev_CorrectAnswer = (EditText) findViewById(R.id.ev_inputquestion_correctAnswer);

        if (!isInputEmpty()) {  //首先判断非空，若有空输入则提示用户进行输入，不进行录入
            questionContent = ev_title.getText().toString();
            questionChapter = Integer.valueOf(ev_Chapter.getText().toString());
            choiceA = ev_ChoiceA.getText().toString();
            choiceB = ev_ChoiceB.getText().toString();
            choiceC = ev_ChoiceC.getText().toString();
            choiceD = ev_ChoiceD.getText().toString();
            correctChoice = ev_CorrectAnswer.getText().toString();

            if (haveAnswer()) {   //当用户输入非空时，检查选项中是否存在正确答案
                Toast.makeText(InputQuestionActivity.this, "选项中存在正确答案！", Toast.LENGTH_LONG).show();
                Question question = new Question(0, questionContent, questionChapter, choiceA, choiceB, choiceC, choiceD, correctChoice);
                questionDao.insertQuestion(question);

                AlertDialog.Builder builder = new AlertDialog.Builder(InputQuestionActivity.this);
                builder.setTitle("添加成功！");
                builder.setMessage("已经成功添加一道题目，是否继续？");

                builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ev_title.setText("");
                        ev_ChoiceA.setText("");
                        ev_ChoiceB.setText("");
                        ev_ChoiceC.setText("");
                        ev_ChoiceD.setText("");
                        ev_CorrectAnswer.setText("");
                        ev_Chapter.setText("");
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    //结束录入
    public void InputCancel() {                   //弹出对话框，点击确定结束录入回到MainActivity，点击取消留在当前界面
        AlertDialog.Builder builder = new AlertDialog.Builder(InputQuestionActivity.this);
        builder.setTitle("警告");
        builder.setMessage("确定要结束题目录入嘛？");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
//                Intent intent = new Intent();
//                intent.setClass(InputQuestionActivity.this,MainActivity.class);
//                startActivity(intent);
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

}