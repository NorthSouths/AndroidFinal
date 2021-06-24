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


}
