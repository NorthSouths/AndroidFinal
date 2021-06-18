package com.example.myapplication2.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication2.data.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    /*
     * DAO层封装了dbhelper 进行操作
     *
     * */
    private final DatabaseHelper dbhelper;
    private final String DBName = "qoc";
    private SQLiteDatabase db;

    public QuestionDao(Context context) {
        dbhelper = DatabaseHelper.getInstance(context, DBName);
    }


    // 我们认为题库不会被删除行。别搞得太麻烦了，要死了
    // 对于Question表的操作，主要有
    // 1. 读取count。这用于随机访问的功能中，产生随机数
    public int getTotalQuestionCount() {
        db = dbhelper.getReadableDatabase();
        String sql = "Select Count(" + DatabaseHelper.QUESTIONID + ") From " + DatabaseHelper.QUESTIONTABLE;
        Cursor cursor = db.rawQuery(sql, null);
        int res = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                res = cursor.getInt(0);
                cursor.moveToNext();
            }
        }
        db.close();
        return res;
    }

    // 2. 创建数据库。这个应该是在dbhelper创建的时候来生成了的。DAO层应该不用写代码

    // 3. 按照给定的随机数序列（ArrayList）给出题目信息，包括题干，选项，答案
    public List<Question> getQuestionList(List<Integer> randomNumList) {
        List<Question> resultLists = new ArrayList<Question>();
        db = dbhelper.getReadableDatabase(); //这也应该是只读的。
        //如果没有查到，那么就不添加。目前如果题库数量小于要求数量，不认为是错误。当然，前端页面应当去判断个数。
        // 至少的有一道题吧。。

        //去两个数据库查询吧。
        // 别抱怨上来就getReadableDatabase 了

        //两种方法都试试嘛。多看看。
        String content, choiceA, choiceB, choiceC, choiceD, correct;
        int chapter;
        for (int i : randomNumList) {
            Cursor questionCursor = db.query(DatabaseHelper.QUESTIONTABLE,
                    new String[]{DatabaseHelper.CONTENT,
                            DatabaseHelper.CHAPTER,
                            DatabaseHelper.CHOICEA,
                            DatabaseHelper.CHOICEB,
                            DatabaseHelper.CHOICEC,
                            DatabaseHelper.CHOICED,
                    }, DatabaseHelper.QUESTIONID + "=?",
                    new String[]{String.valueOf(i)},
                    null,
                    null,
                    null,
                    null);
            Cursor answerCursor = db.query(DatabaseHelper.ANSWERTABLE, new String[]{DatabaseHelper.ANSWER}
                    , DatabaseHelper.QUESTIONID + "=?",
                    new String[]{String.valueOf(i)},
                    null,
                    null,
                    null,
                    null);
            if (answerCursor.getCount() != 1 || questionCursor.getCount() != 1) {
                // 不应该发生的内容。题目ID应不重复。
                return null;
            }
            questionCursor.moveToFirst();
            answerCursor.moveToFirst();
            content = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CONTENT));
            choiceA = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICEA));
            choiceB = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICEB));
            choiceC = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICEC));
            choiceD = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICED));
            chapter = questionCursor.getInt(questionCursor.getColumnIndex(DatabaseHelper.CHAPTER));
            correct = answerCursor.getString(answerCursor.getColumnIndex(DatabaseHelper.ANSWER));

            Question question = new Question(i, content, chapter, choiceA, choiceB, choiceC, choiceD, correct);
            resultLists.add(question);
        }
        return resultLists;

    }

    // 还有啥？ 调用dbhelper的更新(不是升级)数据库功能？
    // 4. queryAll。返回全部的题库。
    public List<Question> getAllQuestion() {
        List<Question> resultLists = new ArrayList<Question>();
        db = dbhelper.getReadableDatabase(); //这也应该是只读的。
        String content = null, choiceA = null, choiceB = null, choiceC = null, choiceD = null, correct = null;
        int id = 0, chapter = 0;
        Cursor questionCursor = db.query(DatabaseHelper.QUESTIONTABLE,
                new String[]{DatabaseHelper.QUESTIONID,
                        DatabaseHelper.CONTENT,
                        DatabaseHelper.CHAPTER,
                        DatabaseHelper.CHOICEA,
                        DatabaseHelper.CHOICEB,
                        DatabaseHelper.CHOICEC,
                        DatabaseHelper.CHOICED,
                }, null, null, null, null, null, null);
        Cursor answerCursor = db.query(DatabaseHelper.ANSWERTABLE, new String[]{DatabaseHelper.ANSWER}
                , null, null, null, null, null, null);
        if (answerCursor.getCount() != questionCursor.getCount()) {
            // 不应该发生的内容 题目和答案应当一致。
            return null;
        }

        questionCursor.moveToFirst();
        answerCursor.moveToFirst();
        for (int i = 0; i < questionCursor.getCount(); i++) {
            id = questionCursor.getInt(questionCursor.getColumnIndex(DatabaseHelper.QUESTIONID));
            content = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CONTENT));
            choiceA = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICEA));
            choiceB = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICEB));
            choiceC = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICEC));
            choiceD = questionCursor.getString(questionCursor.getColumnIndex(DatabaseHelper.CHOICED));
            chapter = questionCursor.getInt(questionCursor.getColumnIndex(DatabaseHelper.CHAPTER));
            correct = answerCursor.getString(answerCursor.getColumnIndex(DatabaseHelper.ANSWER));
        }
        Question question = new Question(id, content, chapter, choiceA, choiceB, choiceC, choiceD, correct);
        resultLists.add(question);

        return resultLists;
    }
}
