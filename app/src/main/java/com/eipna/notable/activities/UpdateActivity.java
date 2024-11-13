package com.eipna.notable.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.eipna.notable.R;
import com.eipna.notable.Database;
import com.eipna.notable.constants.NoteState;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityUpdateBinding;
import com.eipna.notable.utils.DateUtil;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;
    private Database database;

    private NoteModel currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new Database(UpdateActivity.this);
        currentNote = getIntent().getParcelableExtra("NOTE");

        assert currentNote != null;
        binding.titleInput.setText(currentNote.getNoteTitle());
        binding.noteInput.setText(currentNote.getNoteContent());

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

        if (item.getItemId() == R.id.options_update_favorite) {
            if (currentNote.getIsFavorite() == NoteState.FAVORITE_YES.getValue()) {
                currentNote.setIsFavorite(NoteState.FAVORITE_NO.getValue());
            } else {
                currentNote.setIsFavorite(NoteState.FAVORITE_YES.getValue());
            }
            invalidateOptionsMenu();
        }

        if (item.getItemId() == R.id.options_update_archive) {
            database.alterNoteStatus(currentNote.getNoteId(), NoteState.ARCHIVED.getValue());
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_unarchive) {
            database.alterNoteStatus(currentNote.getNoteId(), NoteState.ACTIVE.getValue());
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_Trash) {
            database.alterNoteStatus(currentNote.getNoteId(), NoteState.DELETED.getValue());
            closeActivity();
        }

        if (item.getItemId() == R.id.options_update_restore) {
            database.alterNoteStatus(currentNote.getNoteId(), NoteState.ACTIVE.getValue());
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

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void showNotePropertiesDialog() {
        final int colorInverted = getResources().getColor(R.color.primary_invert, getTheme());
        final int noteWordCount = getWordCount(Objects.requireNonNull(binding.noteInput.getText()).toString());

        @SuppressLint("InflateParams")
        View customDialogTitle = LayoutInflater.from(this).inflate(R.layout.dialog_custom_title, null);

        @SuppressLint("InflateParams")
        View customDialogLibraries = LayoutInflater.from(this).inflate(R.layout.dialog_note_properties, null);

        TextView titleTV = customDialogTitle.findViewById(R.id.customDialogTitle);
        titleTV.setText("Properties");

        TextView wordCountTV = customDialogLibraries.findViewById(R.id.wordCountProperty);
        wordCountTV.setText(String.format("Word Count: %d", noteWordCount));

        TextView dateCreatedTV = customDialogLibraries.findViewById(R.id.dateCreatedProperty);
        dateCreatedTV.setText(String.format("Date Created: %s", DateUtil.getDateString(DateUtil.PATTERN_DETAILED_TIME, currentNote.getNoteDateCreated())));

        TextView lastUpdatedTV = customDialogLibraries.findViewById(R.id.lastUpdatedProperty);
        lastUpdatedTV.setText(String.format("Last Updated: %s", DateUtil.getDateString(DateUtil.PATTERN_DETAILED_TIME, currentNote.getNoteLastUpdated())));

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCustomTitle(titleTV);
        dialogBuilder.setView(customDialogLibraries);
        dialogBuilder.setPositiveButton("Back", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog propertiesDialog = dialogBuilder.create();
        propertiesDialog.show();

        TextView messageTV = propertiesDialog.findViewById(android.R.id.message);
        Objects.requireNonNull(messageTV).setTextColor(colorInverted);

        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(propertiesDialog.getWindow()).getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.86);
        propertiesDialog.getWindow().setAttributes(layoutParams);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable dialogBackground = getResources().getDrawable(R.drawable.dialog, getTheme());
        propertiesDialog.getWindow().setWindowAnimations(0);
        propertiesDialog.getWindow().setBackgroundDrawable(dialogBackground);

        propertiesDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorInverted);
    }

    // Get number of words from note content
    private int getWordCount(String string) {
        String[] words = string.split(" ");
        return words.length;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentNote.getIsFavorite() == NoteState.FAVORITE_YES.getValue()) {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable heartFilled = getResources().getDrawable(R.drawable.heart_filled, getTheme());
            menu.findItem(R.id.options_update_favorite).setIcon(heartFilled);
        } else {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable heartNotFilled = getResources().getDrawable(R.drawable.heart_not_filled, getTheme());
            menu.findItem(R.id.options_update_favorite).setIcon(heartNotFilled);
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void showDeleteDialog() {
        final int colorInverted = getResources().getColor(R.color.primary_invert, getTheme());

        @SuppressLint("InflateParams")
        View customDialogTitle = LayoutInflater.from(this).inflate(R.layout.dialog_custom_title, null);

        TextView titleTV = customDialogTitle.findViewById(R.id.customDialogTitle);
        titleTV.setVisibility(View.GONE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCustomTitle(customDialogTitle);
        dialogBuilder.setMessage("Are you sure you want to permanently delete this note?");
        dialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        dialogBuilder.setPositiveButton("Delete", (dialogInterface, i) -> {
            database.deleteNote(currentNote.getNoteId());
            closeActivity();
        });

        AlertDialog deleteDialog = dialogBuilder.create();
        deleteDialog.show();

        TextView messageText = deleteDialog.findViewById(android.R.id.message);
        Objects.requireNonNull(messageText).setTextColor(colorInverted);

        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(deleteDialog.getWindow()).getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.86);
        deleteDialog.getWindow().setAttributes(layoutParams);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable dialogBackground = getResources().getDrawable(R.drawable.dialog, getTheme());
        deleteDialog.getWindow().setWindowAnimations(0);
        deleteDialog.getWindow().setBackgroundDrawable(dialogBackground);

        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorInverted);
        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorInverted);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_update, menu);

        if (currentNote.getNoteState() == NoteState.ACTIVE.getValue()) {
            menu.findItem(R.id.options_update_unarchive).setVisible(false);
            menu.findItem(R.id.options_update_restore).setVisible(false);
            menu.findItem(R.id.options_update_delete).setVisible(false);
        } else if (currentNote.getNoteState() == NoteState.ARCHIVED.getValue()) {
            menu.findItem(R.id.options_update_archive).setVisible(false);
            menu.findItem(R.id.options_update_restore).setVisible(false);
            menu.findItem(R.id.options_update_delete).setVisible(false);
        } else if (currentNote.getNoteState() == NoteState.DELETED.getValue()) {
            menu.findItem(R.id.options_update_unarchive).setVisible(false);
            menu.findItem(R.id.options_update_Trash).setVisible(false);
        }

        if (currentNote.getIsFavorite() == NoteState.FAVORITE_YES.getValue()) {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable heartFilled = getResources().getDrawable(R.drawable.heart_filled, getTheme());
            menu.findItem(R.id.options_update_favorite).setIcon(heartFilled);
            database.alterNoteFavorite(currentNote.getNoteId(), NoteState.FAVORITE_YES.getValue());
        } else {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable heartNotFilled = getResources().getDrawable(R.drawable.heart_not_filled, getTheme());
            menu.findItem(R.id.options_update_favorite).setIcon(heartNotFilled);
            database.alterNoteFavorite(currentNote.getNoteId(), NoteState.FAVORITE_NO.getValue());
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
            note.setNoteId(currentNote.getNoteId());
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
        return titleInField.equals(currentNote.getNoteTitle()) && contentInField.equals(currentNote.getNoteContent());
    }

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}