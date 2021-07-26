package com.example.mom;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mom.Adapter.CreateGroupDialog;
import com.example.mom.Adapter.ExchangeAdapter;
import com.example.mom.Adapter.GroupUserAdapter;
import com.example.mom.Login.AccLoginActivity;
import com.example.mom.Module.Events;
import com.example.mom.Module.GroupUsers;
import com.example.mom.Module.Invoice;
import com.example.mom.Module.User;
import com.example.mom.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.ImmutableList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static com.example.mom.DefineVars.*;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {
    private ActivityMainBinding binding;
    private FloatingActionButton scanQR, handWriterBill, mainFAB;
    private MaterialToolbar sidebar_menu;
    private MaterialButton signout;
    private DrawerLayout sidebar;
    private NavigationView navagationview;
    private List<Events> data = new ArrayList<>();
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    String displayName, displayEmail, userID, preDateState;
    Uri avaUrl;
    TextView dpEmail, dpUser;
    ChipGroup filter_time;
    ProgressDialog progressDialog;
    ImageView ava;
    Gson gson = new Gson();
    ExchangeAdapter adapter;
    GroupUserAdapter groupUseradapter;
    List<User> users = new ArrayList<>();
    View add, swap;
    HashMap<String, Object> updateData = new HashMap<>();
    LineChart lineChart;
    boolean fab_clicked = false;
    int CurrentViewMode = 1;
    List<Entry> earnings= new ArrayList<>();
    List<Entry> consuming = new ArrayList<>();
    List<String> dateData = new ArrayList<>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineDataSet set1, set2;
    long EarningsAmountTemp, ConsumingAmountTemp;

    //Animation
    public static Animation rotate_out_fab, rotate_in_fab, to_bottom, to_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                     = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainFAB                     = binding.mainFab;
        handWriterBill              = binding.fabHandwriterbill;
        scanQR                      = binding.fabScanqr;
        sidebar_menu                = binding.sidebarMenu;
        sidebar                     = binding.sidebar;
        navagationview              = binding.navagationview;
        signout                     = binding.signout;
        filter_time                 = binding.filterTime;
        user                        = FirebaseAuth.getInstance().getCurrentUser();
        db                          = FirebaseFirestore.getInstance();
        userID                      = user.getUid();
        avaUrl                      = user.getPhotoUrl();
        displayName                 = user.getDisplayName();
        displayEmail                = user.getEmail();
        groupUseradapter            = new GroupUserAdapter(getApplicationContext());
        progressDialog              = new ProgressDialog(this);
        add                         = findViewById(R.id.add_gr);
        swap                        = findViewById(R.id.swap_view);
        lineChart                   = binding.chart;
        sidebar.setDrawerListener(this);
        binding.groupUser.setAdapter(groupUseradapter);

        //Recycleview init
        adapter                     = new ExchangeAdapter(getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        binding.exchangeRcv.setLayoutManager(manager);
        binding.exchangeRcv.setAdapter(adapter);

        //Init Data
        Calendar h  = Calendar.getInstance();
        currentTime = h.getTimeInMillis();
        h.add(Calendar.MONTH, -1);
        pastTime    = h.getTime().getTime();
        DrawLineChart(pastTime, currentTime);

        //Check User existed
        CheckUserExisted();
        initAnimation();
    }

    Long currentTime, pastTime;

    @Override
    protected void onStart() {
        super.onStart();
        //Open sidebar when onclick sidebar icon
        handWriterBill.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddInvoiceActivity.class)));
        sidebar_menu.setNavigationOnClickListener(v -> sidebar.open());
        signout.setOnClickListener(v -> SignOut());
        add.setOnClickListener(v -> AddUsertoGroup());
        swap.setOnClickListener(v -> {
            if (CurrentViewMode == 1) {
                GetInvoiceData(pastTime, currentTime);
            } else {
                DrawLineChart(pastTime, currentTime);
            }
        });
        filter_time.setOnCheckedChangeListener((group, checkedId) -> {
            Calendar c  = Calendar.getInstance();
            currentTime = c.getTimeInMillis();
            switch (checkedId) {
                case R.id.filter_onemonth:
                    c.add(Calendar.MONTH, -1);
                    pastTime = c.getTime().getTime();
                    break;
                case R.id.filter_twomonth:
                    c.add(Calendar.MONTH, -2);
                    pastTime = c.getTime().getTime();
                    break;
                case R.id.filter_oneyear:
                    c.add(Calendar.MONTH, -12);
                    pastTime = c.getTime().getTime();
                    break;
                case R.id.filter_twoyear:
                    c.add(Calendar.MONTH, -24);
                    pastTime = c.getTime().getTime();
                    break;
            }
            if (CurrentViewMode == 1) DrawLineChart(pastTime, currentTime);
            if (CurrentViewMode == 2 || CurrentViewMode == 3) GetInvoiceData(pastTime, currentTime);
        });
        mainFAB.setOnClickListener(v -> {
            fab_clicked = !fab_clicked;
            if (fab_clicked) {
                v.startAnimation(rotate_out_fab);
                handWriterBill.startAnimation(to_top);
                scanQR.startAnimation(to_top);
                handWriterBill.setVisibility(View.VISIBLE);
                scanQR.setVisibility(View.VISIBLE);
            } else {
                v.startAnimation(rotate_in_fab);
                handWriterBill.startAnimation(to_bottom);
                scanQR.startAnimation(to_bottom);
                handWriterBill.setVisibility(View.GONE);
                scanQR.setVisibility(View.GONE);
            }
        });

        //Set onclick event in menu sidebar
        navagationview.setNavigationItemSelectedListener(item -> {
            sidebar.close();
            //Start intent
            switch (item.getItemId()) {
                case R.id.add_money:
                    item.setChecked(true);
                    startActivity(new Intent(MainActivity.this, AddMoneyActivity.class));
                    break;
                case R.id.exchange_history:
                    item.setChecked(true);
                    GetInvoiceData(pastTime, currentTime);
                    break;
                case R.id.group_history:
                    item.setChecked(true);
                    GroupUserView();
                    break;
                case R.id.change_pins:
                    item.setChecked(true);
                    startActivity(new Intent(MainActivity.this, Change_pinActivity.class));
                    break;
                case R.id.info:
                    item.setChecked(true);
                    startActivity(new Intent(MainActivity.this, UpdateInfoActivity.class));
                    break;
                case R.id.share:
                    CopyUserID();
                    break;
            }
            return true;
        });

        binding.groupUser.setOnItemClickListener((parent, view, position, id) -> {
            User x = users.get(position);
            Intent DetailUserPaymentEvents = new Intent(MainActivity.this, DetailUserPaymentEventsActivity.class);
            DetailUserPaymentEvents.putExtra(DETAIL_PAYMENTS, x);
            startActivity(DetailUserPaymentEvents);
        });

        binding.groupUser.setOnItemLongClickListener((parent, view, position, id) -> {
            User x = users.get(position);
            new MaterialAlertDialogBuilder(MainActivity.this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setTitle("Delete")
                    .setMessage("Remove "+x.getEmail()+"?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Remove", (dialog, which) -> {
                        db.collection(GROUP_USERS).whereEqualTo("host", userID).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                                    db.collection(GROUP_USERS).document(i.getId())
                                            .update("members", FieldValue.arrayRemove(x.getUniqueID()))
                                            .addOnSuccessListener(aVoid -> {
                                                groupUseradapter.removeData(position);
                                                Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Delete failed!", Toast.LENGTH_LONG).show());
                                }
                            }
                        });
                    }).show();
            return true;
        });

        //Set onclick FAB events
        scanQR.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setPrompt("Tip: Vol up/down to turn on/off flash!");
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(Capture.class);
            intentIntegrator.initiateScan();
        });


    }

    private void ChangeMode(int ViewMode, int size) {
        CurrentViewMode = ViewMode;
        if (size == 0 && ViewMode != 1) {
            binding.emptyInvoice.setVisibility(View.VISIBLE);
            binding.emptyInvoice.setText((ViewMode == 2) ? "No exchange!" : "No user in group!");
        } else {
            if (ViewMode == 1) {
                //Chart mode
                swap.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                binding.emptyInvoice.setVisibility(View.GONE);
                binding.exchangeRcv.setVisibility(View.GONE);
                binding.chart.setVisibility(View.VISIBLE);
                binding.groupUser.setVisibility(View.GONE);
            }

            if (ViewMode == 2) {
                //Personal mode
                swap.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                binding.emptyInvoice.setVisibility(View.GONE);
                binding.exchangeRcv.setVisibility(View.VISIBLE);
                binding.chart.setVisibility(View.GONE);
                binding.groupUser.setVisibility(View.GONE);
            }

            if (ViewMode == 3) {
                //Group mode
                swap.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                binding.chart.setVisibility(View.GONE);
                binding.emptyInvoice.setVisibility(View.GONE);
                binding.exchangeRcv.setVisibility(View.GONE);
                binding.groupUser.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initAnimation() {
        rotate_out_fab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_out_fab);
        rotate_in_fab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_in_fab);
        to_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_to_bottom);
        to_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_to_top);
    }

    private void DrawLineChart(Long from, Long to) {
        ChangeMode(1, 1);
        progressDialog.setMessage("Getting data....");
        progressDialog.show();
        db.collection(USERS).whereEqualTo("uniqueID", userID).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                    User f = i.toObject(User.class);
                    sidebar_menu.setTitle((f.getAmount() + " "+ f.getUnit()));
                }
            }
        });
        db.collection(PAYMENT_EVENTS).whereEqualTo("uniqueID", userID)
                .whereGreaterThan("time", from)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        earnings.clear();
                        consuming.clear();
                        dateData.clear();
                        data.clear();
                        dataSets.clear();
                        for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                            Events c = i.toObject(Events.class);
                            data.add(c);
                        }
                        if (data.size() == 0) return;
                        Collections.sort(data); // from least to greatest
                        preDateState = DateFormatter(data.get(0).getTime());
                        dateData.add(DateFormatter(data.get(0).getTime()));
                        for (Events i: data) {
                            //Get unique date array
                            if (!DateFormatter(i.getTime()).equals(preDateState)) {
                                preDateState = DateFormatter(i.getTime());
                                dateData.add(DateFormatter(i.getTime()));
                            }
                        }
                        EarningsAmountTemp = 0L;
                        ConsumingAmountTemp = 0L;
                        preDateState = DateFormatter(data.get(0).getTime());
                        for (Events i: data) {
                            if (DateFormatter(i.getTime()).equals(preDateState)){
                                if (i.isEarning()) EarningsAmountTemp += i.getAmount();
                                else ConsumingAmountTemp += i.getAmount();
                            } else {
                                if (EarningsAmountTemp > 0) {
                                    earnings.add(new Entry((int)dateData.indexOf(preDateState), EarningsAmountTemp));
                                }
                                if (ConsumingAmountTemp > 0) {
                                    consuming.add(new Entry((int)dateData.indexOf(preDateState), ConsumingAmountTemp));
                                }
                                if (i.isEarning()) {
                                    EarningsAmountTemp = i.getAmount();
                                    ConsumingAmountTemp = 0L;
                                }
                                else {
                                    ConsumingAmountTemp = i.getAmount();
                                    EarningsAmountTemp = 0L;
                                }
                                preDateState = DateFormatter(i.getTime());
                            }
                        }
                        earnings.add(new Entry(dateData.indexOf(preDateState), EarningsAmountTemp));
                        consuming.add(new Entry(dateData.indexOf(preDateState), ConsumingAmountTemp));

                        set1 = new LineDataSet(earnings, "Earnings");
                        set1.setColor(Color.rgb(31, 236, 180));
                        set1.setValueTextColor(Color.rgb(7, 169, 125));
                        set1.setValueTextSize(10f);
                        set1.setMode(LineDataSet.Mode.LINEAR);
                        dataSets.add(set1);

                        set2 = new LineDataSet(consuming, "Consuming");
                        set2.setColor(Color.rgb(236, 68, 31));
                        set2.setValueTextColor(Color.rgb(160, 5, 10));
                        set2.setValueTextSize(10f);
                        set2.setMode(LineDataSet.Mode.LINEAR);
                        dataSets.add(set2);

                        YAxis rightYAxis = lineChart.getAxisRight();
                        rightYAxis.setEnabled(false);
                        YAxis leftYAxis = lineChart.getAxisLeft();
                        leftYAxis.setEnabled(false);
                        XAxis topXAxis = lineChart.getXAxis();
                        topXAxis.setEnabled(false);

                        XAxis xAxis = lineChart.getXAxis();
