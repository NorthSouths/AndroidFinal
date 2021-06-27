package com.example.myapplication2;
/*
    author : 181110515 徐禹萌
 */
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.dao.UserDao;

public class ModifyPwdActivity extends AppCompatActivity {
    private Button returnBtn, modifyBtn;
    private EditText oldPwdEdit, newPwdEdit, confirmPwdEdit;
    private String name;
    private UserDao userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        userDao = new UserDao(ModifyPwdActivity.this);
        modifyBtn = findViewById(R.id.modify_confirmBtn);
        returnBtn = findViewById(R.id.modify_returnBtn);
        oldPwdEdit = findViewById(R.id.modify_inputOldPwd);
        newPwdEdit = findViewById(R.id.modify_inputNewPwd);
        confirmPwdEdit = findViewById(R.id.modify_inputPwdRepeat);
        name = getIntent().getStringExtra("name");
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onModifyPwd();
            }
        });
    }

    private void onModifyPwd() {
        String oldPassword = oldPwdEdit.getText().toString();
        String newPassword = newPwdEdit.getText().toString();
        String confirmPassword = confirmPwdEdit.getText().toString();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getBaseContext(), "请将信息填写完整", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getBaseContext(), "你输入的两次新密码不一致哦", Toast.LENGTH_SHORT).show();
            return;
        }
        // 根据用户名，查找密码

        if (!oldPassword.equals(userDao.getUserPwd(name, "name"))) {
            Toast.makeText(getBaseContext(), "原密码不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        userDao.updateUserPwd(name, "name", newPassword);
        new AlertDialog.Builder(this).setTitle("Tips!").setMessage("修改密码成功！")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }
}
