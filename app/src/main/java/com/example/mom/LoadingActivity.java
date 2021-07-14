package com.example.mom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.mom.databinding.ActivityLoadingBinding;
import com.google.firebase.auth.FirebaseUser;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class LoadingActivity extends AppCompatActivity {
    private ActivityLoadingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding            = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        Animation blink    = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        //Start blink logo
        binding.loadingLogo.startAnimation(blink);

        //Check user login yet?
        checkLoginState();
    }

    private void checkLoginState() {
        FirebaseUser auth = getInstance().getCurrentUser();
        //Start new thread to check login state
        try {
            Thread.sleep(DefineVars.LOADING_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (auth == null) {
            startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
        }
        finish();


        /*
        ====== Doc ====
        https://material.io/components/text-fields/android#using-text-fields

        ====== Note =====
        Signout Fbase: FirebaseAuth.getInstance().signOut();
        */
    }
}