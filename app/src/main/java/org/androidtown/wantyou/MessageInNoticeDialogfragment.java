package org.androidtown.wantyou;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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

public class MessageInNoticeDialogfragment extends DialogFragment implements Runnable{
    private String message, success;
    private String domain = "http://172.30.5.56:8080/android/";
    private EditText messageText;
    private SelectedFairActivity activity;

    public String getNick() {
        return getArguments().getString("nick");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (SelectedFairActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("쪽지 보내기");
        builder.setMessage("\n받는 사람: " + getNick());
        final View rootView = activity.getLayoutInflater().inflate(R.layout.message, null);
        messageText = (EditText) rootView.findViewById(R.id.messageText);

        builder.setView(rootView);
        builder.setNegativeButton("보내기", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread th = new Thread(MessageInNoticeDialogfragment.this);
                th.start();
            }
        });

        builder.setPositiveButton("취소", null);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void run() {
        String url = domain + "sendMessage";
        String receive = getNick();
        String con = messageText.getText().toString();

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("receive", receive));
            postData.add(new BasicNameValuePair("con", con));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);
            message = obj.getString("message");
            success = obj.getString("success");


            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    };
}
