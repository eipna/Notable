package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            createNote();
        }

        return true;
    }

    public void createNote() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}