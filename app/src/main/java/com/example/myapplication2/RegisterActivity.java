package com.example.myapplication2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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

public class RegisterActivity extends AppCompatActivity {

    private Button returnBtn;
    private Button registerBtn;
    private EditText emailText;
    private EditText nameText;
    private EditText passwordText;
    private Button clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        registerBtn = findViewById(R.id.register_registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });

        returnBtn = findViewById(R.id.register_returnLoginBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        emailText = findViewById(R.id.register_inputEmail);
        nameText = findViewById(R.id.register_inputUserName);
        passwordText = findViewById(R.id.register_inputPwd);

    }

    private void onRegister() {
        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(), "qoc");
        SQLiteDatabase dbReader = helper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery("select name from User", null);
        String name = "";
        boolean registered = false;
        final String inputName = nameText.getText().toString();
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("name"));
            if (inputName.compareTo(name) == 0) {
                registered = true;
                break;
            }
        }

        if (registered) {
            new AlertDialog.Builder(this).setTitle("嘿，宝贝!").setMessage("你已经注册成功啦，赶快登陆吧！")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this, LogInActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name", inputName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }).show();
        } else {
            final String inputEmail = emailText.getText().toString();
            final String inputPassword = passwordText.getText().toString();
            if (inputName.isEmpty() || inputEmail.isEmpty() || inputPassword.isEmpty()) {
                Toast toast = Toast.makeText(RegisterActivity.this, "请输入全部信息", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            if (!inputEmail.contains("@")) {
                Toast toast = Toast.makeText(RegisterActivity.this, "邮箱格式不正确哦～", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            SQLiteDatabase dbWriter = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", inputName);
            values.put("email", inputEmail);
            values.put("password", inputPassword);
            dbWriter.insert("User", null, values);
            Dialog dialog = new AlertDialog.Builder(this).setTitle("嘿，宝贝!").setMessage("你已经注册成功啦，赶快登陆吧！")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this, LogInActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name", inputName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }).show();
        }
    }

    private void clear() {
        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(), "qoc");
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] args = new String[1];
        args[0] = nameText.getText().toString();
        db.delete("User", "name=?", args);
        Toast.makeText(RegisterActivity.this, "delete user " + args[0] + " succeed", Toast.LENGTH_SHORT).show();
    }


}
