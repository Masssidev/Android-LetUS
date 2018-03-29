package org.androidtown.wantyou;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LG on 2017-09-21.
 */

public class ReceivedListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ReceivedMessage> receivedMessageList;

    public ReceivedListAdapter(Context context, ArrayList<ReceivedMessage> receivedMessageList) {
        this.context = context;
        this.receivedMessageList = receivedMessageList;
    }

    @Override
    public int getCount() {
        return receivedMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return receivedMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.message_receive, null);

        TextView messageText = (TextView)v.findViewById(R.id.messageText);
        TextView nameText = (TextView)v.findViewById(R.id.nameText);
        TextView dateText = (TextView)v.findViewById(R.id.dateText);
        TextView readText = (TextView) v.findViewById(R.id.read);

        if (receivedMessageList.get(i).getRead()==0)
            readText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_primary_24dp, 0, 0, 0);
        else
            readText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_black_24dp, 0, 0, 0);

        messageText.setText(receivedMessageList.get(i).getMessage());
        nameText.setText(receivedMessageList.get(i).getName());
        dateText.setText(receivedMessageList.get(i).getDate());

        v.setTag(receivedMessageList.get(i).getMessage());
        return v;
    }
}
