package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MessageBoxActivity extends AppCompatActivity implements Runnable{
    private TextView sendMessage, sendBox, receiveBox;
    private String domain = "http://172.30.5.56:8080/android/";
    private TextView nicknameText, countText;
    private String nickname;
    private int count;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_box);

        Thread th = new Thread(MessageBoxActivity.this);
        th.start();

        nicknameText = (TextView) findViewById(R.id.nicknameText);
        countText = (TextView) findViewById(R.id.countText);
        linearLayout = (LinearLayout) findViewById(R.id.display);

        sendMessage = (TextView)findViewById(R.id.sendMessageText);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendMessageIntent = new Intent(MessageBoxActivity.this, SendMessageActivity.class);
                MessageBoxActivity.this.startActivity(sendMessageIntent);
            }
        });

        sendBox = (TextView) findViewById(R.id.sendBoxText);
        sendBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendBoxIntent = new Intent(MessageBoxActivity.this, SentBoxActivity.class);
                MessageBoxActivity.this.startActivity(sendBoxIntent);
            }
        });

        receiveBox = (TextView) findViewById(R.id.receiveBoxText);
        receiveBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receiveBoxIntent = new Intent(MessageBoxActivity.this, ReceivedBoxActivity.class);
                MessageBoxActivity.this.startActivity(receiveBoxIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_message_box);

        Thread th = new Thread(MessageBoxActivity.this);
        th.start();

        nicknameText = (TextView) findViewById(R.id.nicknameText);
        countText = (TextView) findViewById(R.id.countText);
        linearLayout = (LinearLayout) findViewById(R.id.display);

        sendMessage = (TextView)findViewById(R.id.sendMessageText);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendMessageIntent = new Intent(MessageBoxActivity.this, SendMessageActivity.class);
                MessageBoxActivity.this.startActivity(sendMessageIntent);
            }
        });

        sendBox = (TextView) findViewById(R.id.sendBoxText);
        sendBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendBoxIntent = new Intent(MessageBoxActivity.this, SentBoxActivity.class);
                MessageBoxActivity.this.startActivity(sendBoxIntent);
            }
        });

        receiveBox = (TextView) findViewById(R.id.receiveBoxText);
        receiveBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receiveBoxIntent = new Intent(MessageBoxActivity.this, ReceivedBoxActivity.class);
                MessageBoxActivity.this.startActivity(receiveBoxIntent);
            }
        });
    }

    @Override
    public void run() {
        String url = domain+"getNickname";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            nickname = obj.getString("nickname");
            count = obj.getInt("count");

            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            nicknameText.setText(nickname);
            countText.setText(Integer.toString(count));
            if(count == 0)
                linearLayout.setVisibility(View.GONE);
        }
    };
}
