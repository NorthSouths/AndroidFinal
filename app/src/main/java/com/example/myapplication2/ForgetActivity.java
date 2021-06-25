package com.example.myapplication2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication2.dao.DatabaseHelper;

public class ForgetActivity extends AppCompatActivity {

    private String verNum = "";
    private Button returnBtn;
    private Button confirmBtn, getCodeBtn;
    private EditText phoneNumberText, verText, pwdText;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);
        returnBtn = findViewById(R.id.forget_returnBtn);
        confirmBtn = findViewById(R.id.forget_confirmBtn);
        getCodeBtn = findViewById(R.id.getCodeBtn);
        phoneNumberText = findViewById(R.id.forget_inputUserPhone);
        pwdText = findViewById(R.id.forget_inputPwd);
        verText = findViewById(R.id.forget_verNum);


        for (int i = 0; i < 6; i++) {
            verNum += (char) ('0' + (int) (Math.random() * 10));
        }

        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNum = phoneNumberText.getText().toString();
               // SmsManager massage = SmsManager.getDefault();
              // massage.sendTextMessage(phoneNum, null, verNum, null, null);
                Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
              //  intent.setAction(Intent.ACTION_SENDTO);
                intent.setType("vnd.android-dir/mms-sms");
               // intent.setData(Uri.parse("smsto:" + phoneNum));
                intent.putExtra("sms_body", verNum);
                intent.putExtra("address", phoneNum);
                startActivity(intent);
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPwd();
            }
        });
    }

    void forgetPwd() {
        String verNum_ = verText.getText().toString();
        String phoneNum = phoneNumberText.getText().toString();
        String pwd = pwdText.getText().toString();

        if (phoneNum.isEmpty()) {
            Toast.makeText(ForgetActivity.this, "清输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwd.isEmpty()) {
            Toast.makeText(ForgetActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwd.length() < 6) {
            Toast.makeText(ForgetActivity.this, "密码至少需要6个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(), "AD");
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select password from User where phonenum = '" + phoneNum + "'";
        Cursor cursor = db.rawQuery(sql, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }

        if (!flag) {
            Toast.makeText(ForgetActivity.this, "手机号不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verNum.compareTo(verNum_) != 0) {
            Toast.makeText(ForgetActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("password", pwd);
        db.update("User", value, "name=?", new String[]{phoneNum});
        new AlertDialog.Builder(this).setTitle("嘿，宝贝!").setMessage("你已经成功修改密码啦")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setClass(ForgetActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

}
