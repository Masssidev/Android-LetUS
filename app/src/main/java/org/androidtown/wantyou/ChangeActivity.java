package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ChangeActivity extends AppCompatActivity implements Runnable {
    private EditText nicknameText, passwordText, emailText;
    private Button changeButton, nicknameCheckButton;
    private String bringNickname, bringEmail, result;
    private Boolean checked1 = false;
    private String domain = "http://172.30.5.56:8080/android/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        nicknameText = (EditText) findViewById(R.id.nicknameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        emailText = (EditText) findViewById(R.id.emailText);

        nicknameCheckButton = (Button) findViewById(R.id.nicknameCheckButton);
        changeButton = (Button) findViewById(R.id.changeButton);

        Thread th = new Thread(ChangeActivity.this);
        th.start();

        nicknameCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckNicknameThread th = new CheckNicknameThread(nicknameText, ChangeActivity.this);
                th.start();
                try {
                    th.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = th.getResult();
                if (result.equals("yes")) {
                    checked1 = true;
                }
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked1 == true || bringNickname.equals(nicknameText.getText().toString())) {
                    ChangeThread th = new ChangeThread(nicknameText, passwordText, emailText, ChangeActivity.this);
                    th.start();
                    Intent loginIntent = new Intent(ChangeActivity.this, LoginActivity.class);
                    ChangeActivity.this.startActivity(loginIntent);
                } else {
                    Toast.makeText(ChangeActivity.this, "닉네임 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void run() {
        String url = domain+"bring";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            bringNickname = obj.getString("nickname");
            bringEmail = obj.getString("email");

            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            nicknameText.setText(bringNickname);
            passwordText.setText("");
            emailText.setText(bringEmail);

        }
    };
}

