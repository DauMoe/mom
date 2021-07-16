package com.example.mom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mom.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FloatingActionButton scanQR;
    private MaterialToolbar sidebar_menu;
    private DrawerLayout sidebar;
    private NavigationView navagationview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_main);
        scanQR          = binding.fabScanqr;
        sidebar_menu    = binding.sidebarMenu;
        sidebar         = binding.sidebar;
        navagationview  = binding.navagationview;
    }

    @Override
    protected void onStart() {
        super.onStart();

        sidebar_menu.setNavigationOnClickListener(v -> sidebar.open());

        navagationview.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            sidebar.close();
            //Start intent
            switch (item.getItemId()) {
                case R.id.personal_history:
                    break;
                case R.id.group_history:
                    break;
                case R.id.manager_group:
                    break;
            }
            return true;
        });

        scanQR.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setPrompt("Tip: vol up/donw to turn on/off flash!");
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(Capture.class);
            intentIntegrator.initiateScan();
        });
    }

    //For QRCode Scan
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