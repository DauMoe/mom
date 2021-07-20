package com.example.mom.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mom.Module.User;
import com.example.mom.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class GroupUserAdapter extends BaseAdapter {
    private Context context;
    private List<User> data;
    private LayoutInflater inflater;

    public GroupUserAdapter(Context context) {
        this.context    = context;
        inflater        = LayoutInflater.from(context);
    }

    public void setData(List<User> data) {
        this.data = data;
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
        ViewHolder v;
        if (convertView == null) {
            convertView     = inflater.inflate(R.layout.group_user_item, null);
            v               = new ViewHolder();
            v.ava           = convertView.findViewById(R.id.ava);
            v.user_display  = convertView.findViewById(R.id.user_display);
            v.user_balance  = convertView.findViewById(R.id.user_balance);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        User x = data.get(position);
//        Glide.with(context).load().into(v.ava);
        v.user_display.setText((x.getEmail() != null)? x.getEmail() : x.getPhone());
        v.user_balance.setText(String.valueOf(x.getAmount()));
        return convertView;
    }

    static class ViewHolder {
        ImageView ava;
        MaterialTextView user_display;
        MaterialTextView user_balance;
    }
}
