package org.androidtown.wantyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static org.androidtown.wantyou.R.id.messageText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyFragment extends Fragment implements Runnable{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int id;
    private  JSONArray studyList;
    private String domain = "http://172.30.5.56:8080/android/";
    private DrawerLayout drawerLayout;
    private TextView name, email, myInfo, createStudy, createNotice, messageBox, likeBox, searchStudy, searchNotice, logout, myArticle, myStudy, count;
    private View mypageView;
    private StudyDialogfragment studyDialogfragment;
    private NoticeDialogfragment noticeDialogfragment;
    private LinearLayout linearLayout;
    private String login;

    public StudyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudyFragment newInstance(String param1, String param2) {
        StudyFragment fragment = new StudyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private GridView gridView;
    private StudySingerAdapter adapter;
    private ArrayList<SingerItem> singerItemsList;

    @Override
    public void onActivityCreated(Bundle b){
        super.onActivityCreated(b);

        Thread th = new Thread(StudyFragment.this);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gridView = (GridView) getView().findViewById(R.id.gridView);
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

        adapter = new StudySingerAdapter(id, getContext().getApplicationContext(), singerItemsList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem data = (SingerItem) parent.getItemAtPosition(position);
                Intent selectIntent = new Intent(getContext(), SelectedStudyActivity.class);
                selectIntent.putExtra("id", data.getId());
                getContext().startActivity(selectIntent);
            }
        });

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        gridView.measure(0, expandSpec);
        gridView.getLayoutParams().height = gridView.getMeasuredHeight();

        drawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        mypageView = (View)getView().findViewById(R.id.mypage);

        TextView mypageBtn = (TextView) getView().findViewById(R.id.mypageButton);
        name = (TextView) getView().findViewById(R.id.nameText);
        email = (TextView) getView().findViewById(R.id.emailText);
        count = (TextView) getView().findViewById(R.id.countText);
        linearLayout = (LinearLayout) getView().findViewById(R.id.display);

        mypageBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                MypageThread mypageThread = new MypageThread(getContext());
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
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    getContext().startActivity(loginIntent);
                }
            }
        });

        mypageView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        myInfo = (TextView) getView().findViewById(R.id.myInfoText);
        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myInfoIntent = new Intent(getContext(), ChangeActivity.class);
                getContext().startActivity(myInfoIntent);
            }
        });

        myArticle = (TextView) getView().findViewById(R.id.myArticleText);
        myArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myArticleIntent = new Intent(getContext(), MyArticleActivity.class);
                getContext().startActivity(myArticleIntent);
            }
        });

        myStudy = (TextView) getView().findViewById(R.id.myStudyText);
        myStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myStudyIntent = new Intent(getContext(), MyStudyActivity.class);
                getContext().startActivity(myStudyIntent);
            }
        });

        createStudy = (TextView) getView().findViewById(R.id.createStudyText);
        createStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createStudyIntent = new Intent(getContext(), CreateStudyActivity.class);
                getContext().startActivity(createStudyIntent);
            }
        });

        createNotice = (TextView) getView().findViewById(R.id.createNoticeText);
        createNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNoticeIntent = new Intent(getContext(), CreateNoticeActivity.class);
                getContext().startActivity(createNoticeIntent);
            }
        });

        messageBox = (TextView) getView().findViewById(messageText);
        messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageBoxIntent = new Intent(getContext(), MessageBoxActivity.class);
                getContext().startActivity(messageBoxIntent);
            }
        });

        likeBox = (TextView) getView().findViewById(R.id.likeBoxText);
        likeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent likeBoxIntent = new Intent(getContext(), LikeBoxActivity.class);
                getContext().startActivity(likeBoxIntent);
            }
        });

        searchStudy = (TextView) getView().findViewById(R.id.searchStudyText);
        searchStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyDialogfragment == null)
                    studyDialogfragment = new StudyDialogfragment();
                studyDialogfragment.show(getActivity().getSupportFragmentManager(), "SearchDialog");
            }
        });

        searchNotice = (TextView) getView().findViewById(R.id.searchNoticeText);
        searchNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeDialogfragment == null)
                    noticeDialogfragment = new NoticeDialogfragment();
                noticeDialogfragment.show(getActivity().getSupportFragmentManager(), "SearchDialog");
            }
        });

        logout = (TextView) getView().findViewById(R.id.logoutText);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("확인");
                builder.setMessage("정말로 로그아웃하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        LogoutThread logoutThread = new LogoutThread(getContext());
                        logoutThread.start();
                        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                        getContext().startActivity(loginIntent);
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void run() {
        String url = domain+"findAllStudy";

        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpGet);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);

            id = obj.getInt("id");
            String result = obj.getString("result");

            studyList = new JSONArray(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
