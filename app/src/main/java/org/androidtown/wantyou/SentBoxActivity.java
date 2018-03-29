package org.androidtown.wantyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import java.util.List;

public class SentBoxActivity extends AppCompatActivity implements Runnable{

    private ListView messageListView;
    private SentListAdapter adapter;
    private List<SentMessage> sentMessageList;
    private JSONArray sentList=null;
    private String message;
    private String domain = "http://172.30.5.56:8080/android/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_box);

        messageListView = (ListView)findViewById(R.id.messageListView);
        sentMessageList = new ArrayList<SentMessage>();

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SentMessage data = (SentMessage) parent.getItemAtPosition(position);
                Intent selectMessageIntent = new Intent(SentBoxActivity.this, SentMessageActivity.class);
                selectMessageIntent.putExtra("id", data.getId());
                SentBoxActivity.this.startActivity(selectMessageIntent);
            }
        });

        messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemIndex = position;
                final AdapterView adapterView = parent;
                AlertDialog.Builder builder = new AlertDialog.Builder(SentBoxActivity.this);
                builder.setTitle("확인");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        SentMessage data = (SentMessage) adapterView.getItemAtPosition(itemIndex);
                        RemoveSentMessageThread removeSentMessageThread = new RemoveSentMessageThread(SentBoxActivity.this, data.getId());
                        removeSentMessageThread.start();
                        sentMessageList.remove(itemIndex);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            }
        });

        Thread th = new Thread(SentBoxActivity.this);
        th.start();

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sentList.length(); i++){

            JSONObject order = null;

            try {

                order = sentList.getJSONObject(i);
                sentMessageList.add(new SentMessage(Integer.parseInt(order.getString("id")), order.getString("content"), order.getString("nickname"), order.getString("write_date")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        adapter = new SentListAdapter(this, sentMessageList);

        messageListView.setAdapter(adapter);

    }

    @Override
    public void run() {
        String url = domain + "sentList";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            String result = obj.getString("result");
            sentList = new JSONArray(result);

            if(result.equals("[]")) {
                message = "보낸 쪽지함이 비었습니다";
                handler.sendEmptyMessage(0);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

}
