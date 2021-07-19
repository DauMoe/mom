package com.example.mom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityUpateInfoBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

import static com.example.mom.DefineVars.USERS;

public class UpdateInfoActivity extends AppCompatActivity {
    private ActivityUpateInfoBinding binding;
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    HashMap<String, Object> updateData = new HashMap<>();
    ProgressDialog progressDialog;
    QueryDocumentSnapshot g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                             = DataBindingUtil.setContentView(this, R.layout.activity_upate_info);
        user                                = FirebaseAuth.getInstance().getCurrentUser();
        db                                  = FirebaseFirestore.getInstance();
        String[] list_sex                   = {"Male", "Female", "Other"};
        ArrayAdapter adapter                = new ArrayAdapter(this, R.layout.list_item, list_sex);
        MaterialDatePicker.Builder builder  = MaterialDatePicker.Builder.datePicker();
        progressDialog                      = new ProgressDialog(this);
        builder.setTitleText("Select dob:");
        MaterialDatePicker picker           = builder.build();
        binding.genderItem.setAdapter(adapter);
        binding.infoDob.setOnClickListener(v -> picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        picker.addOnPositiveButtonClickListener(selection -> binding.infoDob.setText(picker.getHeaderText()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection(USERS)
            .whereEqualTo("uniqueID", user.getUid())
            .limit(1)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot i: task.getResult()) {
                        g = i;
                        User f = i.toObject(User.class);
                        binding.infoEmail.getEditText().setText(f.getEmail());
                        binding.infoPhone.getEditText().setText(f.getPhone());
                        binding.infoAddress.getEditText().setText(f.getAddress());
                        binding.infoDob.setText(f.getDob());
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Fetch data firestore failed!", Toast.LENGTH_LONG).show();
                }
            });
        binding.updateInfomation.setOnClickListener(v -> {
            progressDialog.setMessage("Updating...");
            progressDialog.show();
            updateData.clear();
            updateData.put("email", binding.infoEmail.getEditText().getText().toString());
            updateData.put("phone", binding.infoPhone.getEditText().getText().toString());
            updateData.put("address", binding.infoAddress.getEditText().getText().toString());
            updateData.put("gender", binding.genderItem.getText().toString());
            updateData.put("dob", binding.infoDob.getText().toString());

            db.collection(USERS).document(g.getId()).update(updateData)
                .addOnSuccessListener(aVoid -> {
                    //Payment done
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update info successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(UpdateInfoActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Fetch data firestore failed!", Toast.LENGTH_LONG).show());
        });
    }
}