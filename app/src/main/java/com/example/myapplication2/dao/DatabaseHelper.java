package com.example.myapplication2.dao;

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
    private boolean userTableCreated = false;
    private boolean numTableCreated = false;
    private boolean thinkTableCreated = false;
    private boolean literatureTableCreated = false;
    private boolean recordTableCreated = false;


     static final String QUESTIONTABLE = "Question";
     static final String ANSWERTABLE = "Answer";
     static final String QUESTIONID = "question_id";
     static final String CONTENT = "question_content";
     static final String CHAPTER = "question_chapter";
     static final String CHOICEA = "choiceA";
     static final String CHOICEB = "choiceB";
     static final String CHOICEC = "choiceC";
     static final String CHOICED = "choiceD";
     static final String ANSWER = "question_answer";
    // 我们简化，只有单选题目。
    private boolean questionTableCreated = false; //题目表 记录题目 Question(QUESTION_ID INT, QUESTION_CONTENT, CHOICEA,CHOICEB,CHOICEC,CHOICED)
    private boolean answerTableCreated = false; // 答案表 记录答案。 Answer(QUESTION_ID INT, ANSWER VARCHAR)


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
        Log.w("测试","更新你个头啊");
    }

    private void checkTableExist(SQLiteDatabase db) {
        Log.i("QOC", "create table");
        Cursor cursor = db.rawQuery("select name from sqlite_master where type ='table'", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            if (name.equals("User")) {
                userTableCreated = true;
            } else if (name.equals("Numeracy")) {
                numTableCreated = true;
            } else if (name.equals("Thinking")) {
                thinkTableCreated = true;
            } else if (name.equals("Literature")) {
                literatureTableCreated = true;
            } else if (name.equals("Record")) {
                recordTableCreated = true;
            } else if (name.equals("Question")) {
                questionTableCreated = true;
            } else if (name.equals("Answer")) {
                answerTableCreated = true;
            }
        }

        if (!userTableCreated) {
            String createUserTable = "create table User(name varchar(10),email varchar(10), password varchar(10),primary key(name))";
            db.execSQL(createUserTable);
            userTableCreated = true;
        }

        if (!numTableCreated) {
            initTable(db, "Numeracy");
            numTableCreated = true;
        }

        if (!thinkTableCreated) {
            initTable(db, "Thinking");
            thinkTableCreated = true;
        }

        if (!literatureTableCreated) {
            initTable(db, "Literature");
            literatureTableCreated = true;
        }

        if (!recordTableCreated) {
            String sql = "create table Record(user varchar(10),type varchar(10),time varchar(20),score int)";
            db.execSQL(sql);
            recordTableCreated = true;
        }
        //题目表 记录题目 Question(QUESTION_ID INT, QUESTION_CONTENT, CHOICEA,CHOICEB,CHOICEC,CHOICED)
        if (!questionTableCreated) {
            String sql = "create table Question(" +
                    QUESTIONID + " INTEGER primary key autoincrement, " + //主键递增
                    CONTENT + " varchar(65535), " + //题干
                    CHAPTER + " int, " + //章节
                    CHOICEA + " varchar(65535), " +//选项
                    CHOICEB + " varchar(65535), " +//选项
                    CHOICEC + " varchar(65535), " +//选项
                    CHOICED + " varchar(65535) " +//选项
                    ")";
            db.execSQL(sql);
            questionTableCreated = true;
        }
        if (!answerTableCreated) {
            String sql = "create table Answer(" +
                    QUESTIONID + " INTEGER primary key autoincrement, " + //主键递增
                    ANSWER + " varchar(65535) " + //题干
                    ")";
            db.execSQL(sql);
            answerTableCreated = true;
        }
        initQuestionTables(db);
    }


    private void initQuestionTables(SQLiteDatabase db) {
        String dataFileName = "question.txt";
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(dataFileName));
            BufferedReader bufferdReader = new BufferedReader(inputReader);
            String line = "";
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

    private void updateQuestionTables(SQLiteDatabase db) {
        String sql = "drop table " + QUESTIONTABLE;
        db.execSQL(sql);
        sql = "drop table " + ANSWERTABLE;
        db.execSQL(sql);
        initQuestionTables(db);
    }

    private void initTable(SQLiteDatabase db, String tableName) {
        String sql = "";
        if (tableName.equals("Thinking") || tableName.equals("Literature")) {
            sql = "create table " + tableName + "(id int,question text, answerA varchar(100)," +
                    "answerB varchar(100),answerC varchar(100),answerD varchar(100),answerE varchar(100)," +
                    "rightAnswer varchar(100),primary key(id))";
        } else if (tableName.equals("Numeracy")) {
            sql = "create table Numeracy(id int,question text, rightAnswer varchar(100),primary key(id))";
        }
        db.execSQL(sql);
        String suffix = ".txt";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(tableName + suffix));
            BufferedReader bufferdReader = new BufferedReader(inputReader);
            String line = "";
            int id = 1;
            long row = 0;
            while ((line = bufferdReader.readLine()) != null) {
                String[] qa = line.split(";");
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("question", qa[0]);
                if (tableName.equals("Thinking") || tableName.equals("Literature")) {
                    values.put("answerA", qa[1]);
                    values.put("answerB", qa[2]);
                    values.put("answerC", qa[3]);
                    values.put("answerD", qa[4]);
                    values.put("answerE", qa[5]);
                    values.put("rightAnswer", qa[6]);
                } else {
                    values.put("rightAnswer", qa[1]);
                }
                id++;
                row = db.insert(tableName, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
