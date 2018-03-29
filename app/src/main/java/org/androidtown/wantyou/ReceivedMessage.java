package org.androidtown.wantyou;

/**
 * Created by LG on 2017-09-21.
 */

public class ReceivedMessage {
    int id;
    String message;
    String name;
    String date;
    int read;

    public ReceivedMessage(int id, String message, String name, String date, int read) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.date = date;
        this.read = read;
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

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
