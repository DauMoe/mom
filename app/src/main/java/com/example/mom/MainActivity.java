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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.ImmutableList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.mom.DefineVars.DETAIL_PAYMENTS;
import static com.example.mom.DefineVars.GROUP_USERS;
import static com.example.mom.DefineVars.MOM_BILL;
import static com.example.mom.DefineVars.PAYMENT_EVENTS;
import static com.example.mom.DefineVars.PAYMENT_INFO;
import static com.example.mom.DefineVars.USERS;
import static com.example.mom.DefineVars.listMonth;

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
    String displayName, displayEmail, userID;
    Uri avaUrl;
    TextView dpEmail, dpUser;
    ProgressDialog progressDialog;
    ImageView ava;
    Gson gson = new Gson();
    ExchangeAdapter adapter;
    GroupUserAdapter groupUseradapter;
    List<User> users = new ArrayList<>();
    View add, swap;
    String grID;
    HashMap<String, Object> updateData = new HashMap<>();
    LineChart lineChart;
    boolean fab_clicked = false;
    int CurrentViewMode = 1;

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
        DrawLineChart();
//        GetInvoiceData();

        //Check User existed
        CheckUserExisted();
        initAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Open sidebar when onclick sidebar icon
        sidebar_menu.setNavigationOnClickListener(v -> sidebar.open());
        add.setOnClickListener(v -> AddUsertoGroup());
        swap.setOnClickListener(v -> {
            if (CurrentViewMode == 1) {
                GetInvoiceData();
            }
            else {
                DrawLineChart();
            }
        });
        mainFAB.setOnClickListener(v -> {
            fab_clicked = !fab_clicked;
            System.out.println("Clicked: "+fab_clicked);
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
                    GetInvoiceData();
                    break;
                case R.id.group_history:
                    item.setChecked(true);
                    GroupUserView();
                    break;
                case R.id.create_group:
                    item.setChecked(true);
                    CreateGroup();
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
                    .setPositiveButton("Remove", (dialog, which) -> db.collection(GROUP_USERS).document(grID)
                            .update("members", FieldValue.arrayRemove(x.getUniqueID()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    groupUseradapter.removeData(position);
                                    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Delete failed!", Toast.LENGTH_LONG).show();
                                }
                            })).show();
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
        signout.setOnClickListener(v -> SignOut());
    }

    private void ChangeMode(int ViewMode, int size) {
        CurrentViewMode = ViewMode;
        if (size == 0 && ViewMode != 1) {
            binding.emptyInvoice.setVisibility(View.VISIBLE);
            binding.emptyInvoice.setText((ViewMode == 2) ? "No exchange!" : "No user in group!");
        }
        if (ViewMode == 1) {
            //Chart mode
            swap.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            binding.emptyInvoice.setVisibility(View.GONE);
            binding.exchangeRcv.setVisibility(View.GONE);
        }

        if (ViewMode == 2) {
            //Personal mode
            swap.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            binding.exchangeRcv.setVisibility(View.VISIBLE);
            binding.chart.setVisibility(View.GONE);
        }

        if (ViewMode == 3) {
            //Group mode
            swap.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
            binding.chart.setVisibility(View.GONE);
        }
    }

    private void initAnimation() {
        rotate_out_fab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_out_fab);
        rotate_in_fab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_in_fab);
        to_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_to_bottom);
        to_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_to_top);
    }

    private void DrawLineChart() {
        ChangeMode(1, 1);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        List<String> xAxisValues = new ArrayList<>(Arrays.asList("Jan", "Feb", "March", "April", "May", "June","July", "August", "September", "October", "November", "Decemeber"));
        List<Entry> incomeEntries = getIncomeEntries();
        List<Entry> incomeEntries2 = getIncomeEntries2();
        dataSets = new ArrayList<>();
        LineDataSet set1;
        LineDataSet set2;

        set1 = new LineDataSet(incomeEntries, "Income");
        set1.setColor(Color.rgb(65, 168, 121));
        set1.setValueTextColor(Color.rgb(55, 70, 73));
        set1.setValueTextSize(10f);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(set1);

        set2 = new LineDataSet(incomeEntries2, "Outcome");
        set2.setColor(Color.rgb(163, 122, 118));
        set2.setValueTextColor(Color.rgb(55, 70, 73));
        set2.setValueTextSize(10f);
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(set2);

            //customization
//        lineChart.setTouchEnabled(true);
//        lineChart.setDragEnabled(true);
//        lineChart.setScaleEnabled(false);
//        lineChart.setPinchZoom(false);
//        lineChart.setDrawGridBackground(false);
//        lineChart.setExtraLeftOffset(15);
//        lineChart.setExtraRightOffset(15);

            //to hide background lines
//        lineChart.getXAxis().setDrawGridLines(false);
//        lineChart.getAxisLeft().setDrawGridLines(false);
//        lineChart.getAxisRight().setDrawGridLines(false);

            //to hide right Y and top X border
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setEnabled(false);
        XAxis topXAxis = lineChart.getXAxis();
        topXAxis.setEnabled(false);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        set1.setLineWidth(3f);
        set2.setLineWidth(3f);
//        set1.setCircleRadius(3f);
//        set1.setDrawValues(false);

        //String setter in x-Axis
        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.animateX(2000);
        lineChart.invalidate();
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
    }

    private List<Entry> getIncomeEntries() {
        ArrayList<Entry> incomeEntries = new ArrayList<>();

        incomeEntries.add(new Entry(1, 123));
        incomeEntries.add(new Entry(2, 5433));
        incomeEntries.add(new Entry(3, 2414));
        incomeEntries.add(new Entry(4, 572));
        incomeEntries.add(new Entry(5, 1832));
        incomeEntries.add(new Entry(6, 832));
        incomeEntries.add(new Entry(7, 721));
        incomeEntries.add(new Entry(8, 973));
        incomeEntries.add(new Entry(9, 5123));
        incomeEntries.add(new Entry(10, 1258));
        incomeEntries.add(new Entry(11, 4355));
        incomeEntries.add(new Entry(12, 2417));
        return incomeEntries.subList(0, 12);
    }

    private List<Entry> getIncomeEntries2() {
        ArrayList<Entry> incomeEntries = new ArrayList<>();

        incomeEntries.add(new Entry(1, 11400));
        incomeEntries.add(new Entry(2, 1490));
        incomeEntries.add(new Entry(3, 1290));
        incomeEntries.add(new Entry(4, 7300));
        incomeEntries.add(new Entry(5, 4890));
        incomeEntries.add(new Entry(6, 4600));
        incomeEntries.add(new Entry(7, 8100));
        incomeEntries.add(new Entry(8, 7134));
        incomeEntries.add(new Entry(9, 4407));
        incomeEntries.add(new Entry(10, 8862));
        incomeEntries.add(new Entry(11, 4455));
        incomeEntries.add(new Entry(12, 6100));
        return incomeEntries.subList(0, 12);
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
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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
                    }
                });
    }

    private void CreateGroup() {
        FragmentManager fm              = getSupportFragmentManager();
        CreateGroupDialog customDialog  = new CreateGroupDialog();
        customDialog.show(fm, "");
    }

    private void AddUsertoGroup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.password_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextInputLayout user_id = v.findViewById(R.id.pin_authen);
        builder.setView(v);
        builder.setCancelable(true)
            .setPositiveButton("Add", (dialog, which) ->
                db.collection(USERS).whereEqualTo("uniqueID", user_id.getEditText().getText().toString())
                    .limit(1).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.size() == 0) {
                            Toast.makeText(getApplicationContext(), "User is not exited!", Toast.LENGTH_LONG).show();
                        } else {
                            //Check if user is in another gr yet?
                            db.collection(GROUP_USERS).whereArrayContains("members", user_id.getEditText().getText().toString()).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.size()>0) {
                                                Toast.makeText(getApplicationContext(), "This user is in another group", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            //If user didn't in another group
                                            db.collection(GROUP_USERS).document(grID)
                                                .update("members", FieldValue.arrayUnion(user_id.getEditText().getText().toString()))
                                                .addOnSuccessListener(aVoid -> {
                                                    GroupUserView();
                                                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Add failed!", Toast.LENGTH_LONG).show());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Check failed!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Fail to get user", Toast.LENGTH_LONG).show()))
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
        db.collection(GROUP_USERS)
            .whereArrayContains("members", userID)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.size() == 0) {
                    add.setVisibility(View.GONE);
                    ChangeMode(3, 0);
                    progressDialog.dismiss();
                    return;
                }
                for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                    grID            = i.getId();
                    GroupUsers x    = i.toObject(GroupUsers.class);
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
                                        ChangeMode(3, users.size());
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
            });
    }

    private void GetInvoiceData() {
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