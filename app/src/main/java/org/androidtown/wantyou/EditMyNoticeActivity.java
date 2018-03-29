package org.androidtown.wantyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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

public class EditMyNoticeActivity extends AppCompatActivity implements Runnable {
    private TextView title, startDate, endDate, content, category, hit, nickname;
    private ImageView imageView, like;
    private int id, uId;
    private JSONObject order = null;
    private String domain = "http://172.30.5.56:8080/android/";
    private MessageInNoticeDialogfragment messageInNoticeDialogfragment;
    private int userId;
    private JSONObject obj;
    private Button edit, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_notice);

        title = (TextView) findViewById(R.id.titleText);
        startDate = (TextView) findViewById(R.id.startdate);
        endDate = (TextView) findViewById(R.id.enddate);
        content = (TextView) findViewById(R.id.fairContent);
        category = (TextView) findViewById(R.id.categoryText);
        hit = (TextView) findViewById(R.id.hitText);
        imageView = (ImageView) findViewById(R.id.selectedFairImage);
        like = (ImageView) findViewById(R.id.like);
        nickname = (TextView) findViewById(R.id.nameText);
        edit = (Button) findViewById(R.id.edit);
        delete = (Button) findViewById(R.id.delete);

        Intent selectIntent = getIntent();
        id = selectIntent.getExtras().getInt("id");
        Thread th = new Thread(EditMyNoticeActivity.this);
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
                    LikeThread likeThread = new LikeThread(EditMyNoticeActivity.this, id);

                    likeThread.start();
                    try {
                        likeThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String message = likeThread.getMessage();

                    if (message.equals("로그인해주세요")) {
                        Intent loginIntent = new Intent(EditMyNoticeActivity.this, LoginActivity.class);
                        EditMyNoticeActivity.this.startActivity(loginIntent);
                        return;
                    }
                    like.setImageResource(R.mipmap.ic_favorite_pink_36dp);

                } else {
                    like.setImageResource(R.mipmap.ic_favorite_border_pink_36dp);
                    LikeCancelThread likeCancelThread = new LikeCancelThread(EditMyNoticeActivity.this, id);
                    likeCancelThread.start();
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMyArticleThread editMyArticleThread = new EditMyArticleThread(EditMyNoticeActivity.this, id, title.getText().toString(), content.getText().toString());
                editMyArticleThread.start();
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditMyNoticeActivity.this);
                builder.setTitle("확인");
                builder.setMessage("해당 게시글을 정말로 삭제하시겠습니까?\n해당글과 관련된 모든 사항이 삭제됩니다");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        DeleteMyArticleThread deleteMyArticleThread = new DeleteMyArticleThread(EditMyNoticeActivity.this, id);
                        deleteMyArticleThread.start();
                        finish();
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
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
                content.setText(order.getString("content"));
                category.setText(order.getString("category"));
                hit.setText(order.getString("hits"));
                GetImageThread thread = new GetImageThread(EditMyNoticeActivity.this, domain + "getimage?id=" + order.getString("f_id"));
                thread.start();
                Bitmap bitmap = thread.getBitmap();
                imageView.setImageBitmap(bitmap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
