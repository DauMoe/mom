package com.example.mom.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mom.DefineVars;
import com.example.mom.Module.Cates;
import com.example.mom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CatesAdapter extends BaseAdapter {
    List<Cates> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    boolean isEarnings;

    public void setData(List<Cates> data, boolean isEarnings) {
        this.data       = data;
        this.isEarnings = isEarnings;
        notifyDataSetChanged();
    }

    public CatesAdapter(Context context) {
        inflater     = LayoutInflater.from(context);
        this.context = context;
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
            convertView     = inflater.inflate(R.layout.cate_item, null);
            v               = new ViewHolder();
            v.name          = convertView.findViewById(R.id.cate_name);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        v.name.setText(data.get(position).getName());
        return convertView;
    }

    static class ViewHolder {
        TextView name;
    }
}
