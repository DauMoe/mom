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
    View add;
    String grID;
    HashMap<String, Object> updateData = new HashMap<>();
    LineChart lineChart;

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
        add                         = findViewById(R.id.add_gr);
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
        GetInvoiceData();
        add.setVisibility(View.GONE);

        //Check User existed
        CheckUserExisted();
    }


    List<Entry> amountData = new ArrayList<Entry>();
    List<Entry> xxx = new ArrayList<Entry>();
    List<String> dateData = new ArrayList<String>();
    Calendar calendar = Calendar.getInstance();
    private void DrawLineChart() {


        //Desciption
//        Description desc = lineChart.getDescription();
//        desc.setEnabled(true);
//        desc.setText(user.getEmail());
//        desc.setPosition(0, 0);
        amountData.clear();
        dateData.clear();
        xxx.clear();
        db.collection(PAYMENT_EVENTS).whereEqualTo("uniqueID", user.getUid()).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int c = 0;
                    for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                        Events x = i.toObject(Events.class);
                        calendar.setTimeInMillis(x.getTime());
                        if (x.isEarning()) {
                            xxx.add(new Entry(x.getAmount(), c++));
                        } else {
                            amountData.add(new Entry(x.getAmount(), c++));
                        }
                        dateData.add(listMonth[calendar.get(Calendar.MONTH)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR));
                    }

//                    ArrayList<LineDataSet> xx = new ArrayList<LineDataSet>();
//                    LineDataSet set1 = new LineDataSet(amountData, "Consume");
//                    List<Integer> colors1 = new ArrayList<>();
//                    colors1.add(ColorTemplate.rgb("#db1414"));
//                    set1.setColors(colors1);
//                    set1.setLineWidth(2f);
//                    set1.setCircleRadius(4f);
//                    xx.add(set1);
//                    xx.add(new LineDataSet(xxx, "Eranings"));
//                    lineChart.setData(new LineData(dateData, xx));
//                    lineChart.invalidate();//refresh



                    ArrayList<Entry> dataset1 = new ArrayList<Entry>();
                    dataset1.add(new Entry(1f, 0));
                    dataset1.add(new Entry(2f, 1));
                    dataset1.add(new Entry(3f, 2));
                    dataset1.add(new Entry(4f, 3));
                    dataset1.add(new Entry(5f, 4));
                    dataset1.add(new Entry(6f, 5));
                    dataset1.add(new Entry(7f, 6));
                    ArrayList<Entry> dataset2 = new ArrayList<Entry>();
                    dataset2.add(new Entry(3f, 0));
                    dataset2.add(new Entry(4f, 2));
                    dataset2.add(new Entry(5f, 4));
                    dataset2.add(new Entry(6f, 5));
                    dataset2.add(new Entry(7f, 6));
                    dataset2.add(new Entry(8f, 7));
                    dataset2.add(new Entry(9f, 8));
                    String[] xAxis = new String[] {"0", "1", "2", "3", "4", "5", "6", "8", "9"};


                    ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet> ();

                    LineDataSet lDataSet1 = new LineDataSet(dataset1, "DataSet1");
                    lDataSet1.setColor(Color.RED);
                    lDataSet1.setCircleColor(Color.RED);
                    lines.add(lDataSet1);

                    LineDataSet lDataSet2 = new LineDataSet(dataset2, "DataSet2");
                    lines.add(lDataSet2);


                    LineData xxxxxxxx = new LineData(xAxis, lines);
                    lineChart.setData(xxxxxxxx);
                }
            });


//        lineDataSet.setColor(Color.parseColor("#000000"));
//        lineDataSet.setValueTextColor(Color.parseColor("#FFF"));
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
        //Open sidebar when onclick sidebar icon
        sidebar_menu.setNavigationOnClickListener(v -> sidebar.open());

        add.setOnClickListener(v -> AddUsertoGroup());

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
                    add.setVisibility(View.GONE);
                    GetInvoiceData();
                    break;
                case R.id.group_history:
                    item.setChecked(true);
                    add.setVisibility(View.VISIBLE);
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

        binding.groupUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
            }
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
                    ChangeMode(true, 0);
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