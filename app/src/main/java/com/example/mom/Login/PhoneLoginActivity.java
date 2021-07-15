package com.example.mom.Login;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import com.example.mom.DefineVars;
import com.example.mom.R;
import com.example.mom.databinding.ActivityPhoneLoginBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;
    private TextInputLayout phoneNum;
    private AppCompatButton sendOTP;
    protected PhoneAuthOptions options;
    protected FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);
        phoneNum    = binding.loginPhone;
        sendOTP     = binding.sendOTP;
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = phoneNum.getEditText().getText().toString();
                if (phone_number.isEmpty() || !Pattern.compile("^[+]?[0-9]{10,11}$").matcher(phone_number).matches()) {
                    phoneNum.setError(getString(R.string.phone_err));
                    return;
                } else {
                    phoneNum.setError(null);
                    options = PhoneAuthOptions
                                .newBuilder(auth)
                                .setPhoneNumber(phone_number)
                                .setTimeout(DefineVars.TIMEOUT, TimeUnit.SECONDS)
                                .setActivity(PhoneLoginActivity.this)
//                                .setCallbacks(ChangeIntent)
                                .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

            }
        });
    }
}