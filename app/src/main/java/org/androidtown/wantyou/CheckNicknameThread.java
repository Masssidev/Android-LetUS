package org.androidtown.wantyou;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
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
 * Created by LG on 2017-08-01.
 */

public class CheckNicknameThread extends Thread {
    private EditText nicknameText;
    private Context context;
    private String message, success;

    public CheckNicknameThread(EditText nicknameText, Context context) {
        this.nicknameText = nicknameText;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            String url = "http://172.30.5.56:8080/android/checkNickname";
            String nickname = nicknameText.getText().toString();

            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("nickname", nickname));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            message = obj.getString("message");
            success = obj.getString("success");

            handler.sendEmptyMessage(0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return success;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };

}
