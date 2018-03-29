package org.androidtown.wantyou;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LG on 2017-09-21.
 */

public class SentListAdapter extends BaseAdapter {
    private Context context;
    private List<SentMessage> sentMessageList;

    public SentListAdapter(Context context, List<SentMessage> sentMessageList) {
        this.context = context;
        this.sentMessageList = sentMessageList;
    }

    @Override
    public int getCount() {
        return sentMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return sentMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.message_send, null);

        SentMessage item = sentMessageList.get(i); // 데이터 항목을 꺼낸다
         if (item != null) {
             TextView messageText = (TextView) v.findViewById(R.id.messageText);
             TextView nameText = (TextView) v.findViewById(R.id.nameText);
             TextView dateText = (TextView) v.findViewById(R.id.dateText);

             messageText.setText(sentMessageList.get(i).getMessage());
             nameText.setText(sentMessageList.get(i).getName());
             dateText.setText(sentMessageList.get(i).getDate());

             v.setTag(sentMessageList.get(i).getMessage());
         }
        return v;
    }
}
