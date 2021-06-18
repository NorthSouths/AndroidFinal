package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.dao.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AnswerInputActivity extends AppCompatActivity {
    private TextView title;
    private Button submit;
    private Button next;
    private EditText input;
    private TextView question;
    private int index = 1;
    private ArrayList<qoc_input> list = new ArrayList<>();
    private String rightAnswer;
    private int rightNum = 0;
    private int wrongNum = 0;
    private final int TOTAL_QUESTION = 5;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_input);

        title = findViewById(R.id.title1);
        submit = findViewById(R.id.submit1);
        next = findViewById(R.id.next1);
        input = findViewById(R.id.answerInput);
        question = findViewById(R.id.question1);

        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(),"qoc");
        SQLiteDatabase db = helper.getReadableDatabase();
        try{
            Cursor cursor = db.query("Numeracy",null,null,null,null,null,null);
            while(cursor.moveToNext()){
                String q = cursor.getString(cursor.getColumnIndex("question"));
                String ra = cursor.getString(cursor.getColumnIndex("rightAnswer"));
                list.add(new qoc_input(q,ra));
            }

            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        updateQuestion();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });
    }

    private void onSubmit(){
        if(input.getText().toString().compareTo(rightAnswer) == 0){
            rightNum++;
        }else{
            wrongNum++;
        }
        UserInfo info = UserInfo.getInstance(getBaseContext());
        int score = Math.max(rightNum*5 - wrongNum*2,0);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String time = format.format(date);
        UserInfo.Record record = info.genRecord("Numeracy",time,score);
        info.storageRecord(record);
        String msg = "非常棒" + info.getName() + ", 你已经完成该乐园中的全部内容，你做出了 "
                + rightNum + " 个正确答案和 " + wrongNum + " 个错误答案 \n 在此乐园里，你最终获得 " + info.getScore() + " 个经验豆";
        new AlertDialog.Builder(this).setTitle("Tip!").setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnswerInputActivity.this.finish();
                    }
                }).show();
    }

    private void nextAnswer(){
        if(input.getText().toString().compareTo(rightAnswer) == 0){
            rightNum++;
        }else{
            wrongNum++;
        }
        updateQuestion();
    }

    private void onNext(){
        if(index > TOTAL_QUESTION){
            Toast.makeText(this,"你已回答完全部问题啦",Toast.LENGTH_SHORT).show();
            return;
        }
        if(input.getText().toString().isEmpty()){
            Toast.makeText(this,"请输入你的答案",Toast.LENGTH_SHORT).show();
        }else{
            nextAnswer();
        }
    }

    private void updateQuestion(){
        title.setText(index + "/" + TOTAL_QUESTION);
        int random = (int)(Math.random() * list.size());
        question.setText(list.get(random).getQuestion());
        rightAnswer = list.get(random).getRightAnswer();
        list.remove(random);
        input.setText("");
        index++;
    }


    private class qoc_input{
        private String question;
        private String rightAnswer;

        public qoc_input(String question_, String rightAnswer_){
            question = question_;
            rightAnswer = rightAnswer_;
        }

        public String getQuestion() {
            return question;
        }


        public String getRightAnswer() {
            return rightAnswer;
        }
    }
}
