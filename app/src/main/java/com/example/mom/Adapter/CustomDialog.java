package com.example.mom.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mom.AddMoneyActivity;
import com.example.mom.MainActivity;
import com.example.mom.Module.User;
import com.example.mom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;

import in.aabhasjindal.otptextview.OtpTextView;

import static com.example.mom.DefineVars.GenBillID;
import static com.example.mom.DefineVars.PAYMENT_EVENTS;
import static com.example.mom.DefineVars.USERS;

public class CustomDialog extends DialogFragment {
    OtpTextView authen_pin;
    MaterialButton pin_cancel, pin_ok;
    FirebaseFirestore db;
    HashMap<String, Object> updateData = new HashMap<>();
    Long RechangeAmount = 0L;
    String userID, uniqueID;
    boolean isEarnings;
    String from, pre, note = "", cate = "";

    public CustomDialog(Long RechangeAmount, String uID, Boolean isEarnings, String cate, String note) {
        this.RechangeAmount     = RechangeAmount;
        this.uniqueID           = uID;
        this.isEarnings         = isEarnings;
        this.cate               = cate;
        this.note               = note;
    }

    public CustomDialog(Long RechangeAmount, String uID, Boolean isEarnings, String from, String prefix, String note, String cate) {
        this.RechangeAmount     = RechangeAmount;
        this.uniqueID           = uID;
        this.isEarnings         = isEarnings;
        this.from               = from;
        this.pre                = prefix;
        this.note               = note;
        this.cate               = cate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.pin_dialog, container,false);
        authen_pin  = view.findViewById(R.id.pin_input);
        pin_cancel  = view.findViewById(R.id.pin_dialog_cancel);
        pin_ok      = view.findViewById(R.id.pin_dialog_ok);
        db          = FirebaseFirestore.getInstance();
        getDialog().setTitle("Authen");

        pin_cancel.setOnClickListener(v -> dismiss());

        pin_ok.setOnClickListener(v -> db.collection(USERS).whereEqualTo("uniqueID", uniqueID)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot i: queryDocumentSnapshots) {
                    User x = i.toObject(User.class);
                    userID = i.getId();
                    if (authen_pin.getOTP().equals(x.getPIN())) {
                        updateAmount(x.getAmount());
                    } else {
                        Toast.makeText(getContext(), "Wrong PIN", Toast.LENGTH_LONG).show();
                    }
                }
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Get user failed!", Toast.LENGTH_LONG).show()));
        return view;
    }

    private void updateAmount(Long currentAmount) {
        updateData.clear();
        if (isEarnings) {
            updateEarnings(currentAmount);
        } else {
            updatePayment(currentAmount);
        }
    }

    private void updatePayment(Long currentAmount) {
//        if (currentAmount >= RechangeAmount) {
        if (true) {
            updateData.put("amount", (currentAmount-RechangeAmount));
            db.collection(USERS).document(userID).update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        updateData.clear();
                        updateData.put("earning", false);
                        updateData.put("time", Calendar.getInstance().getTimeInMillis());
                        updateData.put("uniqueID", uniqueID);
                        updateData.put("unit", "VND");
                        updateData.put("from", from);
                        updateData.put("amount", RechangeAmount);
                        updateData.put("billID", GenBillID(pre));
                        if (!note.isEmpty()) updateData.put("note", note);
                        if (!cate.isEmpty()) updateData.put("cate", cate);
                        db.collection(PAYMENT_EVENTS).document().set(updateData);
                        //Payment done
                        Toast.makeText(getContext(), "Update amount successful!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        ((Activity) getContext()).finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Update amount failed!", Toast.LENGTH_LONG).show());

        } else {
            Toast.makeText(getContext(), "Your balances is not enough", Toast.LENGTH_LONG).show();
        }
    }

    private void updateEarnings(Long currentAmount) {
        updateData.put("amount", (RechangeAmount+currentAmount));
        db.collection(USERS).document(userID).update(updateData)
                .addOnSuccessListener(aVoid -> {
                    updateData.clear();
                    updateData.put("earning", true);
                    updateData.put("time", Calendar.getInstance().getTimeInMillis());
                    updateData.put("uniqueID", uniqueID);
                    updateData.put("unit", "VND");
                    updateData.put("from", "Bank account");
                    updateData.put("amount", RechangeAmount);
                    updateData.put("billID", GenBillID("IN"));
                    if (!note.isEmpty()) updateData.put("note", note);
                    if (!cate.isEmpty()) updateData.put("cate", cate);
                    //Write to log
                    db.collection(PAYMENT_EVENTS).document().set(updateData);
                    //Payment done
                    Toast.makeText(getContext(), "Update amount successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    ((Activity) getContext()).finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update amount failed!", Toast.LENGTH_LONG).show());
    }
}
