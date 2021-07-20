package com.example.mom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mom.Adapter.ExchangeAdapter;
import com.example.mom.Module.Events;
import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityGroupExchangeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.mom.DefineVars.*;

public class DetailUserPaymentEventsActivity extends AppCompatActivity {
    private ActivityGroupExchangeBinding binding;
    protected User x;
    protected FirebaseFirestore db;
    List<Events> data = new ArrayList<>();
    ExchangeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_group_exchange);
        db          = FirebaseFirestore.getInstance();
        Intent c    = getIntent();
        x           = (User) c.getSerializableExtra(DETAIL_PAYMENTS);
        binding.userDetail.setText((x.getEmail() != null) ? x.getEmail() : x.getPhone());
        //Recycleview init
        adapter                     = new ExchangeAdapter(getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        binding.detailRcv.setLayoutManager(manager);
        binding.detailRcv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.back.setOnClickListener(v -> onBackPressed());
        db.collection(PAYMENT_EVENTS).whereEqualTo("uniqueID", x.getUniqueID()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        data.clear();
                        for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                            Events f = i.toObject(Events.class);
                            data.add(f);
                            if (data.size() > 0) {
                                adapter.setData(data);
                                binding.noDetailExchange.setVisibility(View.GONE);
                                binding.detailRcv.setVisibility(View.VISIBLE);
                            } else {
                                binding.noDetailExchange.setVisibility(View.VISIBLE);
                                binding.detailRcv.setVisibility(View.GONE);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fetch firestore failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}