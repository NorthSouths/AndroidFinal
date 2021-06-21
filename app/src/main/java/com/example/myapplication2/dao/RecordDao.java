package com.example.myapplication2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication2.data.Record;
import com.example.myapplication2.data.UserAnswerResult;

import java.util.List;

// 操作record表和Answer表的内容。
public class RecordDao {
    private final DatabaseHelper dbhelper;
    private final String DBName = "qoc";
    private SQLiteDatabase db;

    public RecordDao(Context context) {
        dbhelper = DatabaseHelper.getInstance(context, DBName);
    }

    //对record表的操作
    //1.接受一个record，将其存入record表中
    public void storageRecord(Record record) {
        db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USER, record.getUser());
        values.put(DatabaseHelper.TIME, record.getTime());
        values.put(DatabaseHelper.SCORE, record.getScore());
        db.insert(DatabaseHelper.RECORDTABLE, null, values);
        db.close();
    }

    // 2.返回Record表最新的自增主键值。
    public int getAutpIncreatedRecordGroupID() {
        db = dbhelper.getReadableDatabase();
        String sql = "select max(" + DatabaseHelper.GROUPID + ") from " + DatabaseHelper.RECORDTABLE;
        Cursor cursor = db.rawQuery(sql, null);
        int a = -1;
        if (cursor.moveToFirst()) {
            a = cursor.getInt(0);
        }
        return a;
    }

    // 3. 为了“乐园”读取数据。这个好像暂时不实现也行

    // 4. 存自己的作答。
    public void storageUserAnswer(int groupID, String user, List<UserAnswerResult> userAnswerResults) {
        db = dbhelper.getWritableDatabase();
        for (UserAnswerResult userAnswerResult : userAnswerResults) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.GROUPID, groupID);
            values.put(DatabaseHelper.QUESTIONID, userAnswerResult.getQuestionID());
            values.put(DatabaseHelper.USER, user);
            values.put(DatabaseHelper.YOURANSWER, userAnswerResult.getUserAnswer());
            db.insert(DatabaseHelper.USERANSWERTABLE, null, values);
        }
        db.close();
    }
}
