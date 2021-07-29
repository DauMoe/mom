package com.example.mom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.example.mom.Adapter.CustomDialog;
import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityAddInvoiceBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import static com.example.mom.DefineVars.GenBillID;
import static com.example.mom.DefineVars.PAYMENT_EVENTS;
import static com.example.mom.DefineVars.USERS;

public class AddInvoiceActivity extends AppCompatActivity {
    private ActivityAddInvoiceBinding binding;
    protected FirebaseFirestore db;
    protected FirebaseAuth user;
    List<String> list_cate = new ArrayList<>();
    ArrayAdapter adapter;
    HashMap<String, Object> updateData = new HashMap<>();
    ProgressDialog progressDialog;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Long amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_add_invoice);
        user            = FirebaseAuth.getInstance();
        db              = FirebaseFirestore.getInstance();
        executor        = ContextCompat.getMainExecutor(this);
        progressDialog  = new ProgressDialog(this);
        //Biometric Authen Override
        biometricPrompt = new BiometricPrompt(AddInvoiceActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //Biometric Authen error
                AuthenWithPassword();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                updateAmount();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Payment")
                .setSubtitle("You can use PIN instead!")
                .setNegativeButtonText("Use PIN")
                .build();
        list_cate.clear();
        db.collection(DefineVars.CONSUMING_CATE).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                list_cate.add(i.getString("name"));
            }
            adapter = new ArrayAdapter(this, R.layout.list_item, list_cate);
            binding.consumingCateItem.setAdapter(adapter);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.addBillBack.setOnClickListener(v -> onBackPressed());
        binding.payHandbill.setOnClickListener(v -> {
            amount = Long.valueOf(binding.addInvoice.getEditText().getText().toString());
            if (amount == 0) {
                Toast.makeText(getApplicationContext(), "Enter amount!", Toast.LENGTH_LONG).show();
                return;
            }
            PaymentInvoice();
        });
    }
    private void AuthenWithPassword() {
        FragmentManager fm          = getSupportFragmentManager();
        CustomDialog customDialog   = new CustomDialog(amount, user.getUid(), false, "User", "HA", binding.addNote.getEditText().getText().toString(), binding.consumingCateItem.getText().toString());
        customDialog.show(fm, "");
    }

    private void PaymentInvoice() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                //Device has biometric hardware => show fingerprint dialog
                biometricPrompt.authenticate(promptInfo);
                break;
            default:
                AuthenWithPassword();
        }
    }
//
    private void updateAmount() {
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        db.collection(USERS)
                .whereEqualTo("uniqueID", user.getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot i: task.getResult()) {
                            User x = i.toObject(User.class);
                            if (x.getAmount() >= amount) {
                                long remain_amount = x.getAmount() - amount;
                                updateData.clear();
                                updateData.put("amount", remain_amount);
                                db.collection(USERS).document(i.getId()).update(updateData)
                                        .addOnSuccessListener(aVoid -> {
                                            updateData.clear();
                                            updateData.put("earning", false);
                                            updateData.put("time", Calendar.getInstance().getTimeInMillis());
                                            updateData.put("uniqueID", user.getUid());
                                            updateData.put("unit", "VND");
                                            updateData.put("from", "User");
                                            updateData.put("amount", amount);
                                            updateData.put("billID", GenBillID("HA"));
                                            updateData.put("note", binding.addNote.getEditText().getText().toString());
                                            updateData.put("cate", binding.consumingCateItem.getText().toString());
                                            db.collection(PAYMENT_EVENTS).document().set(updateData);
                                            progressDialog.dismiss();
                                            //Payment done
                                            startActivity(new Intent(AddInvoiceActivity.this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Fetch data firestore failed!", Toast.LENGTH_LONG).show();
                                        });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Your amount is not enough", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

}