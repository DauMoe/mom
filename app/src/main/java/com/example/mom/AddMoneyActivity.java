package com.example.mom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityAddMoneyBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executor;

import static com.example.mom.DefineVars.*;

public class AddMoneyActivity extends AppCompatActivity {
    private ActivityAddMoneyBinding binding;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    FirebaseFirestore db;
    FirebaseUser user;
    HashMap<String, Object> updateData = new HashMap<>();
    Random g = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                 = DataBindingUtil.setContentView(this, R.layout.activity_add_money);
        executor                = ContextCompat.getMainExecutor(this);
        db                      = FirebaseFirestore.getInstance();
        user                    = FirebaseAuth.getInstance().getCurrentUser();

        //Biometric Authen Override
        biometricPrompt = new BiometricPrompt(AddMoneyActivity.this,
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
                .setTitle("Authentication")
                .setSubtitle("You can use password instead!")
                .setNegativeButtonText("Use password")
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.rechangeNow.setOnClickListener(v -> PaymentInvoice());
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

    private void AuthenWithPassword() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.password_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextInputLayout password = v.findViewById(R.id.pin_authen);
        builder.setView(v);
        db.collection(USERS)
                .whereEqualTo("uniqueID", user.getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot i: task.getResult()) {
                            User x = i.toObject(User.class);
                            builder.setCancelable(true)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (password.getEditText().getText().toString().equals(x.getPIN())) {
                                                updateAmount();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Password invalid", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }

    private void updateAmount() {
        db.collection(USERS)
            .whereEqualTo("uniqueID", user.getUid())
            .limit(1)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot i: task.getResult()) {
                        User x = i.toObject(User.class);
                        long g = x.getAmount() + Long.valueOf(String.valueOf(binding.rechangeAmount.getEditText().getText()));
                        updateData.clear();
                        updateData.put("amount", g);
                        db.collection(USERS).document(i.getId()).update(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updateData.clear();
                                    updateData.put("earning", true);
                                    updateData.put("time", Calendar.getInstance().getTimeInMillis());
                                    updateData.put("uniqueID", user.getUid());
                                    updateData.put("unit", "VND");
                                    updateData.put("groupID", 0);
                                    updateData.put("from", "Bank account");
                                    updateData.put("amount", Long.valueOf(String.valueOf(binding.rechangeAmount.getEditText().getText())));
                                    updateData.put("billID", GenBillID("IN"));
                                    db.collection(PAYMENT_EVENTS).document().set(updateData);
                                    //Payment done
                                    startActivity(new Intent(AddMoneyActivity.this, MainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Update amount failed!", Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                }
            });
    }
}