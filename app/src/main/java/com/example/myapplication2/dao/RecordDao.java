package com.example.myapplication2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication2.data.Record;
import com.example.myapplication2.data.UserAnswerResult;

import java.util.ArrayList;
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
        db.close();
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

    // 5. 按照用户名，返回信息。
    public List<Record> getRecordsByName(String userName) {
        db = dbhelper.getReadableDatabase();
        String sql = "select " + DatabaseHelper.GROUPID + ", " + DatabaseHelper.TIME + ", " + DatabaseHelper.SCORE +
                " from " + DatabaseHelper.RECORDTABLE + " where " + DatabaseHelper.USER + " = " + userName;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 1) {
            Log.w("xu", "不存在此用户的答题记录");
            return null;
        }
        ArrayList<Record> results = new ArrayList<Record>();
        int groupID, score;
        String time;
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
            groupID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROUPID));
            score = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SCORE));
            cursor.moveToNext();
            Record record = new Record(userName, time, score, groupID);
            results.add(record);
        }
        db.close();
        return results;
    }

    //6. 按照给定的groupID查找UserAnswer表，获得List<userAnswerResult>
    public List<UserAnswerResult> getUserAnswerResultsByGroupID(int groupID) {
        ArrayList<UserAnswerResult> userAnswerResults = new ArrayList<>();
        db = dbhelper.getReadableDatabase(); //这也应该是只读的。
        int questionID;
        String your_answer;
        Cursor cursor = db.query(DatabaseHelper.USERANSWERTABLE,
                new String[]{DatabaseHelper.QUESTIONID, DatabaseHelper.YOURANSWER},
                DatabaseHelper.GROUPID + "=?",
                new String[]{String.valueOf(groupID)},
                null, null, null, null);

        if (cursor.getCount() == 0) {
            Log.w("xu", "用户历史作答没有此组号的内容");
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            your_answer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.YOURANSWER));
            questionID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.QUESTIONID));
            UserAnswerResult userAnswerResult = new UserAnswerResult(questionID, your_answer);
            userAnswerResults.add(userAnswerResult);
            cursor.moveToNext();
        }
        return userAnswerResults;
    }
}
