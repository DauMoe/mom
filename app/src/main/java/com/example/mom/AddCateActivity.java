package com.example.mom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.mom.Adapter.AddCateDialog;
import com.example.mom.Adapter.CatesAdapter;
import com.example.mom.Adapter.CustomDialog;
import com.example.mom.Module.Cates;
import com.example.mom.databinding.ActivityAddCateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                     = DataBindingUtil.setContentView(this, R.layout.activity_add_cate);
        user                        = FirebaseAuth.getInstance().getCurrentUser();
        db                          = FirebaseFirestore.getInstance();
        progressDialog              = new ProgressDialog(this);
        adapter                     = new CatesAdapter(getApplicationContext());
        binding.cateRcv.setAdapter(adapter);
        GetEarningsCate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.cateRcv.setOnItemLongClickListener((parent, view, position, id) -> {
            Cates x = data.get(position);
            new MaterialAlertDialogBuilder(AddCateActivity.this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setTitle("Delete")
                    .setMessage("Remove "+x.getName()+" cate ?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Remove", (dialog, which) -> {
                        db.collection(collection)
                                .whereEqualTo("uniqueID", x.getUniqueID())
                                .whereEqualTo("name", x.getName())
                                .limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                                db.collection(collection).document(i.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            data.remove(x);
                                            adapter.setData(data, (collection.equals("earnings") ? true: false));
                                            Toast.makeText(getApplicationContext(), "Deteted", Toast.LENGTH_LONG).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show();
                                        });
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }).show();
            return true;
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
        binding.addCateFab.setOnClickListener(v -> {
            FragmentManager fm              = getSupportFragmentManager();
            AddCateDialog addCateDialog     = new AddCateDialog(getApplicationContext());
            addCateDialog.show(fm, "");
        });
    }

    private void GetEarningsCate() {
        data.clear();
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        collection = DefineVars.EARNING_CATE;
        db.collection(collection).whereEqualTo("uniqueID", user.getUid()).addSnapshotListener((value, error) -> {
            progressDialog.dismiss();
            if (!collection.equals(DefineVars.EARNING_CATE)) return;
            if (error != null) {
                Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show();
                return;
            }
            data.clear();
            if (value.size() != 0) {
                for (QueryDocumentSnapshot i: value) {
                    Cates x = i.toObject(Cates.class);
                    data.add(x);
                }
            }
            adapter.setData(data, true);
        });


//        db.collection(collection).whereEqualTo("uniqueID", user.getUid()).get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
//                        Cates x = i.toObject(Cates.class);
//                        data.add(x);
//                    }
//                    progressDialog.dismiss();
//                    adapter.setData(data, true);
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show();
//                    }
//                });
    }

    private void GetConsumingCate() {
        data.clear();
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        collection = DefineVars.CONSUMING_CATE;
        db.collection(collection).whereEqualTo("uniqueID", user.getUid()).addSnapshotListener((value, error) -> {
            progressDialog.dismiss();
            if (!collection.equals(DefineVars.CONSUMING_CATE)) return;
            if (error != null) {
                Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show();
                return;
            }
            data.clear();
            if (value.size() != 0) {
                for (QueryDocumentSnapshot i: value) {
                    Cates x = i.toObject(Cates.class);
                    data.add(x);
                }
            }
            adapter.setData(data, false);
        });
//        db.collection(collection).whereEqualTo("uniqueID", user.getUid()).get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
//                        Cates x = i.toObject(Cates.class);
//                        data.add(x);
//                    }
//                    progressDialog.dismiss();
//                    adapter.setData(data, false);
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Get data failed!", Toast.LENGTH_LONG).show();
//                    }
//                });
    }
}