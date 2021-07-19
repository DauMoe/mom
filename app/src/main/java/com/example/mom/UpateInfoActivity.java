package com.example.mom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.example.mom.databinding.ActivityUpateInfoBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.zip.Inflater;

public class UpateInfoActivity extends AppCompatActivity {
    private ActivityUpateInfoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding                             = DataBindingUtil.setContentView(this, R.layout.activity_upate_info);
        String[] list_sex                   = {"Male", "Female", "Other"};
        ArrayAdapter adapter                = new ArrayAdapter(this, R.layout.list_item, list_sex);
        MaterialDatePicker.Builder builder  = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select dob:");
        MaterialDatePicker picker           = builder.build();
        binding.sexItem.setAdapter(adapter);
        binding.infoDob.setOnClickListener(v -> {
            picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });
        picker.addOnPositiveButtonClickListener(selection -> binding.infoDob.setText(picker.getHeaderText()));
    }
}