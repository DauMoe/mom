package com.example.mom.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.mom.R;
import com.example.mom.databinding.ActivityPhoneLoginBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;
    private TextInputLayout phoneNum;
    private AppCompatButton sendOTP;

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
                }

            }
        });
    }
}