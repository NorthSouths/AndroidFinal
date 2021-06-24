package com.example.myapplication2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.dao.DatabaseHelper;
import com.example.myapplication2.dao.UserDao;

public class LogInActivity extends AppCompatActivity {
    private Button logInBtn, registerBtn, forgetPwdBtn;
    private EditText nameText;
    private EditText passwordText;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        registerBtn = findViewById(R.id.login_registerBtn);
        logInBtn = findViewById(R.id.login_logInBtn);
        forgetPwdBtn = findViewById(R.id.login_fogPwdBtn);
        userDao = new UserDao(LogInActivity.this);
        forgetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgetPwd();
            }
        });
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogIn();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegister();
            }
        });
        nameText = findViewById(R.id.inputUserName);
        passwordText = findViewById(R.id.inputPwd);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("name")) {
                String name = (String) bundle.get("name");
                nameText.setText(name);
            }
        }

    }

    private void onRegister() {
        Intent intent = new Intent();
        intent.setClass(LogInActivity.this, RegisterActivity.class);
        startActivity(intent);
        // 不结束。放弃注册返回该页面。
    }

    private void onForgetPwd() {
        Intent intent = new Intent();
        intent.setClass(LogInActivity.this, ForgetActivity.class);
        startActivity(intent);
    }

    private void onLogIn() {
        final String inputName = nameText.getText().toString();
        final String inputPassword = passwordText.getText().toString();

        if (inputName.isEmpty() || inputPassword.isEmpty()) {
            Toast toast = Toast.makeText(LogInActivity.this, "请完成全部信息", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {

            boolean log = userDao.isVaildUser(inputName, inputPassword);
            if (log) {
                UserInfo info = UserInfo.getInstance(getBaseContext());
                info.initInfo(inputName);
                Intent intent = new Intent();
                intent.setClass(LogInActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", inputName);
                startActivity(intent, bundle);

            } else {
                Toast toast = Toast.makeText(LogInActivity.this, "账号或密码不正确", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
    }
}
