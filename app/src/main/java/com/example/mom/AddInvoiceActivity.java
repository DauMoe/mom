package com.example.mom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.mom.databinding.ActivityAddInvoiceBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddInvoiceActivity extends AppCompatActivity {
    private ActivityAddInvoiceBinding binding;
    protected FirebaseFirestore db;
    protected FirebaseAuth user;
    List<String> list_cate = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_add_invoice);
        user        = FirebaseAuth.getInstance();
        db          = FirebaseFirestore.getInstance();
        list_cate.clear();
        db.collection(DefineVars.CONSUMING_CATE).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                list_cate.add(i.toString());
            }
            adapter = new ArrayAdapter(this, R.layout.list_item, list_cate);
            binding.consumingCateItem.setAdapter(adapter);
        });
    }
}