package com.eipna.notable.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.eipna.notable.AppDatabase;
import com.eipna.notable.enums.DateTimePattern;
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
        binding.contentInput.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(binding.contentInput, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            createNote();
        }

        return true;
    }

    public void createNote() {
        String titleInField = Objects.requireNonNull(binding.titleInput.getText()).toString();
        String contentInField = Objects.requireNonNull(binding.contentInput.getText()).toString();

        String noteTitle = (titleInField.isEmpty()) ? NoteModel.EMPTY_TITLE : titleInField;
        String noteContent = (contentInField.isEmpty()) ? NoteModel.EMPTY_CONTENT : contentInField;

        NoteModel createdNote = new NoteModel();
        createdNote.setNoteTitle(noteTitle);
        createdNote.setNoteContent(noteContent);
        appDatabase.createNote(createdNote);

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