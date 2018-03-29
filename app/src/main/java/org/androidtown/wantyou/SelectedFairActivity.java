package org.androidtown.wantyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectedFairActivity extends AppCompatActivity implements Runnable {

    private TextView title, startDate, endDate, fairContent, category, hit, nickname;
    private ImageView imageView, like;
    private int id, uId;
    private JSONObject order = null;
    private String domain = "http://172.30.5.56:8080/android/";
    private MessageInNoticeDialogfragment messageInNoticeDialogfragment;
    private int userId;
    private JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_fair);

        title = (TextView) findViewById(R.id.titleText);
        startDate = (TextView) findViewById(R.id.startdate);
        endDate = (TextView) findViewById(R.id.enddate);
        fairContent = (TextView) findViewById(R.id.fairContent);
        category = (TextView) findViewById(R.id.categoryText);
        hit = (TextView) findViewById(R.id.hitText);
        imageView = (ImageView) findViewById(R.id.selectedFairImage);
        like = (ImageView) findViewById(R.id.like);
        nickname = (TextView) findViewById(R.id.nameText);

        Intent selectIntent = getIntent();
        id = selectIntent.getExtras().getInt("id");
        Thread th = new Thread(SelectedFairActivity.this);
        th.start();

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            uId = obj.getInt("id");
            userId = order.getInt("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (uId == userId)
            like.setImageResource(R.mipmap.ic_favorite_pink_36dp);
        else
            like.setImageResource(R.mipmap.ic_favorite_border_pink_36dp);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uId != userId) {
                    LikeThread likeThread = new LikeThread(SelectedFairActivity.this, id);

                    likeThread.start();
                    try {
                        likeThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String message = likeThread.getMessage();

                    if (message.equals("로그인해주세요")) {
                        Intent loginIntent = new Intent(SelectedFairActivity.this, LoginActivity.class);
                        SelectedFairActivity.this.startActivity(loginIntent);
                        return;
                    }
                    like.setImageResource(R.mipmap.ic_favorite_pink_36dp);

                } else {
                    like.setImageResource(R.mipmap.ic_favorite_border_pink_36dp);
                    LikeCancelThread likeCancelThread = new LikeCancelThread(SelectedFairActivity.this, id);
                    likeCancelThread.start();
                }
            }
        });

        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uId == -1)
                    return;
                else {
                    String nick = nickname.getText().toString();
                    if (messageInNoticeDialogfragment == null) {
                        messageInNoticeDialogfragment = new MessageInNoticeDialogfragment();
                    }
                    Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                    bundle.putString("nick", nick); // key , value
                    messageInNoticeDialogfragment.setArguments(bundle);
                    messageInNoticeDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
                }
            }
        });
    }

    @Override
    public void run() {
        String url = domain + "findOneFair";

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("id", Integer.toString(id)));

            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(request);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            obj = new JSONObject(body);
            String result = obj.getString("result");

            order = new JSONObject(result);
            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            try {
                nickname.setText(order.getString("nickname"));
                title.setText(order.getString("title"));
                startDate.setText(order.getString("start_date"));
                endDate.setText(order.getString("expire_date"));
                fairContent.setText(order.getString("content"));
                category.setText(order.getString("category"));
                hit.setText(order.getString("hits"));
                GetImageThread thread = new GetImageThread(SelectedFairActivity.this, domain + "getimage?id=" + order.getString("f_id"));
                thread.start();
                Bitmap bitmap = thread.getBitmap();
                imageView.setImageBitmap(bitmap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
