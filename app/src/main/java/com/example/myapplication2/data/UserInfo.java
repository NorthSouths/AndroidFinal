package com.example.myapplication2.data;

import android.content.Context;

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
        this.context = context_;
    }

    public void initInfo(String name_) {
        this.name = name_;
    }

    public String getName() {
        return name;
    }

}
