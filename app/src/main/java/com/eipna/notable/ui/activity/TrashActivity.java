package com.eipna.notable.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.eipna.notable.R;
import com.eipna.notable.databinding.ActivityTrashBinding;

public class TrashActivity extends AppCompatActivity {

    private ActivityTrashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}