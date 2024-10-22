package com.eipna.notable.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.eipna.notable.R;
import com.eipna.notable.databinding.ActivitySettingsBinding;
import com.eipna.notable.ui.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsContainer, new SettingsFragment())
                .commit();
    }
}