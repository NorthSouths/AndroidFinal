package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowAnswerActivity extends AppCompatActivity {
    private TextView tv;
    private int groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answer);
        tv = (TextView) findViewById(R.id.showGroupID);
        Intent intent = getIntent();
        groupID = intent.getIntExtra("groupID", -1);
        tv.setText(String.valueOf(groupID));
    }

}
