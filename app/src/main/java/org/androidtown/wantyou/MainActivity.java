package org.androidtown.wantyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static org.androidtown.wantyou.R.id.messageText;


public class MainActivity extends AppCompatActivity implements Runnable {
    private int id;
    private GridView gridView, gridView2;
    private StudySingerAdapter adapter;
    private NoticeSingerAdapter adapter2;
    private ArrayList<SingerItem> singerItemsList, singerItemsList2;
    private Button mainButton, studyButton, noticeButton;
    private LinearLayout main;
    private JSONArray studyList, noticeList;
    private String domain = "http://172.30.5.56:8080/android/";
    private String login;
    private DrawerLayout drawerLayout;
    private View mypageView;
    private TextView myInfo, createStudy, createNotice, messageBox, likeBox, searchStudy, searchNotice, logout, name, email, myArticle, myStudy, count;
    private StudyDialogfragment studyDialogfragment;
    private NoticeDialogfragment noticeDialogfragment;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView2 = (GridView) findViewById(R.id.grid2View);
        singerItemsList = new ArrayList<SingerItem>();
        singerItemsList2 = new ArrayList<SingerItem>();

        Thread th = new Thread(MainActivity.this);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < studyList.length(); i++) {
            JSONObject order = null;
            try {
                order = studyList.getJSONObject(i);
                singerItemsList.add(new SingerItem(Integer.parseInt(order.getString("id")), order.getInt("user_id"), order.getString("title"), order.getString("member"), order.getString("hits"), domain + "getimage?id=" + order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < noticeList.length(); i++) {
            JSONObject order = null;
            try {
                order = noticeList.getJSONObject(i);
                singerItemsList2.add(new SingerItem(Integer.parseInt(order.getString("id")), order.getInt("user_id"), order.getString("title"), order.getString("member"), order.getString("hits"), domain + "getimage?id=" + order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new StudySingerAdapter(id, getApplicationContext(), singerItemsList);
        adapter2 = new NoticeSingerAdapter(id, getApplicationContext(), singerItemsList2);
        gridView.setAdapter(adapter);
        gridView2.setAdapter(adapter2);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(MainActivity.this, SelectedStudyActivity.class);
                selectIntent.putExtra("id", data.getId());
                MainActivity.this.startActivity(selectIntent);
            }
        });

        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(MainActivity.this, SelectedFairActivity.class);
                selectIntent.putExtra("id", data.getId());
                MainActivity.this.startActivity(selectIntent);
            }
        });

        mainButton = (Button) findViewById(R.id.mainButton);
        studyButton = (Button) findViewById(R.id.studyButton);
        noticeButton = (Button) findViewById(R.id.noticeButton);
        main = (LinearLayout) findViewById(R.id.main);


        studyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new StudyFragment());
                fragmentTransaction.commit();
            }
        });

        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new NoticeFragment());
                fragmentTransaction.commit();
            }
        });

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new MainFragment());
                fragmentTransaction.commit();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mypageView = (View) findViewById(R.id.mypage);

        TextView mypageBtn = (TextView) findViewById(R.id.mypageButton);
        name = (TextView) findViewById(R.id.nameText);
        email = (TextView) findViewById(R.id.emailText);
        count = (TextView) findViewById(R.id.countText);
        linearLayout = (LinearLayout) findViewById(R.id.display);

        mypageBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                MypageThread mypageThread = new MypageThread(MainActivity.this);
                mypageThread.start();
                try {
                    mypageThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login = mypageThread.getResult();
                if(mypageThread.getCount() == 0)
                    linearLayout.setVisibility(View.GONE);
                else
                    linearLayout.setVisibility(View.VISIBLE);
                if (!login.equals("no")) {
                    name.setText(mypageThread.getNickname());
                    email.setText(mypageThread.getEmail());
                    count.setText(Integer.toString(mypageThread.getCount()));
                    drawerLayout.openDrawer(mypageView);
                } else if (login.equals("no")) {
                    handler.sendEmptyMessage(0);
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(loginIntent);
                }
            }
        });

        mypageView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        myInfo = (TextView) findViewById(R.id.myInfoText);
        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myInfoIntent = new Intent(MainActivity.this, ChangeActivity.class);
                MainActivity.this.startActivity(myInfoIntent);
            }
        });

        myArticle = (TextView) findViewById(R.id.myArticleText);
        myArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myArticleIntent = new Intent(MainActivity.this, MyArticleActivity.class);
                MainActivity.this.startActivity(myArticleIntent);
            }
        });

        myStudy = (TextView) findViewById(R.id.myStudyText);
        myStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myStudyIntent = new Intent(MainActivity.this, MyStudyActivity.class);
                MainActivity.this.startActivity(myStudyIntent);
            }
        });

        createStudy = (TextView) findViewById(R.id.createStudyText);
        createStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createStudyIntent = new Intent(MainActivity.this, CreateStudyActivity.class);
                MainActivity.this.startActivity(createStudyIntent);
            }
        });

        createNotice = (TextView) findViewById(R.id.createNoticeText);
        createNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNoticeIntent = new Intent(MainActivity.this, CreateNoticeActivity.class);
                MainActivity.this.startActivity(createNoticeIntent);
            }
        });

        messageBox = (TextView) findViewById(messageText);
        messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageBoxIntent = new Intent(MainActivity.this, MessageBoxActivity.class);
                MainActivity.this.startActivity(messageBoxIntent);
            }
        });

        likeBox = (TextView) findViewById(R.id.likeBoxText);
        likeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent likeBoxIntent = new Intent(MainActivity.this, LikeBoxActivity.class);
                MainActivity.this.startActivity(likeBoxIntent);
            }
        });

        searchStudy = (TextView) findViewById(R.id.searchStudyText);
        searchStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyDialogfragment == null)
                    studyDialogfragment = new StudyDialogfragment();
                studyDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
            }
        });

        searchNotice = (TextView) findViewById(R.id.searchNoticeText);
        searchNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeDialogfragment == null)
                    noticeDialogfragment = new NoticeDialogfragment();
                noticeDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
            }
        });

        logout = (TextView) findViewById(R.id.logoutText);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("확인");
                builder.setMessage("정말로 로그아웃하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        LogoutThread logoutThread = new LogoutThread(MainActivity.this);
                        logoutThread.start();
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(loginIntent);
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView2 = (GridView) findViewById(R.id.grid2View);
        singerItemsList = new ArrayList<SingerItem>();
        singerItemsList2 = new ArrayList<SingerItem>();

        Thread th = new Thread(MainActivity.this);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < studyList.length(); i++) {
            JSONObject order = null;
            try {
                order = studyList.getJSONObject(i);
                singerItemsList.add(new SingerItem(Integer.parseInt(order.getString("id")), order.getInt("user_id"), order.getString("title"), order.getString("member"), order.getString("hits"), domain + "getimage?id=" + order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < noticeList.length(); i++) {
            JSONObject order = null;
            try {
                order = noticeList.getJSONObject(i);
                singerItemsList2.add(new SingerItem(Integer.parseInt(order.getString("id")), order.getInt("user_id"), order.getString("title"), order.getString("member"), order.getString("hits"), domain + "getimage?id=" + order.getString("f_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new StudySingerAdapter(id, getApplicationContext(), singerItemsList);
        adapter2 = new NoticeSingerAdapter(id, getApplicationContext(), singerItemsList2);
        gridView.setAdapter(adapter);
        gridView2.setAdapter(adapter2);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(MainActivity.this, SelectedStudyActivity.class);
                selectIntent.putExtra("id", data.getId());
                MainActivity.this.startActivity(selectIntent);
            }
        });

        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(MainActivity.this, SelectedFairActivity.class);
                selectIntent.putExtra("id", data.getId());
                MainActivity.this.startActivity(selectIntent);
            }
        });

        mainButton = (Button) findViewById(R.id.mainButton);
        studyButton = (Button) findViewById(R.id.studyButton);
        noticeButton = (Button) findViewById(R.id.noticeButton);
        main = (LinearLayout) findViewById(R.id.main);

        studyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new StudyFragment());
                fragmentTransaction.commit();
            }
        });

        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new NoticeFragment());
                fragmentTransaction.commit();
            }
        });

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new MainFragment());
                fragmentTransaction.commit();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mypageView = (View) findViewById(R.id.mypage);

        TextView mypageBtn = (TextView) findViewById(R.id.mypageButton);
        name = (TextView) findViewById(R.id.nameText);
        email = (TextView) findViewById(R.id.emailText);
        count = (TextView) findViewById(R.id.countText);
        linearLayout = (LinearLayout) findViewById(R.id.display);

        mypageBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                MypageThread mypageThread = new MypageThread(MainActivity.this);
                mypageThread.start();
                try {
                    mypageThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login = mypageThread.getResult();
                if(mypageThread.getCount() == 0)
                    linearLayout.setVisibility(View.GONE);
                else
                    linearLayout.setVisibility(View.VISIBLE);
                if (!login.equals("no")) {
                    name.setText(mypageThread.getNickname());
                    email.setText(mypageThread.getEmail());
                    count.setText(Integer.toString(mypageThread.getCount()));
                    drawerLayout.openDrawer(mypageView);
                } else if (login.equals("no")) {
                    handler.sendEmptyMessage(0);
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(loginIntent);
                }
            }
        });

        mypageView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        myInfo = (TextView) findViewById(R.id.myInfoText);
        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myInfoIntent = new Intent(MainActivity.this, ChangeActivity.class);
                MainActivity.this.startActivity(myInfoIntent);
            }
        });

        myArticle = (TextView) findViewById(R.id.myArticleText);
        myArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myArticleIntent = new Intent(MainActivity.this, MyArticleActivity.class);
                MainActivity.this.startActivity(myArticleIntent);
            }
        });

        myStudy = (TextView) findViewById(R.id.myStudyText);
        myStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myStudyIntent = new Intent(MainActivity.this, MyStudyActivity.class);
                MainActivity.this.startActivity(myStudyIntent);
            }
        });

        createStudy = (TextView) findViewById(R.id.createStudyText);
        createStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createStudyIntent = new Intent(MainActivity.this, CreateStudyActivity.class);
                MainActivity.this.startActivity(createStudyIntent);
            }
        });

        createNotice = (TextView) findViewById(R.id.createNoticeText);
        createNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNoticeIntent = new Intent(MainActivity.this, CreateNoticeActivity.class);
                MainActivity.this.startActivity(createNoticeIntent);
            }
        });

        messageBox = (TextView) findViewById(messageText);
        messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageBoxIntent = new Intent(MainActivity.this, MessageBoxActivity.class);
                MainActivity.this.startActivity(messageBoxIntent);
            }
        });

        likeBox = (TextView) findViewById(R.id.likeBoxText);
        likeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent likeBoxIntent = new Intent(MainActivity.this, LikeBoxActivity.class);
                MainActivity.this.startActivity(likeBoxIntent);
            }
        });

        searchStudy = (TextView) findViewById(R.id.searchStudyText);
        searchStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyDialogfragment == null)
                    studyDialogfragment = new StudyDialogfragment();
                studyDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
            }
        });

        searchNotice = (TextView) findViewById(R.id.searchNoticeText);
        searchNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeDialogfragment == null)
                    noticeDialogfragment = new NoticeDialogfragment();
                noticeDialogfragment.show(getSupportFragmentManager(), "SearchDialog");
            }
        });

        logout = (TextView) findViewById(R.id.logoutText);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("확인");
                builder.setMessage("정말로 로그아웃하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        LogoutThread logoutThread = new LogoutThread(MainActivity.this);
                        logoutThread.start();
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(loginIntent);
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
        String url = domain + "findMain";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(body);

            id = obj.getInt("id");
            String result = obj.getString("result");
            String result2 = obj.getString("result2");
            studyList = new JSONArray(result);
            noticeList = new JSONArray(result2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
        }
    };
}
