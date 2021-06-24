package com.example.myapplication2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private Button confirmBtn;
    private EditText nameText;
    private EditText verText;
    private EditText pwdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);

        returnBtn = findViewById(R.id.forget_returnBtn);
        confirmBtn = findViewById(R.id.forget_confirmBtn);
        nameText = findViewById(R.id.forget_inputUserName);
        pwdText = findViewById(R.id.forget_inputPwd);
        verText = findViewById(R.id.forget_verNum);

        final Bundle bundle = getIntent().getExtras();
        for(int i = 0; i < 4; i++){
            verNum += (char)('0' + (int)(Math.random() * 10));
        }
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        String channelId = createNotificationChannel("my_channel_ID", "my_channel_NAME", NotificationManager.IMPORTANCE_HIGH);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("验证码")
                .setContentText(String.valueOf(verNum))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(100, notification.build());


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

    void forgetPwd(){
        String verNum_ = verText.getText().toString();
        String userName = nameText.getText().toString();
        String pwd = pwdText.getText().toString();

        if(userName.isEmpty()){
            Toast.makeText(ForgetActivity.this, "清输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pwd.isEmpty()){
            Toast.makeText(ForgetActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwd.length() < 6) {
            Toast.makeText(ForgetActivity.this, "密码至少需要6个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(),"AD");
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select password from User where name = '" + userName + "'";
        Cursor cursor = db.rawQuery(sql,null,null);
        boolean flag = false;
        while(cursor.moveToNext()){
            flag = true;
        }

        if(!flag){
            Toast.makeText(ForgetActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if(verNum.compareTo(verNum_) != 0){
            Toast.makeText(ForgetActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("password",pwd);
        db.update("User",value,"name=?",new String[]{userName});
        new AlertDialog.Builder(this).setTitle("嘿，宝贝!").setMessage("你已经成功修改密码啦")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setClass(ForgetActivity.this,LogInActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

    private String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }
}
