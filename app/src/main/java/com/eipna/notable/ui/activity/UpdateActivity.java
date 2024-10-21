package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityUpdateBinding;
import com.eipna.notable.util.DateUtil;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;
    private Database database;

    private int noteIdExtra;
    private String noteTitleExtra, noteContentExtra;
    private long noteDateCreatedExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(UpdateActivity.this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_update, menu);
        return true;
    }

    private void updateNote() {
        if (noteIsUnchanged()) {
            finish();
        } else {
            NoteModel updatedNote = new NoteModel();
            String updateNoteTitle = Objects.requireNonNull(binding.titleInput.getText()).toString();
            String updateNotedContent = Objects.requireNonNull(binding.noteInput.getText()).toString();

            updatedNote.setNoteId(noteIdExtra);
            updatedNote.setNoteTitle(updateNoteTitle);
            updatedNote.setNoteContent(updateNotedContent);
            database.updateNote(updatedNote);
            closeActivity();
        }
    }

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean noteIsUnchanged() {
        String currentNoteTitle = Objects.requireNonNull(binding.titleInput.getText()).toString();
        String currentNoteContent = Objects.requireNonNull(binding.noteInput.getText()).toString();
        return currentNoteTitle.equals(noteTitleExtra) && currentNoteContent.equals(noteContentExtra);
    }
}