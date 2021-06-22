package com.example.myapplication2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication2.dao.DatabaseHelper;

import java.util.ArrayList;

public class UserInfo {
    private static UserInfo Instance;
    private Context context;
    private String name;

    public static synchronized UserInfo getInstance(Context context) {
        if (Instance == null) {
            Instance = new UserInfo(context);
        }
        return Instance;
    }

    private UserInfo(Context context_) {
        context = context_;
    }

    public void initInfo(String name_) {
        name = name_;
    }

    public String getName() {
        return name;
    }




    public ArrayList<String> getRecord() {
        UserInfo info = UserInfo.getInstance(context);
        String name = info.getName();

        DatabaseHelper helper = DatabaseHelper.getInstance(context, "qoc");
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from Record where user= '" + name + "' order by score";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
          //  String type = cursor.getString(cursor.getColumnIndex("type"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int score = cursor.getInt(cursor.getColumnIndex("score"));
            list.add("你于 " + time + " 获得 " + score + "经验豆");
        }
        cursor.close();
        return list;
    }

    public int getScore() {
        DatabaseHelper helper = DatabaseHelper.getInstance(context, "qoc");
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select SUM(score) AS scores from Record where user=?";
        Cursor cursor = db.rawQuery(sql, new String[]{name});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("scores"));
    }


}
