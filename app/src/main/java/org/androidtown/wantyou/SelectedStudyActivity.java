package org.androidtown.wantyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class SelectedStudyActivity extends AppCompatActivity implements Runnable {
    private TextView title, startDate, endDate, fairContent, category, hit, member, nickname, count;
    private ImageView imageView, like;
    private Button apply;
    private int id, uId;
    private JSONObject order = null;
    private String domain = "http://172.30.5.56:8080/android/";
    private MessageInStudyDialogfragment messageInStudyDialogfragment;
    private int userId;
    private JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_study);

        title = (TextView) findViewById(R.id.titleText);
        startDate = (TextView) findViewById(R.id.startdate);
        endDate = (TextView) findViewById(R.id.enddate);
        fairContent = (TextView) findViewById(R.id.fairContent);
        category = (TextView) findViewById(R.id.categoryText);
        hit = (TextView) findViewById(R.id.hitText);
        member = (TextView) findViewById(R.id.memberText);
        apply = (Button) findViewById(R.id.apply);
        imageView = (ImageView) findViewById(R.id.selectedFairImage);
        like = (ImageView) findViewById(R.id.like);
        nickname = (TextView) findViewById(R.id.nameText);
        count = (TextView) findViewById(R.id.countText);

        Intent selectIntent = getIntent();
        id = selectIntent.getExtras().getInt("id");
        final Thread th = new Thread(SelectedStudyActivity.this);
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
                    LikeThread likeThread = new LikeThread(SelectedStudyActivity.this, id);

                    likeThread.start();
                    try {
                        likeThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String message = likeThread.getMessage();

                    if (message.equals("로그인해주세요")) {
                        Intent loginIntent = new Intent(SelectedStudyActivity.this, LoginActivity.class);
                        SelectedStudyActivity.this.startActivity(loginIntent);
                        return;
                    }
                    like.setImageResource(R.mipmap.ic_favorite_pink_36dp);

                } else {
                    like.setImageResource(R.mipmap.ic_favorite_border_pink_36dp);
                    LikeCancelThread likeCancelThread = new LikeCancelThread(SelectedStudyActivity.this, id);
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
                    if (messageInStudyDialogfragment == null) {
                        messageInStudyDialogfragment = new MessageInStudyDialogfragment();
                    }
                    Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                    bundle.putString("nick", nick); // key , value
                    messageInStudyDialogfragment.setArguments(bundle);
                    messageInStudyDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
                }
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyStudyThread applyStudyThread = new ApplyStudyThread(SelectedStudyActivity.this, id);
                applyStudyThread.start();

                try {
                    applyStudyThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (applyStudyThread.getResult().equals("yes")) {
                    th.start();
                }
            }
        });

    }

    @Override
    public void run() {
        String url = domain + "findOneStudy";

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
                member.setText(order.getString("member"));
                count.setText(Integer.toString(obj.getInt("count")));
                GetImageThread thread = new GetImageThread(SelectedStudyActivity.this, domain + "getimage?id=" + order.getString("f_id"));
                thread.start();
                Bitmap bitmap = thread.getBitmap();
                imageView.setImageBitmap(bitmap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
