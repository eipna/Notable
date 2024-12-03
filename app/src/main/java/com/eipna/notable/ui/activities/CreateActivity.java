package com.eipna.notable.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.eipna.notable.Database;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityCreateBinding;

import java.util.Objects;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(CreateActivity.this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Checks for shared texts from other applications
        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if (getIntent().getType().equals("text/plain")) {
                handleSharedTexts(getIntent());
            }
        }

        // Set focus on note field on load
        binding.contentInput.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(binding.contentInput, InputMethodManager.SHOW_IMPLICIT);
    }

    private void handleSharedTexts(Intent intent) {
        String receivedSharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (receivedSharedText != null) {
            binding.contentInput.setText(receivedSharedText);
        }
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
        createdNote.setTitle(noteTitle);
        createdNote.setContent(noteContent);
        database.createNote(createdNote);

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