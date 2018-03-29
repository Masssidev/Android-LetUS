package org.androidtown.wantyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by LG on 2018-01-18.
 */

public class LikeListAdapter extends BaseAdapter implements Runnable{
    private Context context;
    private List<Like> likeList;

    public LikeListAdapter(Context context, List<Like> likeList) {
        this.context = context;
        this.likeList = likeList;
    }

    @Override
    public int getCount() {
        return likeList.size();
    }

    @Override
    public Object getItem(int i) {
        return likeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.like, null);

        TextView titleText = (TextView)v.findViewById(R.id.titleText);
        TextView contentText = (TextView)v.findViewById(R.id.contentText);
        ImageView image = (ImageView) v.findViewById(R.id.likeImage);


        titleText.setText(likeList.get(i).getTitle().toString());
        contentText.setText(likeList.get(i).getContent().toString());

        image.setImageBitmap(getBitmap(likeList.get(i). getUrl()));
        v.setTag(likeList.get(i).getTitle());

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
