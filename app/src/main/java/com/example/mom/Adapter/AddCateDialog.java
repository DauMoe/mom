package com.example.mom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mom.DefineVars;
import com.example.mom.MainActivity;
import com.example.mom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddCateDialog extends DialogFragment {
    Button add_cancel, add_cate;
    TextInputLayout add_cate_name;
    AutoCompleteTextView add_cate_type_item;
    protected FirebaseFirestore db;
    protected FirebaseUser user;
    String[] cates = {"earnings", "consuming"};
    ArrayAdapter adapter;
    Context context;
    String collection;
    HashMap<String, Object> updateData = new HashMap<>();

    public AddCateDialog(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v              = inflater.inflate(R.layout.add_cate_dialog, container,false);
        add_cancel          = v.findViewById(R.id.add_cate_cancel);
        add_cate            = v.findViewById(R.id.add_cate);
        add_cate_name       = v.findViewById(R.id.add_cate_name);
        add_cate_type_item  = v.findViewById(R.id.add_cate_type_item);
        user                = FirebaseAuth.getInstance().getCurrentUser();
        db                  = FirebaseFirestore.getInstance();
        adapter             = new ArrayAdapter(context, R.layout.list_item, cates);
        add_cate_type_item.setAdapter(adapter);
        getDialog().setTitle("Add category");

        add_cancel.setOnClickListener(v1 -> dismiss());
        add_cate.setOnClickListener(v1 -> {
            String name         = add_cate_name.getEditText().getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(context, "Enter cate name", Toast.LENGTH_LONG).show();
                return;
            }
            String selectedCate = add_cate_type_item.getText().toString();
            updateData.clear();
            updateData.put("uniqueID", user.getUid());
            updateData.put("name", name);
            if (selectedCate.equals(cates[0])) {
                collection = DefineVars.EARNING_CATE;
            } else {
                collection = DefineVars.CONSUMING_CATE;
            }
            db.collection(collection).document().set(updateData)
                    .addOnSuccessListener(aVoid -> {
                        dismiss();
                        Toast.makeText(context, "Add successful", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show());
        });
        return v;
    }
}
