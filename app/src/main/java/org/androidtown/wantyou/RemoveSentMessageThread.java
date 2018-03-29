package org.androidtown.wantyou;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LG on 2018-03-17.
 */

public class RemoveSentMessageThread extends Thread{
    private Context context;
    private int id;
    private String message;
    private String domain = "http://172.30.5.56:8080/android/";

    public RemoveSentMessageThread(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public void run() {

        String url = domain+"removeSentMessage";

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("id", Integer.toString(id)));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            message = obj.getString("message");

            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return message;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
}
