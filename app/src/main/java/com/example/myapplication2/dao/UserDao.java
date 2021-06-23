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


    //3.
}
