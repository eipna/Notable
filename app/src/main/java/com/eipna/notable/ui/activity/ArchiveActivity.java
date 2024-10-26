package com.eipna.notable.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.eipna.notable.R;
import com.eipna.notable.databinding.ActivityArchiveBinding;

public class ArchiveActivity extends AppCompatActivity {

    private ActivityArchiveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}