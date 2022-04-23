package com.example.myapplication2.dao;
/*
    author : 2191110328 乔楠
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static DatabaseHelper instance;

    // 五张记录表
    private boolean userTableCreated = false;
    private boolean questionTableCreated = false; //题目表 记录题目 Question(QUESTION_ID INT, QUESTION_CONTENT, CHOICEA,CHOICEB,CHOICEC,CHOICED)
    private boolean answerTableCreated = false; // 答案表 记录答案。 Answer(QUESTION_ID INT, ANSWER VARCHAR)
    private boolean recordTableCreated = false; // record表，我们借用一下它的逻辑，但是修改为
    // Record(group_id, user,time,score)
    private boolean userAnswerTableCreated = false;

    static final String QUESTIONTABLE = "Question";
    static final String ANSWERTABLE = "Answer";
    static final String USERTABLE = "User";
    static final String QUESTIONID = "question_id";
    static final String CONTENT = "question_content";
    static final String CHAPTER = "question_chapter";
    static final String CHOICEA = "choiceA";
    static final String CHOICEB = "choiceB";
    static final String CHOICEC = "choiceC";
    static final String CHOICED = "choiceD";
    static final String ANSWER = "question_answer";
    static final String RECORDTABLE = "Record";
    static final String USERANSWERTABLE = "UserAnswer";
    static final String GROUPID = "group_id";
    static final String USER = "user";
    static final String TIME = "time";
    static final String SCORE = "score";
    static final String YOURANSWER = "your_answer";

    static final String DBNAME = "AD";
    static final String NAME = "name";
    static final String PHONENUM = "phonenum";
    static final String EMAIL = "email";
    static final String PASSWORD = "password";
    private Context context;

    public static synchronized DatabaseHelper getInstance(Context context, String name) {
        if (instance == null) {
            instance = new DatabaseHelper(context, name);
        }
        return instance;
    }

    private DatabaseHelper(Context context, String name) {
        this(context, name, VERSION);
        this.context = context;
    }

    private DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        checkTableExist(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        checkTableExist(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("测试", "就不更新");
    }

    /*
        检查
        USERTABLE 用户信息表
        RECORDTABLE 答题成绩表
        QUESTIONTABLE 问题表
        ANSWERTABLE 正确答案表
        USERANSWERTABLE 用户答题记录表
    */
    private void checkTableExist(SQLiteDatabase db) {
        Log.i("QOC", "create table");
        // 查询数据库"AD"中的所有存在表名
        Cursor cursor = db.rawQuery("select name from sqlite_master where type ='table'", null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            if (name.equals(USERTABLE)) {
                userTableCreated = true;
            } else if (name.equals(RECORDTABLE)) {
                recordTableCreated = true;
            } else if (name.equals(QUESTIONTABLE)) {
                questionTableCreated = true;
            } else if (name.equals(ANSWERTABLE)) {
                answerTableCreated = true;
            } else if (name.equals(USERANSWERTABLE)) {
                userAnswerTableCreated = true;
            }
        }

        // USERTABLE 用户信息表
        if (!userTableCreated) {
            String createUserTable = "create table User(name varchar(65535), phonenum varchar(20), email varchar(65535), password varchar(65535), primary key(name))";
            db.execSQL(createUserTable);
            userTableCreated = true;
        }

        // USERANSWERTABLE 用户答题记录表
        if (!userAnswerTableCreated) {
            String sql = "create table " +
                    USERANSWERTABLE +
                    "(" +
                    GROUPID + " Integer , " +
                    QUESTIONID + " Integer ," +
                    USER + " varchar(65535), " +
                    YOURANSWER + " varchar(65535) " +
                    ")";
            db.execSQL(sql);
            userAnswerTableCreated = true;
        }

        // 答题成绩表。
        if (!recordTableCreated) {
            String sql = "create table " +
                    RECORDTABLE +
                    "(" +
                    GROUPID + " INTEGER primary key autoincrement, " +
                    USER + " varchar(65535), " +
                    TIME + " varchar(65535), " +
                    SCORE + " int" +
                    ")";
            db.execSQL(sql);
            recordTableCreated = true;
        }

        //题目表 记录题目 Question(QUESTION_ID INT, QUESTION_CONTENT, CHOICEA,CHOICEB,CHOICEC,CHOICED)
        if (!questionTableCreated) {
            String sql = "create table " +
                    QUESTIONTABLE +
                    "(" +
                    QUESTIONID + " INTEGER primary key autoincrement, " + //主键递增
                    CONTENT + " varchar(65535), " + //题干
                    CHAPTER + " int, " + //章节
                    CHOICEA + " varchar(65535), " +//选项
                    CHOICEB + " varchar(65535), " +//选项
                    CHOICEC + " varchar(65535), " +//选项
                    CHOICED + " varchar(65535) " +//选项
                    ")";
            db.execSQL(sql);
            flag = true;
            questionTableCreated = true;
        }


        // 正确答案表
        if (!answerTableCreated) {
            String sql = "create table " +
                    ANSWERTABLE +
                    "(" +
                    QUESTIONID + " INTEGER primary key autoincrement, " + //主键递增
                    ANSWER + " varchar(65535) " + //题干
                    ")";
            db.execSQL(sql);
            flag = true;
            answerTableCreated = true;
        }

        // 读取数据。这个应该在表不存在的时候再调用吧。
        if (flag) {
            initQuestionTables(db);
        }
    }


    private void initQuestionTables(SQLiteDatabase db) {
        String dataFileName = "question.txt";
        InputStreamReader inputReader;
        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(dataFileName));
            BufferedReader bufferdReader = new BufferedReader(inputReader);
            String line;
            int id = 1;
            bufferdReader.readLine(); //跳过第一行的说明性文字
            while ((line = bufferdReader.readLine()) != null) {
                String[] strs = line.split(";");

                // 理论上一条数据集中有7个内容。因此判断范围，提示数据集报错信息
                if (strs.length != 7) {
                    Log.w("数据集出现问题", strs[0]);
                    continue;
                }
                /*strs[0] 题目内容
                 * strs[1] 题目所属章节
                 * strs[2] 选项A
                 * strs[3] 选项B
                 * strs[4] 选项C
                 * strs[5] 选项D
                 * strs[6] 正确答案
                 * */

                // 我们要向2个表—— Question和Answer 表中插入内容。建立两个ContentValues
                ContentValues questionValues = new ContentValues();
                ContentValues answerValues = new ContentValues();

                questionValues.put(QUESTIONID, id);
                questionValues.put(CONTENT, strs[0].trim());
                questionValues.put(CHAPTER, Integer.valueOf(strs[1].trim()));
                questionValues.put(CHOICEA, strs[2].trim());
                questionValues.put(CHOICEB, strs[3].trim());
                questionValues.put(CHOICEC, strs[4].trim());
                questionValues.put(CHOICED, strs[5].trim());

                answerValues.put(QUESTIONID, id);
                answerValues.put(ANSWER, strs[6].trim());
                id++;
                db.insert(QUESTIONTABLE, null, questionValues);
                db.insert(ANSWERTABLE, null, answerValues);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
