package org.androidtown.wantyou;

/**
 * Created by LG on 2018-03-21.
 */

public class Member {
    private int id;
    private String nickname, email;

    public Member(int id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
