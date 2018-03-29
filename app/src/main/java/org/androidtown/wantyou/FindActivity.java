package org.androidtown.wantyou;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindActivity extends AppCompatActivity {
    private EditText nicknameText, emailText;
    private Button findButton;
    private String success, result;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        nicknameText = (EditText) findViewById(R.id.nicknameText);
        emailText = (EditText) findViewById(R.id.emailText);
        findButton = (Button) findViewById(R.id.findButton);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FindThread th = new FindThread(nicknameText, emailText, FindActivity.this);
                th.start();

                try {
                    th.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                success=th.getSuccess();
                if(success.equals("yes")){

                    result=th.getUser_id();

                    SendEmailThread th2 = new SendEmailThread(result, emailText, FindActivity.this);
                    th2.start();
                    try {
                        th2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    getApplicationContext().startActivity(loginIntent);
                    Toast.makeText(getApplicationContext(), "해당 이메일로 아이디와 임시 비밀번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void runDialog(final int seconds)

    {
        progressDialog = ProgressDialog.show(FindActivity.this, "Please wait....", "메일을 발송중입니다.");

        new Thread(new Runnable(){
            public void run(){
                try {
                    Thread.sleep(seconds * 1000);
                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
