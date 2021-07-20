package com.example.mom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mom.Adapter.ExchangeAdapter;
import com.example.mom.Adapter.GroupUserAdapter;
import com.example.mom.Login.AccLoginActivity;
import com.example.mom.Module.Events;
import com.example.mom.Module.GroupUsers;
import com.example.mom.Module.Invoice;
import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mom.DefineVars.*;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {
    private ActivityMainBinding binding;
    private FloatingActionButton scanQR;
    private MaterialToolbar sidebar_menu;
    private MaterialButton signout;
    private DrawerLayout sidebar;
    private NavigationView navagationview;
    private List<Events> data = new ArrayList<>();
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    String displayName, displayEmail, userID;
    Uri avaUrl;
    TextView dpEmail, dpUser;
    ProgressDialog progressDialog;
    ImageView ava;
    Gson gson = new Gson();
    ExchangeAdapter adapter;
    GroupUserAdapter groupUseradapter;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                     = DataBindingUtil.setContentView(this, R.layout.activity_main);
        scanQR                      = binding.fabScanqr;
        sidebar_menu                = binding.sidebarMenu;
        sidebar                     = binding.sidebar;
        navagationview              = binding.navagationview;
        signout                     = binding.signout;
        user                        = FirebaseAuth.getInstance().getCurrentUser();
        db                          = FirebaseFirestore.getInstance();
        userID                      = user.getUid();
        avaUrl                      = user.getPhotoUrl();
        displayName                 = user.getDisplayName();
        displayEmail                = user.getEmail();
        groupUseradapter            = new GroupUserAdapter(getApplicationContext());
        progressDialog              = new ProgressDialog(this);
        sidebar.setDrawerListener(this);
        binding.groupUser.setAdapter(groupUseradapter);

        //Recycleview init
        adapter                     = new ExchangeAdapter(getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        binding.exchangeRcv.setLayoutManager(manager);
        binding.exchangeRcv.setAdapter(adapter);
    }

    private void ChangeMode(boolean isGroupUserMode, int size) {
        if (size == 0) {
            binding.groupUser.setVisibility(View.GONE);
            binding.exchangeRcv.setVisibility(View.GONE);
            binding.emptyInvoice.setText(isGroupUserMode ? "No group!" : "No exchange!");
            binding.emptyInvoice.setVisibility(View.VISIBLE);
        } else {
            binding.emptyInvoice.setVisibility(View.GONE);
            if (isGroupUserMode) {
                binding.groupUser.setVisibility(View.VISIBLE);
                binding.exchangeRcv.setVisibility(View.GONE);
            } else {
                binding.groupUser.setVisibility(View.GONE);
                binding.exchangeRcv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection(USERS)
            .whereEqualTo("uniqueID", userID)
            .limit(1)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot i: task.getResult()) {
                        User x = i.toObject(User.class);
                        sidebar_menu.setTitle("Balance: "+x.getAmount() + " " +x.getUnit());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fetch firestore failed!", Toast.LENGTH_LONG).show();
                }
            });
        GetInvoiceData();
//        GroupUserView();
        sidebar_menu.setNavigationOnClickListener(v -> sidebar.open());
        navagationview.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            sidebar.close();
            //Start intent
            switch (item.getItemId()) {
                case R.id.add_money:
                    startActivity(new Intent(MainActivity.this, AddMoneyActivity.class));
                    break;
                case R.id.exchange_history:
                    GetInvoiceData();
                    break;
                case R.id.group_history:
                    GroupUserView();
                    break;
                case R.id.change_pins:
                    startActivity(new Intent(MainActivity.this, Change_pinActivity.class));
                    break;
                case R.id.info:
                    startActivity(new Intent(MainActivity.this, UpdateInfoActivity.class));
                    break;
            }
            return true;
        });
        scanQR.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setPrompt("Tip: Vol up/down to turn on/off flash!");
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(Capture.class);
            intentIntegrator.initiateScan();
        });
        signout.setOnClickListener(v -> SignOut());
    }

    private void GroupUserView() {
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        db.collection(GROUP_USERS)
            .whereArrayContains("members", userID)
            .limit(1)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                        GroupUsers x = i.toObject(GroupUsers.class);
                        sidebar_menu.setTitle(x.getName());
                        users.clear();
                        for (String f: x.getMembers()) {
                            db.collection(USERS).whereEqualTo("uniqueID", f).limit(1).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            progressDialog.dismiss();
                                            for (QueryDocumentSnapshot g: queryDocumentSnapshots) {
                                                users.add(g.toObject(User.class));
                                                if (users.size() == x.getMembers().size()) {
                                                    //Get all users
                                                    groupUseradapter.setData(users);
                                                }
                                                ChangeMode(true, users.size());
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Fetch firestore failed!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }
            });
    }

    private void GetInvoiceData() {
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        db.collection(PAYMENT_EVENTS)
                .whereEqualTo("uniqueID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        data.clear();
                        progressDialog.dismiss();
                        for (QueryDocumentSnapshot i: task.getResult()) {
                            Events x = i.toObject(Events.class);
                            data.add(x);
                        }
                        ChangeMode(false, data.size());
                        Collections.reverse(data); //sort by timestamp
                        adapter.setData(data);
                    } else {
                        Toast.makeText(getApplicationContext(), "Fetch firestore failed!", Toast.LENGTH_LONG).show();
                    }
                });
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
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

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
    public void onDrawerClosed(@NonNull View drawerView) {}

    @Override
    public void onDrawerStateChanged(int newState) {}
}