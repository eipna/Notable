package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityCreateBinding;
import com.eipna.notable.util.DateUtil;

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
        NoteModel newNote = new NoteModel();
        String title = Objects.requireNonNull(binding.titleInput.getText()).toString();
        String content = Objects.requireNonNull(binding.noteInput.getText()).toString();

        // Error handling for title and note fields
        if (title.isEmpty()) {
            title = String.format("Note %s", DateUtil.getDateString(DateUtil.PATTERN_DD_MM_YYYY, DateUtil.getCurrentTime()));
        }

        if (content.isEmpty()) {
            content = NoteModel.EMPTY_NOTE;
        }

        newNote.setNoteTitle(title);
        newNote.setNoteContent(content);
        newNote.setNoteDateCreated(DateUtil.getCurrentTime());
        database.createNote(newNote);

        closeActivity();
    }

    public void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}