package com.example.myapplication2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication2.database.DatabaseHelper;

import java.text.DateFormat;
import java.util.ArrayList;

public class UserInfo {
    private static UserInfo Instance;
    private Context context;

    public static synchronized UserInfo getInstance(Context context){
        if(Instance == null){
            Instance = new UserInfo(context);
        }
        return Instance;
    }

    private UserInfo(Context context_){
        context = context_;
    }

    public void initInfo(String name_){
        name = name_;
    }

    public String getName() {
        return name;
    }

    private String name;

    public Record genRecord(String type_, String time_, int score_){
        return new Record(type_,time_,score_);
    }


    public void storageRecord(Record record){
        DatabaseHelper helper = DatabaseHelper.getInstance(context,"qoc");
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user",name);
        values.put("type",record.getType());
        values.put("time",record.getTime());
        values.put("score",record.getScore());
        db.insert("Record",null,values);
    }

    public ArrayList<String> getRecord(){
        UserInfo info = UserInfo.getInstance(context);
        String name = info.getName();

        DatabaseHelper helper = DatabaseHelper.getInstance(context,"qoc");
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from Record where user= '"  + name + "' order by score";
        Cursor cursor = db.rawQuery(sql,null);

        ArrayList<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int score = cursor.getInt(cursor.getColumnIndex("score"));
            list.add("\"" + type +"\" 乐园——你于 " + time + " 获得 " + score+"经验豆");
        }
        cursor.close();
        return list;
    }

    public int getScore(){
        DatabaseHelper helper = DatabaseHelper.getInstance(context,"qoc");
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select SUM(score) AS scores from Record where user=?";
        Cursor cursor = db.rawQuery(sql,new String[]{name});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("scores"));
    }



    public class Record{
        public String getType() {
            return type;
        }

        public String getTime() {
            return time;
        }

        public int getScore() {
            return score;
        }

        private String type;
        private String time;
        private int score;

        public Record(String type_, String time_, int score_){
            type = type_;
            time = time_;
            score = score_;
        }


    }
}
