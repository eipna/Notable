package com.eipna.notable.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.eipna.notable.R;
import com.eipna.notable.databinding.ActivityCreateBinding;
import com.eipna.notable.util.DateUtil;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Sets date creation text to current date (Eg. Monday, October 21 2024)
        binding.dateText.setText(DateUtil.getDateString(DateUtil.PATTERN_DETAILED, DateUtil.getCurrentTime()));
    }
}