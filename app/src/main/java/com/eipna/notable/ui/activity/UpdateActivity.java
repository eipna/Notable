package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.eipna.notable.R;
import com.eipna.notable.databinding.ActivityUpdateBinding;
import com.eipna.notable.util.DateUtil;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;

    private int noteIdExtra;
    private String noteTitleExtra, noteContentExtra;
    private long noteDateCreatedExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getExtras();

        binding.titleInput.setText(noteTitleExtra);
        binding.noteInput.setText(noteContentExtra);
        binding.dateText.setText(DateUtil.getDateString(DateUtil.PATTERN_DETAILED, noteDateCreatedExtra));

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getExtras() {
        noteIdExtra = getIntent().getIntExtra("NOTE_ID", -1);
        noteTitleExtra = getIntent().getStringExtra("NOTE_TITLE");
        noteContentExtra = getIntent().getStringExtra("NOTE_CONTENT");
        noteDateCreatedExtra = getIntent().getLongExtra("NOTE_DATE_CREATED", -1);
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