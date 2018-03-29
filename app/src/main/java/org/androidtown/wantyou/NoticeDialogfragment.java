package org.androidtown.wantyou;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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

/**
 * Created by LG on 2018-01-08.
 */

public class NoticeDialogfragment extends DialogFragment implements Runnable{
    private int id;
    private String search, result;
    private String domain = "http://172.30.5.56:8080/android/";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("공모전 검색");
        builder.setMessage("\n제목이나 내용에 해당하는\n검색어를 입력해주세요");
        final View rootView = activity.getLayoutInflater().inflate(R.layout.search, null);
        final EditText searchText = (EditText) rootView.findViewById(R.id.searchText);

        builder.setView(rootView);
        builder.setNegativeButton("검색", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                search = searchText.getText().toString();

                Thread th = new Thread(NoticeDialogfragment.this);
                th.start();
            }
        });

        builder.setPositiveButton("취소", null);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void run() {
        try {
            String url = domain + "searchNotice";

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
            Log.d("login", Integer.toString(id));

            if(result.equals("[]"))
                handler.sendEmptyMessage(0);
            else if(result.equals("검색어를 입력해주세요"))
                handler.sendEmptyMessage(1);
            else
                handler.sendEmptyMessage(2);

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
                    Toast.makeText(getContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Intent resultIntent = new Intent(getContext(), ResultNoticeActivity.class);
                    resultIntent.putExtra("result", result);
                    resultIntent.putExtra("id", id);
                    resultIntent.putExtra("search", search);
                    getContext().startActivity(resultIntent);
                    break;
            }
        }
    };
}
