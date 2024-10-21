package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.eipna.notable.R;
import com.eipna.notable.databinding.ActivityUpdateBinding;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            updateNote();
        }

        return true;
    }

    private void updateNote() {
        closeActivity();
    }

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}