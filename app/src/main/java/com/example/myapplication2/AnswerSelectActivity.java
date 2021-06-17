package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AnswerSelectActivity extends AppCompatActivity {
    private TextView question;
    private TextView title;
    private RadioGroup group;
    private RadioButton[] answerBtn = new RadioButton[5];
    private Button submit;
    private Button next;
    private String type = "批判思维";
    private ArrayList<qoc_select> list = new ArrayList<>();
    private int index = 1;
    private String rightAnswer;
    private int rightNum;
    private int wrongNum;
    private int checkedIndex;
    private final int TOTAL_QUESTION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("qoc","answer activity select create");
        super.onCreate(savedInstanceState);

        final Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey("type")){
                type = (String)bundle.get("type");
            }
        }
        setContentView(R.layout.activity_answer_select);

        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(),"qoc");
        SQLiteDatabase db = helper.getReadableDatabase();
        try{
            Cursor cursor = db.query(type,null,null,null,null,null,null);
            while(cursor.moveToNext()){
                String q = cursor.getString(cursor.getColumnIndex("question"));
                String a1 = cursor.getString(cursor.getColumnIndex("answerA"));
                String a2 = cursor.getString(cursor.getColumnIndex("answerB"));
                String a3 = cursor.getString(cursor.getColumnIndex("answerC"));
                String a4 = cursor.getString(cursor.getColumnIndex("answerD"));
                String a5 = cursor.getString(cursor.getColumnIndex("answerE"));
                String ra = cursor.getString(cursor.getColumnIndex("rightAnswer"));
                list.add(new qoc_select(q,a1,a2,a3,a4,a5,ra));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        rightNum = 0;
        wrongNum = 0;
        question = findViewById(R.id.question2);
        group = findViewById(R.id.answerGroup);
        answerBtn[0] = findViewById(R.id.answerA);
        answerBtn[1] = findViewById(R.id.answerB);
        answerBtn[2] = findViewById(R.id.answerC);
        answerBtn[3] = findViewById(R.id.answerD);
        answerBtn[4] = findViewById(R.id.answerE);
        submit = findViewById(R.id.submit2);
        next = findViewById(R.id.next2);
        title = findViewById(R.id.title2);

        updateQuestion();

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == answerBtn[0].getId()){
                     checkedIndex = 0;
                }else if(checkedId == answerBtn[1].getId()){
                    checkedIndex = 1;
                }else if(checkedId == answerBtn[2].getId()){
                    checkedIndex = 2;
                }else if(checkedId == answerBtn[3].getId()){
                    checkedIndex = 3;
                }else if(checkedId == answerBtn[4].getId()){
                    checkedIndex = 4;
                }
            };
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerBtn[checkedIndex].getText().toString().compareTo(rightAnswer) == 0){
                    rightNum++;
                    Log.i("qoc","right");
                }else{
                    wrongNum++;
                    Log.i("qoc","wrong");
                }
                UserInfo info = UserInfo.getInstance(getBaseContext());
                int score = Math.max(rightNum*5 - wrongNum*2,0);
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String time = format.format(date);
                UserInfo.Record record = info.genRecord(type,time,score);
                info.storageRecord(record);
                String msg = "非常棒" + info.getName() + ", 你已经完成该乐园中的全部内容，你做出了 "
                        + rightNum + " 个正确答案和 " + wrongNum + " 个错误答案 \n 在此乐园里，你最终获得 " + info.getScore() + " 个经验豆";
                new AlertDialog.Builder(AnswerSelectActivity.this).setTitle("Tip!").setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AnswerSelectActivity.this.finish();
                            }
                        }).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("qoc","answer activity is destroyed");
    }

    private void onNext(){
        if(index > TOTAL_QUESTION){
            Toast.makeText(this,"你已完成全部内容",Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isChecked = false;
        for (RadioButton button:answerBtn) {
            if(button.isChecked()) isChecked = true;
        }
        if(!isChecked){
            Toast.makeText(this,"请选择一个选项",Toast.LENGTH_SHORT).show();
        }else{
            nextAnswer();
        }
    }

    private void updateQuestion(){
        int random = (int)(Math.random() * list.size());
        qoc_select q = list.get(random);
        if(q == null)  return;
        title.setText(index + "/" + TOTAL_QUESTION);
        question.setText(q.getQuestion());
        answerBtn[0].setText(q.getAnswerA());
        answerBtn[1].setText(q.getAnswerB());
        answerBtn[2].setText(q.getAnswerC());
        answerBtn[3].setText(q.getAnswerD());
        answerBtn[4].setText(q.getAnswerE());
        rightAnswer = q.getRightAnswer();
        index++;
        list.remove(random);
        group.clearCheck();
        group.clearCheck();
    }

    private void nextAnswer(){
        if(answerBtn[checkedIndex].getText().toString().compareTo(rightAnswer) == 0){
            rightNum++;
            Log.i("qoc","right");
        }else{
            wrongNum++;
            Log.i("qoc","wrong");
        }
        updateQuestion();
    }

    private class qoc_select{
        private String question;
        private String answerA;
        private String answerB;
        private String answerC;
        private String answerD;
        private String answerE;
        private String rightAnswer;

        public qoc_select(String question_, String answerA_, String answerB_,String answerC_,String answerD_,String answerE_, String rightAnswer_){
            question = question_;
            answerA = answerA_;
            answerB = answerB_;
            answerC = answerC_;
            answerD = answerD_;
            answerE = answerE_;
            rightAnswer = rightAnswer_;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswerA() {
            return answerA;
        }

        public String getAnswerB() {
            return answerB;
        }

        public String getAnswerC() {
            return answerC;
        }

        public String getAnswerD() {
            return answerD;
        }

        public String getAnswerE() {
            return answerE;
        }

        public String getRightAnswer() {
            return rightAnswer;
        }
    }
}

