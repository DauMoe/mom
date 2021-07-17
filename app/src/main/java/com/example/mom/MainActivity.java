package com.example.mom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.mom.Login.AccLoginActivity;
import com.example.mom.Module.Invoice;
import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.example.mom.DefineVars.MOM_BILL;
import static com.example.mom.DefineVars.PAYMENT_INFO;
import static com.example.mom.DefineVars.USERS;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {
    private ActivityMainBinding binding;
    private FloatingActionButton scanQR;
    private MaterialToolbar sidebar_menu;
    private MaterialButton signout;
    private DrawerLayout sidebar;
    private NavigationView navagationview;
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    String displayName, displayEmail, userID;
    Uri avaUrl;
    TextView dpEmail, dpUser;
    ImageView ava;
    Gson gson = new Gson();
    boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_main);
        scanQR          = binding.fabScanqr;
        sidebar_menu    = binding.sidebarMenu;
        sidebar         = binding.sidebar;
        navagationview  = binding.navagationview;
        signout         = binding.signout;
        user            = FirebaseAuth.getInstance().getCurrentUser();
        db              = FirebaseFirestore.getInstance();
        userID          = user.getUid();
        avaUrl          = user.getPhotoUrl();
        displayName     = user.getDisplayName();
        displayEmail    = user.getEmail();
        emailVerified   = user.isEmailVerified();
        sidebar.setDrawerListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        db.collection(USERS)
                .whereEqualTo("uniqueID", user.getEmail())
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot i: task.getResult()) {
                            User x = i.toObject(User.class);
                            sidebar_menu.setTitle(x.getAmount() + " " +x.getUnit());
                        }
                    }
                });
        sidebar_menu.setNavigationOnClickListener(v -> sidebar.open());
        navagationview.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            sidebar.close();
            //Start intent
            switch (item.getItemId()) {
                case R.id.exchange_history:
                    break;
                case R.id.group_history:
                    break;
                case R.id.manager_group:
                    break;
                case R.id.change_pin:
                    break;
                case R.id.info:
                    break;
            }
            return true;
        });

        scanQR.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setPrompt("Tip: Vol up/down to turn on/off flash!");
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(Capture.class);
            intentIntegrator.initiateScan();
        });
        signout.setOnClickListener(v -> SignOut());
    }

    private void handlerQR(IntentResult intentResult) {
        try {
            //Convert String to Bill Object
            Invoice x = gson.fromJson(intentResult.getContents(), Invoice.class);
            if (x.getCompany().equals(MOM_BILL)) {
                Intent payment = new Intent(MainActivity.this, PaymentActivity.class);
                payment.putExtra(PAYMENT_INFO, x);
                startActivity(payment);
            } else {
                Toast.makeText(getApplicationContext(), "This is not MoM QR!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "This is not MoM QR!", Toast.LENGTH_LONG).show();
        }
    }

    //For QRCode Scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
//            Toast.makeText(getApplicationContext(), intentResult.getContents(), Toast.LENGTH_LONG).show();
            handlerQR(intentResult);
        } else {
            Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_LONG).show();
        }
    }

    private void SignOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, AccLoginActivity.class));
            finish();
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        dpUser      = findViewById(R.id.sidebar_username);
        dpEmail     = findViewById(R.id.sidebar_gmail);
        ava         = findViewById(R.id.user_ava);
        if (user.getEmail() != null) {
            dpEmail.setText(displayEmail);
        }
        if (user.getDisplayName() != null) {
            dpUser.setText(displayName);
        }
        if (avaUrl != null) {
            Glide.with(this).load(avaUrl).into(ava);
        }
        if (user.getEmail() == null && user.getPhoneNumber() != null) {
            dpUser.setText(user.getPhoneNumber());
            dpEmail.setText(null);
        }
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}