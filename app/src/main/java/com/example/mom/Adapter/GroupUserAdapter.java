package com.example.mom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mom.Module.User;
import com.example.mom.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class GroupUserAdapter extends BaseAdapter {
    private Context context;
    private List<User> data = new ArrayList<>();
    private LayoutInflater inflater;

    public GroupUserAdapter(Context context) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
    }

    public void setData(List<User> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(User x) {
        this.data.add(x);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data != null) return data.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder v;
        if (convertView == null) {
            convertView     = inflater.inflate(R.layout.group_user_item, null);
            v               = new ViewHolder();
            v.ava           = convertView.findViewById(R.id.ava);
            v.user_display  = convertView.findViewById(R.id.user_display);
            v.user_balance  = convertView.findViewById(R.id.user_balance);
            v.gr_item       = convertView.findViewById(R.id.gr_item);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        User x = data.get(position);
//        Glide.with(context).load().into(v.ava);
        v.user_display.setText((x.getEmail() != null)? x.getEmail() : x.getPhone());
        v.user_balance.setText("Balance:" +x.getAmount() + x.getUnit());
        return convertView;
    }

    public void removeData(int pos) {
        this.data.remove(pos);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView ava;
        MaterialTextView user_display;
        MaterialTextView user_balance;
        LinearLayout gr_item;
    }
}
