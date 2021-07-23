package com.example.mom.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mom.DefineVars;
import com.example.mom.MainActivity;
import com.example.mom.Module.GroupUsers;
import com.example.mom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupDialog extends DialogFragment {
    protected FirebaseFirestore db;
    protected FirebaseAuth user;
    TextInputLayout gr_name;
    MaterialButton cancel, create;
    List<String> Members = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.create_group_dialog, container,false);
        getDialog().setTitle("Create group");
        db      = FirebaseFirestore.getInstance();
        user    = FirebaseAuth.getInstance();
        gr_name = v.findViewById(R.id.gr_name);
        cancel  = v.findViewById(R.id.create_gr_cancel);
        create  = v.findViewById(R.id.create_gr_ok);

        cancel.setOnClickListener(x -> dismiss());
        create.setOnClickListener(x -> {
            db.collection(DefineVars.GROUP_USERS).whereEqualTo("host", user.getUid()).limit(1).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.size()>0) {
                            Toast.makeText(getContext(), "You are own another group!", Toast.LENGTH_LONG).show();
                            dismiss();
                            return;
                        }
                        Members.clear();
                        Members.add(user.getUid());
                        db.collection(DefineVars.GROUP_USERS).document().set(new GroupUsers(gr_name.getEditText().getText().toString(), user.getUid(), Members))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Create group successful!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    ((Activity) getContext()).finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Create group failed!", Toast.LENGTH_LONG).show());
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Fetch Firestore failed", Toast.LENGTH_LONG).show();
                        }
                    });
        });
        return v;
    }
}
