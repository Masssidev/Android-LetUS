package org.androidtown.wantyou;

/**
 * Created by LG on 2017-08-11.
 */

public class SingerItem {

    String title, people, visit, url;
    int id, user_id;

    public SingerItem(int id, int user_id, String title, String people, String visit, String url) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.people = people;
        this.visit = visit;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
