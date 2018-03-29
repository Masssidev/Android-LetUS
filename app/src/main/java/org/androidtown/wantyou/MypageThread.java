package org.androidtown.wantyou;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by LG on 2018-01-22.
 */

public class MypageThread extends Thread {
    private String domain = "http://172.30.5.56:8080/android/";
    private Context context;
    private String message, nickname, email;
    private int count;

    public MypageThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        String url = domain + "mypage";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            message = obj.getString("message");

            if(!message.equals("no")) {
                nickname = obj.getString("nickname");
                email = obj.getString("email");
                count = obj.getInt("count");
                Log.d("hhh", Integer.toString(count));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return message;
    }

    public String getNickname(){
        return nickname;
    }

    public String getEmail(){
        return email;
    }

    public int getCount() { return count; }
}
