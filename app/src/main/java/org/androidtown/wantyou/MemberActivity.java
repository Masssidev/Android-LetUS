package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MemberActivity extends AppCompatActivity implements Runnable{
    private int id;
    private ListView memberListView;
    private MyMemberAdapter adapter;
    private ArrayList<Member> memberList;
    private JSONArray memberListArray;
    private String title, message;
    private String domain = "http://172.30.5.56:8080/android/";
    private TextView titleText;
    private MessageToMemberDialogfragment messageToMemberDialogfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        titleText = (TextView)findViewById(R.id.titleText);

        Intent selectIntent = getIntent();
        id = selectIntent.getExtras().getInt("id");
        Log.d("태그", Integer.toString(id));

        memberListView = (ListView)findViewById(R.id.memberList);

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member data = (Member) parent.getItemAtPosition(position);
                String nick = data.getNickname();

                if (messageToMemberDialogfragment == null) {
                    messageToMemberDialogfragment = new MessageToMemberDialogfragment();
                }
                Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                bundle.putString("nick", nick); // key , value
                messageToMemberDialogfragment.setArguments(bundle);
                messageToMemberDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
            }
        });

        memberList = new ArrayList<Member>();

        Thread th = new Thread(MemberActivity.this);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < memberListArray.length(); i++){
            JSONObject order = null;
            try {
                order = memberListArray.getJSONObject(i);
                memberList.add(new Member(order.getInt("id"), order.getString("nickname"), order.getString("email")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new MyMemberAdapter(this, memberList);
        memberListView.setAdapter(adapter);
    }

    @Override
    public void run() {
        String url = domain+"myMember";

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("id", Integer.toString(id)));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            String result = obj.getString("result");
            title = obj.getString("title");

            if(result.equals("[]")) {
                message = "해당 스터디에 함께하는 멤버가 아직 없습니다";
                handler.sendEmptyMessage(0);
                finish();
            }

            memberListArray = new JSONArray(result);
            handler.sendEmptyMessage(1);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    titleText.setText(title);
                    Toast.makeText(MemberActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    titleText.setText(title);
                    break;
            }
        }
    };

}
