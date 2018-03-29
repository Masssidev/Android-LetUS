package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements Runnable {

    private EditText idText, pwText;
    private Button loginButton;
    private String message, success;
    private boolean login=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = (EditText) findViewById(R.id.idText);
        pwText = (EditText) findViewById(R.id.pwText);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread(LoginActivity.this);
                th.start();
            }
        });

        TextView registerButton = (TextView) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        TextView findButton = (TextView) findViewById(R.id.findButton);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findIntent = new Intent(LoginActivity.this, FindActivity.class);
                LoginActivity.this.startActivity(findIntent);
            }
        });

    }

    @Override
    public void run() {
        String url = "http://172.30.5.56:8080/android/login";
        String id = idText.getText().toString();
        String pw = pwText.getText().toString();

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("id", id));
            postData.add(new BasicNameValuePair("pw", pw));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            message = obj.getString("message");
            success = obj.getString("success");

            if(success.equals("yes")){
                login=true;
            }

            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(login==true) {
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LoginActivity.this.startActivity(loginIntent);
            }else
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

}
