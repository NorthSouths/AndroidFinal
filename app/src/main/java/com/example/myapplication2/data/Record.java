package com.example.myapplication2.data;
/*
    author : 181110514 徐佳辰
 */
public class Record {
    private String user;
    private String time;
    private int score;
    private int gruopID;

    public Record() {
    }

    public Record(String user, String time, int score, int gruopID) {
        this.user = user;
        this.time = time;
        this.score = score;
        this.gruopID = gruopID;
    }

    public Record(String user, String time, int score) {
        this.user = user;
        this.time = time;
        this.score = score;
    }

    public int getGruopID() {
        return gruopID;
    }

    public void setGruopID(int gruopID) {
        this.gruopID = gruopID;
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