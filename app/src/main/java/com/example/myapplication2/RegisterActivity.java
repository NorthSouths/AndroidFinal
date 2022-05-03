package com.example.myapplication2;
/*
    author : 2191110329 曲铭倩
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication2.dao.DatabaseHelper;
import com.example.myapplication2.dao.UserDao;
import com.example.myapplication2.data.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private Button returnBtn;
    private Button registerBtn;
    private EditText emailText;
    private EditText nameText;
    private EditText passwordText, passwordRepeat;
    private EditText phoneNumText;
    private TextView lable;
    private char[] verNum = new char[4];
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        userDao = new UserDao(RegisterActivity.this);

        lable = findViewById(R.id.register_pwdRepeatLabel);

        registerBtn = findViewById(R.id.register_confirmBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });

        returnBtn = findViewById(R.id.register_returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        phoneNumText = (EditText) findViewById(R.id.register_inputPhone);
        emailText = (EditText) findViewById(R.id.register_inputEmail);
        nameText = (EditText) findViewById(R.id.register_inputUserName);
        passwordText = (EditText) findViewById(R.id.register_inputPwd);
        passwordRepeat = (EditText) findViewById(R.id.register_inputPwdRepeat);

    }

    private void onRegister() {

        final String inputName = nameText.getText().toString();
        String inputEmail = emailText.getText().toString();
        String inputPassword = passwordText.getText().toString();
        String inputPhoneNum = phoneNumText.getText().toString();
        String inputPwdRepeat = passwordRepeat.getText().toString();

        // 不能为空
        if (inputName.isEmpty() || inputEmail.isEmpty() || inputPassword.isEmpty() || inputPhoneNum.isEmpty()) {
            Toast toast = Toast.makeText(RegisterActivity.this, "请输入完整信息", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (inputName.length() < 2) {
            Toast.makeText(RegisterActivity.this, "用户名至少需要2个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (inputPassword.length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码至少需要6个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!inputPassword.equals(inputPwdRepeat)) {
            Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        // 判断邮箱
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(inputEmail);
        if (!m.matches()) {
            Toast.makeText(RegisterActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        // 手机号得是数字
        for (int i = 0; i < inputPhoneNum.length(); i++) {
            if (inputPhoneNum.charAt(i) > '9' || inputPhoneNum.charAt(i) < '0') {
                Toast.makeText(RegisterActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (userDao.isExistSth(inputName, "name")) {
            Toast.makeText(RegisterActivity.this, "用户名已被注册", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userDao.isExistSth(inputPhoneNum, "phonenum")) {
            Toast.makeText(RegisterActivity.this, "手机号已被注册", Toast.LENGTH_SHORT).show();
            return;
        }

        //插入记录。
        User newUser = new User(inputName, inputPhoneNum, inputEmail, inputPassword);
        userDao.insertUser(newUser);
        Dialog dialog = new AlertDialog.Builder(this).setTitle("注册成功").setMessage("你已经注册成功啦，赶快登陆吧！")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(RegisterActivity.this, LogInActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", inputName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }


    private void clear() {
        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(), "AD");
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] args = new String[1];
        args[0] = nameText.getText().toString();
        db.delete("User", "name=?", args);
        Toast.makeText(RegisterActivity.this, "delete user " + args[0] + " succeed", Toast.LENGTH_SHORT).show();
    }

}
