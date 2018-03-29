package org.androidtown.wantyou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText idText, pwText, nicknameText, emailText;
    private Button idCheckButton, nicknameCheckButton, registerButton;
    private Boolean checked1 = false, checked2 = false;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idText = (EditText) findViewById(R.id.idText);
        pwText = (EditText) findViewById(R.id.passwordText);
        nicknameText = (EditText) findViewById(R.id.nicknameText);
        emailText = (EditText) findViewById(R.id.emailText);

        idCheckButton = (Button) findViewById(R.id.idCheckButton);
        nicknameCheckButton = (Button) findViewById(R.id.nicknameCheckButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        idCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIdThread th = new CheckIdThread(idText, RegisterActivity.this);
                th.start();
                try {
                    th.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result=th.getResult();
                if(result.equals("yes")){
                    checked1=true;
                }
            }
        });

        nicknameCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckNicknameThread th2 = new CheckNicknameThread(nicknameText, RegisterActivity.this);
                th2.start();
                try {
                    th2.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result=th2.getResult();
                if(result.equals("yes")){
                    checked2=true;
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked1==true && checked2==true) {
                    RegisterThread th = new RegisterThread(idText, pwText, nicknameText, emailText, RegisterActivity.this);
                    th.start();
                } else {
                    if (checked1==false) {
                        Toast.makeText(RegisterActivity.this, "아이디 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "닉네임 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
