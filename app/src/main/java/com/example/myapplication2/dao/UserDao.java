package com.example.myapplication2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.myapplication2.RegisterActivity;
import com.example.myapplication2.data.User;

public class UserDao {
    private final DatabaseHelper dbhelper;
    private final String DBName = "qoc";
    private SQLiteDatabase db;

    public UserDao(Context context) {
        dbhelper = DatabaseHelper.getInstance(context, DBName);
    }

    //1.判断某项内容是否存在
    public boolean isExistSth(String target, String Type) {
        db = dbhelper.getReadableDatabase();
        String temp;
        Cursor cursor = db.rawQuery("select " + Type + " from " + DatabaseHelper.USERTABLE, null);
        boolean registered = false;
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            temp = cursor.getString(cursor.getColumnIndex(Type));
            if (target.compareTo(temp) == 0) {
                registered = true;
                break;
            }
        }
        cursor.close();
        db.close();
        return registered;
    }

    // 2. 插入记录
    public void insertUser(User user) {
        db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NAME, user.getName());
        values.put(DatabaseHelper.PHONENUM, user.getPhoneNum());
        values.put(DatabaseHelper.EMAIL, user.getEmail());
        values.put(DatabaseHelper.PASSWORD, user.getPassword());

        db.insert(DatabaseHelper.USERTABLE, null, values);
        db.close();

    }


    //3.登录操作的控制，输入用户名和密码，返回是否验证成功。
    public boolean isVaildUser(String name, String password) {
        db = dbhelper.getReadableDatabase();
        boolean vaild = false;
        Cursor cursor = null;
        String[] args = new String[2];
        args[0] = name;
        args[1] = password;
        try {
            cursor = db.query("User", null, "name=? and password = ?", args, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (cursor.moveToNext()) {
            vaild = true;
        }
        cursor.close();
        db.close();
        return vaild;
    }

    // 4.保留的接口。
    public int getScore(String username) {

        db = dbhelper.getReadableDatabase();
        String sql = "select SUM(score) AS scores from Record where user=?";
        Cursor cursor = db.rawQuery(sql, new String[]{username});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SCORE));
    }
}
