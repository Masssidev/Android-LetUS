package org.androidtown.wantyou;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class SendMessageActivity extends AppCompatActivity implements Runnable {

    private EditText receiver, content;
    private String message, success;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        receiver = (EditText) findViewById(R.id.receiverText);
        content = (EditText) findViewById(R.id.content);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread(SendMessageActivity.this);
                th.start();
            }
        });

    }

    @Override
    public void run() {
        String url = "http://172.30.5.56:8080/android/sendMessage";
        String receive = receiver.getText().toString();
        String con = content.getText().toString();

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("receive", receive));
            postData.add(new BasicNameValuePair("con", con));

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Toast.makeText(SendMessageActivity.this, message, Toast.LENGTH_SHORT).show();
            if (success.equals("yes"))
                finish();
        }
    };
}
