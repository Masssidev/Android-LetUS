package org.androidtown.wantyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

public class ResultStudyActivity extends AppCompatActivity implements Runnable{
    private int id;
    private JSONArray studyList;
    private String domain = "http://172.30.5.56:8080/android/";
    private GridView gridView;
    private StudySingerAdapter adapter;
    private ArrayList<SingerItem> singerItemsList;
    private String result, search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_study);

        Intent resultIntent = getIntent();
        result = resultIntent.getExtras().getString("result");
        id = resultIntent.getExtras().getInt("id");
        Log.d("login", Integer.toString(id));
        search = resultIntent.getExtras().getString("search");

        try {
            studyList = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gridView = (GridView)findViewById(R.id.gridView);
        singerItemsList = new ArrayList<SingerItem>();

        for (int i = 0; i < studyList.length(); i++){
            JSONObject order = null;
            try {
                order = studyList.getJSONObject(i);
                singerItemsList.add(new SingerItem(Integer.parseInt(order.getString("id")), order.getInt("user_id"), order.getString("title"), order.getString("member"), order.getString("hits"), domain+"getimage?id="+order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new StudySingerAdapter(id, getApplicationContext(), singerItemsList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(ResultStudyActivity.this, SelectedStudyActivity.class);
                selectIntent.putExtra("id", data.getId());
                ResultStudyActivity.this.startActivity(selectIntent);
            }
        });

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        gridView.measure(0, expandSpec);
        gridView.getLayoutParams().height = gridView.getMeasuredHeight();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_result_study);

        Thread thread = new Thread(ResultStudyActivity.this);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            studyList = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gridView = (GridView)findViewById(R.id.gridView);
        singerItemsList = new ArrayList<SingerItem>();

        for (int i = 0; i < studyList.length(); i++){
            JSONObject order = null;
            try {
                order = studyList.getJSONObject(i);
                singerItemsList.add(new SingerItem(Integer.parseInt(order.getString("id")), order.getInt("user_id"), order.getString("title"), order.getString("member"), order.getString("hits"), domain+"getimage?id="+order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new StudySingerAdapter(id, getApplicationContext(), singerItemsList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(ResultStudyActivity.this, SelectedStudyActivity.class);
                selectIntent.putExtra("id", data.getId());
                ResultStudyActivity.this.startActivity(selectIntent);
            }
        });

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        gridView.measure(0, expandSpec);
        gridView.getLayoutParams().height = gridView.getMeasuredHeight();
    }

    @Override
    public void run() {
        try {
            String url = domain + "searchStudy";

            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("search", search));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            result = obj.getString("result");
            id = obj.getInt("id");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
