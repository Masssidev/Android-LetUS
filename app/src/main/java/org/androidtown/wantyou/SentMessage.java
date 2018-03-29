package org.androidtown.wantyou;

/**
 * Created by LG on 2017-09-21.
 */

public class SentMessage {
    int id;
    String message;
    String name;
    String date;

    public SentMessage(int id, String message, String name, String date) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
