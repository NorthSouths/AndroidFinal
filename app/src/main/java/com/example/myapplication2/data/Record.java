package com.example.myapplication2.data;

public class Record {
    private String user;
    private String time;
    private int score;

    public Record() {
    }

    public Record(String user, String time, int score) {
        this.user = user;
        this.time = time;
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}