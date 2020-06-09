package com.example.codingcafe.chatter;

public class GroupMessages {
    private String name, message,  time, date;
    public GroupMessages(){

    }

    public GroupMessages(String name, String message, String time, String date) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
