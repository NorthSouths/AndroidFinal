package com.example.myapplication2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static DatabaseHelper instance;
    private boolean userTableCreated = false;
    private boolean numTableCreated = false;
    private boolean thinkTableCreated = false;
    private boolean literatureTableCreated = false;
    private boolean recordTableCreated = false;

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
    public void onCreate(SQLiteDatabase db) {
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
                    "question_id primary key autoincrement, " +
                    "question_content varchar(65535), " +
                    "question_chapter int, "+
                    "choiceA varchar(65535), " +
                    "choiceB varchar(65535), " +
                    "choiceC varchar(65535), " +
                    "choiceD varchar(65535) " +
                    ")";
            db.execSQL(sql);
            questionTableCreated = true;
        }
        if (!answerTableCreated) {
            String sql = "create table Answer()";

        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
