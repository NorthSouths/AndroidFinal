package com.example.myapplication2.dao;
/*
    author : 2191110328 乔楠
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication2.data.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    /*
     * DAO层封装了dbhelper 进行操作
     *
     * */
    private final DatabaseHelper dbhelper;
    private final String DBName = DatabaseHelper.DBNAME;
    private SQLiteDatabase db;

    public QuestionDao(Context context) {
        dbhelper = DatabaseHelper.getInstance(context, DBName);
    }


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
        cursor.close();
        db.close();
        return res;
    }

    // 2. 按照给定的题号ID给出题目信息，包括题干，选项，答案
    public Question getQuestion(int QID) {
        db = dbhelper.getReadableDatabase(); //这也应该是只读的。
        String content, choiceA, choiceB, choiceC, choiceD, correct;
        int chapter;
        Cursor questionCursor = db.query(DatabaseHelper.QUESTIONTABLE,
                new String[]{DatabaseHelper.CONTENT,
                        DatabaseHelper.CHAPTER,
                        DatabaseHelper.CHOICEA,
                        DatabaseHelper.CHOICEB,
                        DatabaseHelper.CHOICEC,
                        DatabaseHelper.CHOICED,
                }, DatabaseHelper.QUESTIONID + "=?",
                new String[]{String.valueOf(QID)},
                null,
                null,
                null,
                null);
        Cursor answerCursor = db.query(DatabaseHelper.ANSWERTABLE, new String[]{DatabaseHelper.ANSWER}
                , DatabaseHelper.QUESTIONID + "=?",
                new String[]{String.valueOf(QID)},
                null,
                null,
                null,
                null);
        if (answerCursor.getCount() != 1 || questionCursor.getCount() != 1) {
            // 不应该发生的内容。题目ID应不重复。
            Log.w("xu", "不应该发生的情况。不存在此题目！");
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
        questionCursor.close();
        answerCursor.close();
        return new Question(QID, content, chapter, choiceA, choiceB, choiceC, choiceD, correct);
    }

    // 3. 按照给定的随机数序列（ArrayList）给出题目信息，包括题干，选项，答案
    public List<Question> getQuestionList(List<Integer> randomNumList) {
        List<Question> resultLists = new ArrayList<Question>();
        db = dbhelper.getReadableDatabase(); //这也应该是只读的。
        //如果没有查到，那么就不添加。目前如果题库数量小于要求数量，不认为是错误。当然，前端页面应当去判断个数。
        // 至少的有一道题吧。。

        //去两个数据库查询吧。
        // 别抱怨上来就getReadableDatabase 了
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
                Log.w("xu", "不应该发生的情况。题目ID重复！");
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
            questionCursor.close();
            answerCursor.close();
            // 注意，这里游标不用自动下移，但是范围查询可别忘了
        }
        return resultLists;

    }

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
            Question question = new Question(id, content, chapter, choiceA, choiceB, choiceC, choiceD, correct);
            resultLists.add(question);
            questionCursor.moveToNext();
            answerCursor.moveToNext();
        }

        questionCursor.close();
        answerCursor.close();
        db.close();
        return resultLists;
    }

    //5. 范围查询 本操作是为了按章节进行查找而准备的。
    public List<Question> getQuestionListByChapter(int targetChapter) {
        List<Question> resultLists = new ArrayList<Question>();
        db = dbhelper.getReadableDatabase(); //这也应该是只读的。
        String content, choiceA, choiceB, choiceC, choiceD, correct;
        int chapter, id;
        // 这次我们使用 rawQuery
        // select Question.question_id, question_content, question_chapter, choiceA,choiceB,choiceC,choiceD,question_answer
        // from Question inner join Answer on Question.question_id = Answer.question_id
        // where Question.question_chapter = 2
        String SQL = "Select " +
                DatabaseHelper.QUESTIONTABLE + "." + DatabaseHelper.QUESTIONID + ", " + DatabaseHelper.CONTENT + ", " + DatabaseHelper.CHAPTER + ", " +
                DatabaseHelper.CHOICEA + ", " + DatabaseHelper.CHOICEB + ", " + DatabaseHelper.CHOICEC + ", " + DatabaseHelper.CHOICED + ", " + DatabaseHelper.ANSWER + " " +
                " from " + DatabaseHelper.QUESTIONTABLE + " inner join " + DatabaseHelper.ANSWERTABLE +
                " on " + DatabaseHelper.QUESTIONTABLE + "." + DatabaseHelper.QUESTIONID + " = " + DatabaseHelper.ANSWERTABLE + "." + DatabaseHelper.QUESTIONID + " " +
                "where " + DatabaseHelper.QUESTIONTABLE + "." + DatabaseHelper.CHAPTER + " = " + String.valueOf(targetChapter);
        Cursor cursor = db.rawQuery(SQL, null);
        if (cursor.getCount() == 0) {
            Log.w("xu", "按章节做题读取到了不存在的章节。数据集还不够全");
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.QUESTIONID));
            content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTENT));
            choiceA = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICEA));
            choiceB = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICEB));
            choiceC = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICEC));
            choiceD = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICED));
            chapter = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CHAPTER));
            correct = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANSWER));
            Question question = new Question(id, content, chapter, choiceA, choiceB, choiceC, choiceD, correct);
            resultLists.add(question);
            cursor.moveToNext();
        }
        return resultLists;
    }

    //6. 手动导入题库
    public void insertQuestion(Question question) {
        ContentValues questionValues = new ContentValues();
        ContentValues answerValues = new ContentValues();
        //id自动生成（+1）
        db = dbhelper.getWritableDatabase();
        questionValues.put(DatabaseHelper.CONTENT, question.getQuestionContent());
        questionValues.put(DatabaseHelper.CHAPTER, question.getQuestionChapter());
        questionValues.put(DatabaseHelper.CHOICEA, question.getChoiceA());
        questionValues.put(DatabaseHelper.CHOICEB, question.getChoiceB());
        questionValues.put(DatabaseHelper.CHOICEC, question.getChoiceC());
        questionValues.put(DatabaseHelper.CHOICED, question.getChoiceD());
        answerValues.put(DatabaseHelper.ANSWER, question.getCorrectChoice());
        db.insert(DatabaseHelper.QUESTIONTABLE, null, questionValues);
        db.insert(DatabaseHelper.ANSWERTABLE, null, answerValues);
        db.close();
    }

}
