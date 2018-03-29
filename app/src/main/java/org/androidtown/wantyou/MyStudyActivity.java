package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyStudyActivity extends AppCompatActivity implements Runnable{
    private ListView studyListView;
    private LikeListAdapter adapter;
    private ArrayList<Like> studyList;
    private JSONArray studyListArray;
    private String message;
    private String domain = "http://172.30.5.56:8080/android/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_study);

        studyListView = (ListView)findViewById(R.id.studyList);

        studyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Like data = (Like) parent.getItemAtPosition(position);
                Intent myMemberIntent = new Intent(MyStudyActivity.this, MemberActivity.class);
                myMemberIntent.putExtra("id", data.getId());
                MyStudyActivity.this.startActivity(myMemberIntent);
            }
        });

        studyList = new ArrayList<Like>();

        Thread th = new Thread(MyStudyActivity.this);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < studyListArray.length(); i++){
            JSONObject order = null;
            try {
                order = studyListArray.getJSONObject(i);
                studyList.add(new Like(order.getInt("id"), order.getString("title"), order.getString("content"), domain+"getimage?id="+order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new LikeListAdapter(this, studyList);
        studyListView.setAdapter(adapter);
    }

    @Override
    public void run() {
        String url = domain+"myStudy";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            String result = obj.getString("result");

            studyListArray = new JSONArray(result);

            if(result.equals("[]")) {
                message = "현재 진행중인 스터디가 없습니다";
                handler.sendEmptyMessage(0);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MyStudyActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
}
