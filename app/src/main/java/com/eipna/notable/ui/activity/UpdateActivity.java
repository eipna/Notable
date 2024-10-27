package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

    private int noteIdExtra, noteStatusExtra;
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
        noteStatusExtra = getIntent().getIntExtra("NOTE_STATUS", -1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            updateNote();
        }

        if (item.getItemId() == R.id.options_update_archive) {
            database.alterNoteStatus(noteIdExtra, NoteModel.STATUS_ARCHIVED);
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_unarchive) {
            database.alterNoteStatus(noteIdExtra, NoteModel.STATUS_DEFAULT);
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_Trash) {
            database.alterNoteStatus(noteIdExtra, NoteModel.STATUS_DELETED);
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_restore) {
            database.alterNoteStatus(noteIdExtra, NoteModel.STATUS_DEFAULT);
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_delete) {
            showDeleteDialog();
        }

        if (item.getItemId() == R.id.options_update_share) {
            showShareIntent();
        }
        return true;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this)
                .setTitle("Permanently Delete Note")
                .setMessage("This operation will permanently delete the note from your device.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialogInterface, i) -> {
                    database.deleteNote(noteIdExtra);
                    closeActivity();
                });

        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.delete, getTheme()));
        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.delete, getTheme()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_update, menu);
        switch (noteStatusExtra) {
            case NoteModel.STATUS_DEFAULT:
                menu.findItem(R.id.options_update_unarchive).setVisible(false);
                menu.findItem(R.id.options_update_restore).setVisible(false);
                menu.findItem(R.id.options_update_delete).setVisible(false);
                break;
            case NoteModel.STATUS_ARCHIVED:
                menu.findItem(R.id.options_update_archive).setVisible(false);
                menu.findItem(R.id.options_update_restore).setVisible(false);
                menu.findItem(R.id.options_update_delete).setVisible(false);
                break;
            case NoteModel.STATUS_DELETED:
                menu.findItem(R.id.options_update_unarchive).setVisible(false);
                menu.findItem(R.id.options_update_Trash).setVisible(false);
                break;
        }
        return true;
    }

    private void showShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Objects.requireNonNull(binding.noteInput.getText()).toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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
            updatedNote.setNoteDateEdited(DateUtil.getCurrentTime());
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