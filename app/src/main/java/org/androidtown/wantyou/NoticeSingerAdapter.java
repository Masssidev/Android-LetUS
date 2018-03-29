package org.androidtown.wantyou;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LG on 2017-08-14.
 */
class NoticeSingerAdapter extends BaseAdapter implements Runnable {
    private int id = -1;
    private Context context;
    ArrayList<SingerItem> items = new ArrayList<SingerItem>();

    public NoticeSingerAdapter(int id, Context context, ArrayList<SingerItem> items) {
        this.id = id;
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(SingerItem item) {
        items.add(item);
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.notice_singer_item, null);

        TextView textView = (TextView) v.findViewById(R.id.textView);
        TextView textView3 = (TextView) v.findViewById(R.id.textView3);
        final ImageView likeView = (ImageView) v.findViewById(R.id.likeView);
        ImageView bgd = (ImageView) v.findViewById(R.id.bgd);

        textView.setText(items.get(position).getTitle());
        textView3.setText(items.get(position).getVisit());

        if (id == items.get(position).getUser_id())
            likeView.setImageResource(R.mipmap.ic_favorite_white_24dp);
        else
            likeView.setImageResource(R.mipmap.ic_favorite_border_white_24dp);

        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindLikeCheckedThread findLikeCheckedThread = new FindLikeCheckedThread(items.get(position).getId(), context);

                findLikeCheckedThread.start();
                try {
                    findLikeCheckedThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String message = findLikeCheckedThread.getResult();

                if (message.equals("로그인해주세요")) {
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                } else if (message.equals("추가"))
                    likeView.setImageResource(R.mipmap.ic_favorite_white_24dp);
                else
                    likeView.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
            }
        });

        bgd.setImageBitmap(getBitmap(items.get(position).getUrl()));

        bgd.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);
        v.setTag(items.get(position).getTitle());

        return v;
    }

    private URL imgUrl = null;
    private HttpURLConnection connection = null;
    private InputStream is = null;
    private Bitmap retBitmap = null;

    private Bitmap getBitmap(String url) {
        try {
            imgUrl = new URL(url);
            Thread th = new Thread(this);
            th.start();
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retBitmap;
    }

    @Override
    public synchronized void run() {
        try {
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}