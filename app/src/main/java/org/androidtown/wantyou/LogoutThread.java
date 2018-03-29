package org.androidtown.wantyou;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by LG on 2018-03-18.
 */

public class LogoutThread extends Thread {
    private String domain = "http://172.30.5.56:8080/android/";
    private Context context;
    private String message;

    public LogoutThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        String url = domain+"logout";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            message = obj.getString("message");

            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
}
