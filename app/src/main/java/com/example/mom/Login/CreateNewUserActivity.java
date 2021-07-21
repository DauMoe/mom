package com.example.mom.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mom.DefineVars;
import com.example.mom.Module.User;
import com.example.mom.R;
import com.example.mom.databinding.ActivityCreateNewUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateNewUserActivity extends AppCompatActivity {
    private ActivityCreateNewUserBinding binding;
    protected FirebaseFirestore db;
    protected FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db                                  = FirebaseFirestore.getInstance();
        auth                                = FirebaseAuth.getInstance();
        progressDialog                      = new ProgressDialog(this);
        binding                             = DataBindingUtil.setContentView(this, R.layout.activity_create_new_user);
        String[] list_gender                = {"Male", "Female", "Other"};
        ArrayAdapter adapter                = new ArrayAdapter(this, R.layout.list_item, list_gender);
        binding.genderItem.setAdapter(adapter);
        MaterialDatePicker.Builder builder  = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select dob:");
        MaterialDatePicker picker           = builder.build();
        binding.createDob.setOnClickListener(v -> picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        picker.addOnPositiveButtonClickListener(selection -> binding.createDob.setText(picker.getHeaderText()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.createAcc.setOnClickListener(v -> {
            String email        = binding.createEmail.getEditText().getText().toString();
            String pass         = binding.createPass.getEditText().getText().toString();
            boolean isErr       = false;
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                isErr = true;
                binding.createEmail.setError(getString(R.string.email_err));
            } else {
                binding.createEmail.setError(null);
            }
            if (pass.isEmpty() || pass.length() < 6) {
                isErr = true;
                binding.createPass.setError(getString(R.string.pass_err));
            } else {
                binding.createPass.setError(null);
            }
            if (isErr) return;
            progressDialog.setMessage("Creating...");
            progressDialog.show();
            String add      = binding.createAddress.getEditText().getText().toString();
            String gender   = binding.genderItem.getText().toString();
            String dob      = binding.createDob.getText().toString();
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        db.collection(DefineVars.USERS).document()
                                .set(new User(
                                        "",
                                        email,
                                        "",
                                        "VND",
                                        user.getUid(),
                                        "123456",
                                        add,
                                        gender,
                                        dob,
                                        0L))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Create user successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(CreateNewUserActivity.this, AccLoginActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Insert to DB failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Create failed!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}