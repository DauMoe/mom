package com.example.mom.Login;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import com.example.mom.DefineVars;
import com.example.mom.R;
import com.example.mom.databinding.ActivityPhoneLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;
    private TextInputLayout phoneNum;
    private AppCompatButton sendOTP, verify;
    private LinearLayout getPhoneNumber, getOTP;
    private TextView resend;
    private PhoneNumberUtils phoneUtil;
    protected PhoneAuthOptions options;
    protected String verifyID;
    protected PhoneAuthProvider.ForceResendingToken forceToken;
    protected PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    protected FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);
        phoneNum        = binding.loginPhone;
        sendOTP         = binding.sendOTP;
        getPhoneNumber  = binding.getPhoneNumber;
        getOTP          = binding.getOtp;
        verify          = binding.verify;
        resend          = binding.resendOtp;
    }

    @Override
    protected void onStart() {
        super.onStart();
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                SigninWithCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "Sending OTP!", Toast.LENGTH_LONG).show();
                forceToken = forceResendingToken;
                verifyID   = s;
                getPhoneNumber.setVisibility(View.GONE);
                getOTP.setVisibility(View.VISIBLE);
            }
        };

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = phoneNum.getEditText().getText().toString();
                if (phone_number.isEmpty() || !Pattern.compile("^[+]?[0-9]{10,11}$").matcher(phone_number).matches()) {
                    phoneNum.setError(getString(R.string.phone_err));
                    return;
                } else {
                    phoneNum.setError(null);
//                    phoneNum.getEditText().setText(phoneUtil.formatNumber(phone_number, "","VN"));
                    Log.i("PHONE CONVERTED: ", phoneUtil.formatNumber(phone_number, Locale.getDefault().getCountry()));
//                    CredentialPhoneNumber(phone_number);
                }
            }
        });
        
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP(phoneNum.getEditText().getText().toString());
            }
        });
    }

    private void SigninWithCredential(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String phone = auth.getCurrentUser().getPhoneNumber();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void CredentialPhoneNumber(String phone_number) {
        options = PhoneAuthOptions
                .newBuilder(auth)
                .setPhoneNumber(phone_number)
                .setTimeout(DefineVars.TIMEOUT, TimeUnit.SECONDS)
                .setActivity(PhoneLoginActivity.this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendOTP(String phone_number) {
        options = PhoneAuthOptions
                .newBuilder(auth)
                .setPhoneNumber(phone_number)
                .setTimeout(DefineVars.TIMEOUT, TimeUnit.SECONDS)
                .setActivity(PhoneLoginActivity.this)
                .setForceResendingToken(forceToken)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


}