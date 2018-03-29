package org.androidtown.wantyou;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

public class ApplyStudyThread extends Thread {
    private int id;
    private Context context;
    private String message;
    private String domain = "http://172.30.5.56:8080/android/";

    public ApplyStudyThread(Context context, int id) {
        this.id = id;
        this.context = context;
    }

    @Override
    public void run() {

        String url = domain + "applyStudy";

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("articleId", Integer.toString(id)));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            message = obj.getString("success");

            if (message.equals("no"))
                handler.sendEmptyMessage(0);
            else if (message.equals("full"))
                handler.sendEmptyMessage(1);
            else if (message.equals("expire"))
                handler.sendEmptyMessage(2);
            else if (message.equals("same"))
                handler.sendEmptyMessage(3);
            else
                handler.sendEmptyMessage(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return message;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(context, "로그인해주세요", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    break;
                case 1:
                    Toast.makeText(context, "해당 스터디 모집 인원이 마감되었습니다", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context, "해당 스터디 모집 기간이 마감되었습니다", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(context, "이미 활동중인 스터디입니다", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(context, "해당 스터디 신청이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
