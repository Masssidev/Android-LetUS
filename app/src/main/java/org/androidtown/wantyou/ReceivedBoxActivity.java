package org.androidtown.wantyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class ReceivedBoxActivity extends AppCompatActivity implements Runnable {

    private ListView messageListView;
    private ReceivedListAdapter adapter;
    private ArrayList<ReceivedMessage> receivedMessageList;
    private JSONArray receivedList = null;
    private String message;
    private String domain = "http://172.30.5.56:8080/android/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_box);

        messageListView = (ListView) findViewById(R.id.messageListView);
        receivedMessageList = new ArrayList<ReceivedMessage>();

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceivedMessage data = (ReceivedMessage) parent.getItemAtPosition(position);
                Intent selectMessageIntent = new Intent(ReceivedBoxActivity.this, ReceivedMessageActivity.class);
                selectMessageIntent.putExtra("id", data.getId());
                ReceivedBoxActivity.this.startActivity(selectMessageIntent);
            }
        });

        messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemIndex = position;
                final AdapterView adapterView = parent;
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceivedBoxActivity.this);
                builder.setTitle("확인");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        SentMessage data = (SentMessage) adapterView.getItemAtPosition(itemIndex);
                        RemoveReceivedMessageThread removeReceivedMessageThread = new RemoveReceivedMessageThread(ReceivedBoxActivity.this, data.getId());
                        removeReceivedMessageThread.start();
                        receivedMessageList.remove(itemIndex);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            }
        });

        Thread th = new Thread(ReceivedBoxActivity.this);
        th.start();

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < receivedList.length(); i++) {

            JSONObject order = null;

            try {
                order = receivedList.getJSONObject(i);
                receivedMessageList.add(new ReceivedMessage(Integer.parseInt(order.getString("id")), order.getString("content"), order.getString("nickname"), order.getString("write_date"), order.getInt("read")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new ReceivedListAdapter(this, receivedMessageList);

        for(int i=0; i<receivedMessageList.size(); ++i){
            Log.d("ttt", Integer.toString(receivedMessageList.get(i).getRead()));
        }

        messageListView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_receive_box);

        messageListView = (ListView) findViewById(R.id.messageListView);
        receivedMessageList = new ArrayList<ReceivedMessage>();

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceivedMessage data = (ReceivedMessage) parent.getItemAtPosition(position);
                Intent selectMessageIntent = new Intent(ReceivedBoxActivity.this, ReceivedMessageActivity.class);
                selectMessageIntent.putExtra("id", data.getId());
                ReceivedBoxActivity.this.startActivity(selectMessageIntent);
            }
        });

        messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemIndex = position;
                final AdapterView adapterView = parent;
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceivedBoxActivity.this);
                builder.setTitle("확인");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        ReceivedMessage data = (ReceivedMessage) adapterView.getItemAtPosition(itemIndex);
                        RemoveReceivedMessageThread removeReceivedMessageThread = new RemoveReceivedMessageThread(ReceivedBoxActivity.this, data.getId());
                        removeReceivedMessageThread.start();
                        receivedMessageList.remove(itemIndex);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            }
        });

        Thread th = new Thread(ReceivedBoxActivity.this);
        th.start();

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < receivedList.length(); i++) {

            JSONObject order = null;

            try {
                order = receivedList.getJSONObject(i);
                receivedMessageList.add(new ReceivedMessage(Integer.parseInt(order.getString("id")), order.getString("content"), order.getString("nickname"), order.getString("write_date"), order.getInt("read")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new ReceivedListAdapter(this, receivedMessageList);

        for(int i=0; i<receivedMessageList.size(); ++i){
            Log.d("ttt", Integer.toString(receivedMessageList.get(i).getRead()));
        }

        messageListView.setAdapter(adapter);

    }

    @Override
    public void run() {
        String url = domain + "receivedList";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            String result = obj.getString("result");

            receivedList = new JSONArray(result);

            if(result.equals("[]")) {
                message = "받은 쪽지함이 비었습니다";
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
