package com.example.mom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityChangePinBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

import static com.example.mom.DefineVars.USERS;

public class Change_pinActivity extends AppCompatActivity {
    private ActivityChangePinBinding binding;
    private OtpTextView otpTextView;
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    ProgressDialog progressDialog;
    HashMap<String, Object> updateData = new HashMap<>();
    protected QueryDocumentSnapshot g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding             = DataBindingUtil.setContentView(this, R.layout.activity_change_pin);
        otpTextView         = findViewById(R.id.otp_view);
        progressDialog      = new ProgressDialog(this);
        user                = FirebaseAuth.getInstance().getCurrentUser();
        db                  = FirebaseFirestore.getInstance();
        binding.authenPin.setVisibility(View.VISIBLE);
        binding.changePin.setVisibility(View.GONE);
        binding.pinDesc.setText("Your default PIN is provided in app");
    }

    @Override
    protected void onStart() {
        super.onStart();
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                binding.authenPin.setOnClickListener(v -> {
                    progressDialog.setMessage("Wait!");
                    progressDialog.show();
                    db.collection(USERS)
                        .whereEqualTo("uniqueID", user.getUid())
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot i: task.getResult()) {
                                    g = i;
                                    User x = i.toObject(User.class);
                                    if (x.getPIN().equals(otp)) {
                                        otpTextView.setOTP(null);
                                        binding.pinDesc.setText("Pin is 6 digits");
                                        binding.authenPin.setVisibility(View.GONE);
                                        binding.changePin.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Your PIN is wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Fetch firestore failed!", Toast.LENGTH_LONG).show();
                            }
                        });
                });

                binding.changePin.setOnClickListener(v -> {
                    updateData.clear();
                    progressDialog.setMessage("Updating ...");
                    progressDialog.show();
                    updateData.put("pin", otp);
                    db.collection(USERS).document(g.getId()).update(updateData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Update successful!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Change_pinActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_LONG).show();
                            }
                        });
                });
            }
        });
    }
}