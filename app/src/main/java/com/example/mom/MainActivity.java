package com.example.mom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mom.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FloatingActionButton scanQR;
    private MaterialToolbar sidebar_menu;
    private DrawerLayout sidebar;
    boolean isSideBarOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_main);
        scanQR          = binding.fabScanqr;
        sidebar_menu    = binding.sidebarMenu;
        sidebar         = binding.sidebar;
    }

    @Override
    protected void onStart() {
        super.onStart();

        sidebar_menu.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSideBarOpen) {
                    sidebar.close();
                } else {
                    sidebar.open();
                }
                isSideBarOpen = !isSideBarOpen;
            }
        });
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setPrompt("Tip: vol up/donw to turn on/off flash!");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult.getContents() != null) {
            Log.w("RESULT: ", intentResult.getContents());
            Toast.makeText(getApplicationContext(), intentResult.getContents(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "FAILED!", Toast.LENGTH_LONG).show();
        }
    }
}