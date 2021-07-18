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
import android.widget.EditText;
import android.widget.Toast;

import com.example.mom.Module.Invoice;
import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityPaymentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executor;

import static com.example.mom.DefineVars.*;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    SimpleDateFormat time_format;
    Calendar calendar;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Gson gson = new Gson();
    FirebaseFirestore db;
    FirebaseUser user;
    String amount;
    HashMap<String, Object> updateData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                 = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        time_format             = new SimpleDateFormat("kk:mm:ss");
        calendar                = Calendar.getInstance();
        executor                = ContextCompat.getMainExecutor(this);
        db                      = FirebaseFirestore.getInstance();
        user                    = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent payment_intent   = getIntent();
        Invoice payment         = (Invoice) payment_intent.getSerializableExtra(PAYMENT_INFO);
        amount                  = payment.getAmount();
        calendar.setTimeInMillis(payment.getTime());

        //Set Invoice Info
        binding.paymentBillID.setText("#"+payment.getBillID());
        binding.paymentBillId.setText(payment.getBillID());
        binding.billDuedate.setText(listMonth[calendar.get(Calendar.MONTH)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR));
        binding.billTime.setText(time_format.format(payment.getTime()));
        binding.billTotal.setText(payment.getTotal() + " items");
        binding.paymentAmount.setText(payment.getAmount()+" "+payment.getUnit());

        //Go to previous activity
        binding.invoiceIcon.setOnClickListener(v -> onBackPressed());

        //Biometric Authen Override
        biometricPrompt = new BiometricPrompt(PaymentActivity.this,
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
                .setSubtitle("You can use password instead!")
                .setNegativeButtonText("Use password")
                .build();
        binding.pay.setOnClickListener(v -> PaymentInvoice());
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
                            .setPositiveButton("Payment", new DialogInterface.OnClickListener() {
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

    private void updateAmount() {
        db.collection(USERS)
            .whereEqualTo("uniqueID", user.getUid())
            .limit(1)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot i: task.getResult()) {
                        User x = i.toObject(User.class);
                        if (Long.valueOf(x.getAmount()) >= Long.valueOf(amount)) {
                            long remain_amount = Long.valueOf(x.getAmount()) - Long.valueOf(amount);
                            updateData.clear();
                            updateData.put("amount", remain_amount);
                            db.collection(USERS).document(i.getId()).update(updateData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Payment done
                                        startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Fetch data firestore failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                        } else {
                            Toast.makeText(getApplicationContext(), "Your amount is not enough", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
    }
}