package com.example.mom.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.mom.MainActivity;
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
//import com.google.i18n.phonenumbers.NumberParseException;
//import com.google.i18n.phonenumbers.PhoneNumberUtil;
//import com.google.i18n.phonenumbers.Phonenumber;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;
    private TextInputLayout phoneNum, otp;
    private AppCompatButton sendOTP, verify;
    private LinearLayout getPhoneNumber, getOTP;
    private TextView resend;
    private ProgressDialog progressDialog;
//    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
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
        otp             = binding.otp;
        progressDialog  = new ProgressDialog(this);
        getPhoneNumber.setVisibility(View.VISIBLE);
        getOTP.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                SigninWithCredential(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Verify failed!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                forceToken = forceResendingToken;
                verifyID   = s;
                getPhoneNumber.setVisibility(View.GONE);
                getOTP.setVisibility(View.VISIBLE);
                progressDialog.hide();
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
                    progressDialog.setMessage("Sending OTP...");
                    progressDialog.show();
                    phoneNum.getEditText().setText("+84"+phone_number.substring(1));
                    CredentialPhoneNumber("+84"+phone_number.substring(1));
//                    try {
                        //Country code denpend on your device language
                        //String countryCode = getResources().getConfiguration().locale.getCountry();
//                        Phonenumber.PhoneNumber temp = phoneUtil.parse(phone_number, "VN");
//                        phone_number = phoneUtil.format(temp, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
//                        phoneNum.getEditText().setText(phone_number);
//                        CredentialPhoneNumber(phone_number);
//                    } catch (NumberParseException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
        
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Resending OTP...");
                progressDialog.show();
                resendOTP(phoneNum.getEditText().getText().toString());
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP = otp.getEditText().getText().toString();
                if (OTP.isEmpty()) {
                    otp.setError(getString(R.string.otp_err));
                    return;
                } else {
                    otp.setError(null);
                    progressDialog.setMessage("Verifying...");
                    progressDialog.show();
                    VerifyOTP(OTP);
                }
            }
        });
    }

    private void VerifyOTP(String otpcode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyID, otpcode);
        SigninWithCredential(credential);
    }

    private void SigninWithCredential(PhoneAuthCredential phoneAuthCredential) {
        progressDialog.hide();
        auth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String phone = auth.getCurrentUser().getPhoneNumber();
                        Log.i("RESULT: ", "Login success!");
                        startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR: ", "Login failed!");
                        Toast.makeText(getApplicationContext(), "Login failed! Try again", Toast.LENGTH_LONG).show();
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