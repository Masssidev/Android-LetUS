package org.androidtown.wantyou;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LG on 2018-03-21.
 */

public class MyMemberAdapter extends BaseAdapter{
    private Context context;
    ArrayList<Member> items = new ArrayList<Member>();

    public MyMemberAdapter(Context context, ArrayList<Member> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v = View.inflate(context, R.layout.member, null);
        TextView textView = (TextView) v.findViewById(R.id.nicknameText);
        TextView textView2 = (TextView) v.findViewById(R.id.emailText);

        textView.setText(items.get(position).getNickname());
        textView2.setText(items.get(position).getEmail());

        return v;
    }
}
