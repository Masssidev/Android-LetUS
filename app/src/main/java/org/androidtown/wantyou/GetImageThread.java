package org.androidtown.wantyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LG on 2017-11-20.
 */

public class GetImageThread extends Thread {
    private String url;
    private Context context;
    private HttpURLConnection connection = null;
    private InputStream is = null;
    private Bitmap retBitmap = null;
    private URL imgUrl = null;

    public GetImageThread(Context context, String url) {
        this.url = url;
        this.context=context;
    }

    public Bitmap getBitmap() {
        try {
            imgUrl = new URL(url);
            Thread th= new Thread(this);
            th.start();
            th.join();
        }catch(Exception e){
            e.printStackTrace();
        }
        return retBitmap;
    }

    @Override
    public synchronized void run() {
        try{
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(connection!=null) {
                connection.disconnect();
            }
        }
    }

}
