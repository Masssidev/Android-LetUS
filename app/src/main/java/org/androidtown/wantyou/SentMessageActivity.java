package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class SentMessageActivity extends AppCompatActivity implements Runnable{

    private TextView receiver, contentText, dateText;
    private String messageReceiver, messageContent, messageTime;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_message);

        receiver=(TextView)findViewById(R.id.receiverText);
        contentText=(TextView)findViewById(R.id.sentMessageText);
        dateText=(TextView)findViewById(R.id.dateText);

        Thread th = new Thread(SentMessageActivity.this);
        th.start();

        Intent selectIntent = getIntent();
        id = selectIntent.getExtras().getInt("id");

    }
    @Override
    public void run() {
        String url = "http://172.30.5.56:8080/android/findOneSentMessage";

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("id", Integer.toString(id)));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            messageReceiver = obj.getString("messageReceiver");
            messageContent = obj.getString("messageContent");
            messageTime = obj.getString("messageTime");

            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            receiver.setText(messageReceiver);
            contentText.setText(messageContent);
            dateText.setText(messageTime);
        }
    };
}
