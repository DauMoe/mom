package com.example.mom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mom.Adapter.CatesAdapter;
import com.example.mom.Module.Cates;
import com.example.mom.databinding.ActivityAddCateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.mom.DefineVars.GROUP_USERS;

public class AddCateActivity extends AppCompatActivity {
    private ActivityAddCateBinding binding;
    protected FirebaseFirestore db;
    protected FirebaseUser user;
    CatesAdapter adapter;
    List<Cates> data = new ArrayList<>();
    String collection;
    boolean isEarnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                     = DataBindingUtil.setContentView(this, R.layout.activity_add_cate);
        user                        = FirebaseAuth.getInstance().getCurrentUser();
        db                          = FirebaseFirestore.getInstance();
        adapter                     = new CatesAdapter(getApplicationContext());
        binding.cateRcv.setAdapter(adapter);
        binding.cateRcv.setOnItemClickListener((parent, view, position, id) -> {
            Cates x = data.get(position);
            if (isEarnings) collection = DefineVars.EARNING_CATE;
            else collection = DefineVars.CONSUMING_CATE;
            new MaterialAlertDialogBuilder(AddCateActivity.this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setTitle("Delete")
                    .setMessage("Remove "+x.getName()+" cate ?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Remove", (dialog, which) -> {
                        db.collection(collection)
                                .whereEqualTo("uniqueID", user.getUid())
                                .whereEqualTo("name", x.getName())
                                .limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                                        db.collection(collection).document(i.getId())
                                            .delete()
                                            .addOnSuccessListener(aVoid -> data.remove(position))
                                            .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show());
                                    }
                                });
                            }).show();
        });
        binding.filterTime.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.earnings_addcate:
                    GetEarningsCate();
                    break;
                case R.id.consuming_addcate:
                    GetConsumingCate();
                    break;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetEarningsCate();
    }

    private void GetEarningsCate() {
        data.clear();
        db.collection(DefineVars.EARNING_CATE).whereEqualTo("uniqueID", user.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                        Cates x = i.toObject(Cates.class);
                        data.add(x);
                    }
                    adapter.setData(data, true);
                });
    }

    private void GetConsumingCate() {
        data.clear();
        db.collection(DefineVars.CONSUMING_CATE).whereEqualTo("uniqueID", user.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                        Cates x = i.toObject(Cates.class);
                        data.add(x);
                    }
                    adapter.setData(data, false);
                });
    }
}