package com.example.mom.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.mom.R;
import com.example.mom.databinding.ActivityPhoneLoginBinding;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);
    }
}