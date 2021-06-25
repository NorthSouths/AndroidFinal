package com.example.myapplication2;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.dao.DatabaseHelper;
import com.example.myapplication2.data.UserInfo;

public class ModifyPwdActivity extends AppCompatActivity {
    private Button modify;
    private EditText oldPwd;
    private EditText newPwd;
    private EditText confirmPwd;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);

        modify = findViewById(R.id.modify_modifyBtn);
        oldPwd = findViewById(R.id.old_pwd);
        newPwd = findViewById(R.id.new_pwd);
        confirmPwd = findViewById(R.id.confirm_pwd);

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPwd();
            }
        });
    }

    private void modifyPwd(){
        String oldPassword = oldPwd.getText().toString();
        String newPassword = newPwd.getText().toString();
        String confirmPassword = confirmPwd.getText().toString();

        if(oldPassword.isEmpty()|| newPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(getBaseContext(),"请输入全部密码",Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper helper = DatabaseHelper.getInstance(getBaseContext(),"AD");
        SQLiteDatabase db = helper.getReadableDatabase();
        UserInfo info = UserInfo.getInstance(getBaseContext());
        String name = info.getName();

        String sql = "select password from User where name = '" + name + "'";
        Cursor cursor = db.rawQuery(sql,null,null);
        while(cursor.moveToNext()){
            if(!cursor.getString(0).equals(oldPassword)){
                cursor.close();
                Toast.makeText(getBaseContext(),"请输入正确的旧密码",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(!newPassword.equals(confirmPassword)){
            Toast.makeText(getBaseContext(),"你输入的两次新密码不一致哦",Toast.LENGTH_SHORT).show();
            return;
        }

        db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("password",newPassword);
        db.update("User",value,"name=?",new String[]{name});
        new AlertDialog.Builder(this).setTitle("嘿，宝贝!").setMessage("你已经成功修改密码啦")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setClass(ModifyPwdActivity.this,LogInActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }
}
