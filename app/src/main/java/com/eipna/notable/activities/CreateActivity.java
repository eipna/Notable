package com.eipna.notable.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.eipna.notable.AppDatabase;
import com.eipna.notable.constants.DateTimePattern;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityCreateBinding;
import com.eipna.notable.utils.DateUtil;

import java.util.Objects;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appDatabase = new AppDatabase(CreateActivity.this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set focus on note field on load
        binding.noteInput.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(binding.noteInput, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            createNote();
        }

        return true;
    }

    public void createNote() {
        String title = Objects.requireNonNull(binding.titleInput.getText()).toString();
        String content = Objects.requireNonNull(binding.noteInput.getText()).toString();

        // Sets title as empty title placeholder if field is blank
        if (title.isEmpty()) {
            title = String.format("Note %s", DateUtil.getDateString(DateTimePattern.MONTH_DAY_YEAR, DateUtil.getCurrentTime()));
        }

        // Sets note as empty note placeholder if field is blank
        if (content.isEmpty()) {
            content = "Empty content.";
        }

        NoteModel newNote = new NoteModel();
        newNote.setNoteTitle(title);
        newNote.setNoteContent(content);

        appDatabase.createNote(newNote);
        closeActivity();
    }

    public void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}