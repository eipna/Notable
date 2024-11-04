package com.eipna.notable.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
    private int noteStatusExtra;
    private int noteIsFavoriteExtra;

    private String noteTitleExtra;
    private String noteContentExtra;

    private long noteDateCreatedExtra;
    private long noteLastUpdatedExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(UpdateActivity.this);
        setNoteExtras();

        binding.titleInput.setText(noteTitleExtra);
        binding.noteInput.setText(noteContentExtra);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setNoteExtras() {
        noteIdExtra = getIntent().getIntExtra("NOTE_ID", -1);
        noteTitleExtra = getIntent().getStringExtra("NOTE_TITLE");
        noteContentExtra = getIntent().getStringExtra("NOTE_CONTENT");
        noteDateCreatedExtra = getIntent().getLongExtra("NOTE_DATE_CREATED", -1);
        noteStatusExtra = getIntent().getIntExtra("NOTE_STATUS", -1);
        noteIsFavoriteExtra = getIntent().getIntExtra("NOTE_FAVORITE", -1);
        noteLastUpdatedExtra = getIntent().getLongExtra("NOTE_LAST_UPDATED", -1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            updateNote();
        }

        if (item.getItemId() == R.id.options_update_favorite) {
            switch (noteIsFavoriteExtra) {
                case NoteModel.IS_FAVORITE:
                    noteIsFavoriteExtra = NoteModel.NOT_FAVORITE;
                    break;
                case NoteModel.NOT_FAVORITE:
                    noteIsFavoriteExtra = NoteModel.IS_FAVORITE;
                    break;
            }
            invalidateOptionsMenu();
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

        if (item.getItemId() == R.id.options_update_properties) {
            showNotePropertiesDialog();
        }
        return true;
    }

    @SuppressLint("DefaultLocale")
    private void showNotePropertiesDialog() {
        // Get activity layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);

        // Get dialog properties view or layout
        @SuppressLint("InflateParams")
        View propertiesDialog = inflater.inflate(R.layout.dialog_properties, null);

        // Get all text views in custom properties dialog
        TextView dateCreatedProperty = propertiesDialog.findViewById(R.id.notePropertiesDateCreated);
        TextView lastUpdatedProperty = propertiesDialog.findViewById(R.id.notePropertiesLastUpdated);
        TextView wordCountProperty = propertiesDialog.findViewById(R.id.notePropertiesWordCount);

        // Set text to note properties text views
        final int wordCount = getWordCount(Objects.requireNonNull(binding.noteInput.getText()).toString());
        wordCountProperty.setText(String.format("Word Count: %d", wordCount));

        dateCreatedProperty.setText(String.format("Date Created: %s", DateUtil.getDateString(DateUtil.PATTERN_DETAILED_TIME, noteDateCreatedExtra)));
        lastUpdatedProperty.setText(String.format("Last Updated: %s", DateUtil.getDateString(DateUtil.PATTERN_DETAILED_TIME, noteLastUpdatedExtra)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Properties")
                .setView(propertiesDialog)
                .setNegativeButton("Go Back", null);

        AlertDialog notePropertiesDialog = builder.create();
        notePropertiesDialog.show();

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable popupMenuBG = getResources().getDrawable(R.drawable.popup_menu, getTheme());
        Objects.requireNonNull(notePropertiesDialog.getWindow()).setWindowAnimations(0);
        Objects.requireNonNull(notePropertiesDialog.getWindow()).setBackgroundDrawable(popupMenuBG);

        notePropertiesDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.dialog_button, getTheme()));
    }

    // Get number of words from note content
    private int getWordCount(String string) {
        String[] words = string.split(" ");
        return words.length;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (noteIsFavoriteExtra) {
            case NoteModel.IS_FAVORITE:
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable heartFilled = getResources().getDrawable(R.drawable.heart_filled, getTheme());
                menu.findItem(R.id.options_update_favorite).setIcon(heartFilled);
                break;
            case NoteModel.NOT_FAVORITE:
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable heartNotFilled = getResources().getDrawable(R.drawable.heart_not_filled, getTheme());
                menu.findItem(R.id.options_update_favorite).setIcon(heartNotFilled);
                break;
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

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable popupMenuBG = getResources().getDrawable(R.drawable.popup_menu, getTheme());
        Objects.requireNonNull(deleteDialog.getWindow()).setWindowAnimations(0);
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(popupMenuBG);

        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_button, getTheme()));
        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.dialog_button, getTheme()));
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

        switch (noteIsFavoriteExtra) {
            case NoteModel.IS_FAVORITE:
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable heartFilled = getResources().getDrawable(R.drawable.heart_filled, getTheme());
                menu.findItem(R.id.options_update_favorite).setIcon(heartFilled);
                database.alterNoteFavorite(noteIdExtra, NoteModel.IS_FAVORITE);
                break;
            case NoteModel.NOT_FAVORITE:
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable heartNotFilled = getResources().getDrawable(R.drawable.heart_not_filled, getTheme());
                menu.findItem(R.id.options_update_favorite).setIcon(heartNotFilled);
                database.alterNoteFavorite(noteIdExtra, NoteModel.NOT_FAVORITE);
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
        if (isNoteUnchanged()) {
            closeActivity();
        } else {
            String updatedTitle = Objects.requireNonNull(binding.titleInput.getText()).toString();
            String updatedNote = Objects.requireNonNull(binding.noteInput.getText()).toString();

            // Sets title as empty title placeholder if field is blank
            if (updatedTitle.isEmpty()) {
                updatedTitle = NoteModel.EMPTY_TITLE;
            }

            // Sets note as empty note placeholder if field is blank
            if (updatedNote.isEmpty()) {
                updatedNote = NoteModel.EMPTY_NOTE;
            }

            NoteModel note = new NoteModel();
            note.setNoteId(noteIdExtra);
            note.setNoteTitle(updatedTitle);
            note.setNoteContent(updatedNote);
            note.setNoteLastUpdated(DateUtil.getCurrentTime());

            database.updateNote(note);
            closeActivity();
        }
    }

    private boolean isNoteUnchanged() {
        // Get string in title and content fields
        String titleInField = Objects.requireNonNull(binding.titleInput.getText()).toString();
        String contentInField = Objects.requireNonNull(binding.noteInput.getText()).toString();

        // Match title and content fields string with title and content extras
        return titleInField.equals(noteTitleExtra) && contentInField.equals(noteContentExtra);
    }

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}