//                        xAxis.setGranularity(1f);
//                        xAxis.setCenterAxisLabels(true);
                        xAxis.setEnabled(true);
//                        xAxis.setDrawGridLines(false);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                        set1.setLineWidth(2f);
                        set2.setLineWidth(2f);
//                      set1.setCircleRadius(3f);
//                      set1.setDrawValues(false);

                        //String setter in x-Axis
                        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dateData));

                        LineData data = new LineData(dataSets);
                        lineChart.setData(data);
                        lineChart.animateX(2000);
                        lineChart.invalidate();
                        lineChart.getLegend().setEnabled(true);
                        lineChart.getDescription().setText("Date");
                    }
                });
    }

    private void CheckUserExisted() {
        updateData.clear();
        updateData.put("unit", "VND");
        updateData.put("uniqueID", user.getUid());
        updateData.put("PIN", "123456");
        updateData.put("amount", 0);
        if (user.getPhoneNumber() != null) {
            updateData.put("phone", user.getPhoneNumber());
        }
        if (user.getEmail() != null) {
            updateData.put("email", user.getEmail());
        }
        db.collection(USERS)
                .whereEqualTo("uniqueID", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size()==0) {
                //User isn't exist
                db.collection(USERS).document().set(updateData)
                    .addOnSuccessListener(aVoid -> {})
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Create failed!", Toast.LENGTH_LONG).show());
            }
        });

        db.collection(GROUP_USERS).whereEqualTo("host", user.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size()==0) {
                        updateData.clear();
                        updateData.put("host", user.getUid());
                        updateData.put("members", new ArrayList<>());
                        updateData.put("name", "My group");
                        db.collection(GROUP_USERS).document().set(updateData)
                            .addOnSuccessListener(v -> {})
                            .addOnFailureListener(v -> {
                                Toast.makeText(getApplicationContext(), "Create group failed!", Toast.LENGTH_LONG).show();
                            });
                    }
                });
    }

    private void AddUsertoGroup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.password_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextInputLayout user_id = v.findViewById(R.id.pin_authen);
        builder.setView(v);
        builder.setCancelable(true)
            .setPositiveButton("Add", (dialog, which) -> {
                if (user_id.getEditText().getText().toString().equals(userID)) {
                    Toast.makeText(getApplicationContext(), "You can't add yourself to this group!", Toast.LENGTH_LONG).show();
                    return;
                }
                db.collection(USERS).whereEqualTo("uniqueID", user_id.getEditText().getText().toString())
                        .limit(1).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.size() == 0) {
                                Toast.makeText(getApplicationContext(), "User is not exited!", Toast.LENGTH_LONG).show();
                            } else {
                                db.collection(GROUP_USERS).whereEqualTo("host", userID).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                                            db.collection(GROUP_USERS).document(i.getId()).update("members", FieldValue.arrayUnion(user_id.getEditText().getText().toString()))
                                                .addOnSuccessListener(aVoid -> {
                                                    GroupUserView();
                                                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Add failed!", Toast.LENGTH_LONG).show());
                                        }
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Fail to get user", Toast.LENGTH_LONG).show());
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void CopyUserID() {
        ClipboardManager clipboardManager   = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData                   = ClipData.newPlainText("UserID", userID);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), "Your ID copied!", Toast.LENGTH_LONG).show();
    }

    private void GroupUserView() {
        progressDialog.setMessage("Waiting...");
        progressDialog.show();

        db.collection(GROUP_USERS).whereEqualTo("host", userID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    users.clear();
                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                        GroupUsers x = i.toObject(GroupUsers.class);
                        if (x.getMembers().size() == 0) {
                            progressDialog.dismiss();
                            ChangeMode(3, users.size());
                            return;
                        }
                        for (String f: x.getMembers()) {
                            db.collection(USERS).whereEqualTo("uniqueID", f).limit(1).get()
                                    .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                        for (QueryDocumentSnapshot g: queryDocumentSnapshots1) {
                                            users.add(g.toObject(User.class));
                                        }
                                        groupUseradapter.setData(users);
                                        progressDialog.dismiss();
                                        ChangeMode(3, users.size());
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Get user group failed", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    private void GetInvoiceData(Long from, Long to) {
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        db.collection(USERS)
            .whereEqualTo("uniqueID", userID)
            .limit(1)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot i: task.getResult()) {
                        User x = i.toObject(User.class);
                        sidebar_menu.setTitle(x.getAmount() + " " +x.getUnit());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fetch firestore failed!", Toast.LENGTH_LONG).show();
                }
            });
        db.collection(PAYMENT_EVENTS)
            .whereEqualTo("uniqueID", userID)
            .whereGreaterThan("time", from)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    data.clear();
                    progressDialog.dismiss();
                    for (QueryDocumentSnapshot i: task.getResult()) {
                        Events x = i.toObject(Events.class);
                        data.add(x);
                    }
                    ChangeMode(2, data.size());
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
        } else {
            dpUser.setText("Hello!");
        }
        if (avaUrl != null) {
            Glide.with(this).load(avaUrl).into(ava);
        }
        if (user.getEmail() == null && user.getPhoneNumber() != null) {
            dpUser.setText("Hello!");
            dpEmail.setText(user.getPhoneNumber());
        }
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {}

    @Override
    public void onDrawerStateChanged(int newState) {}
}