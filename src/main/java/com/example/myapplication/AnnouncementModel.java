package com.example.myapplication;
public class AnnouncementModel {
    String title;
    String msg;
    String date;

    public AnnouncementModel() {}

    public AnnouncementModel(String title, String msg, String date) {
        this.title = title;
        this.msg = msg;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() {
        return date;
    }
}
