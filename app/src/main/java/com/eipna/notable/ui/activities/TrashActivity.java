package com.eipna.notable.ui.activities;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eipna.notable.R;
import com.eipna.notable.AppDatabase;
import com.eipna.notable.enums.NoteState;
import com.eipna.notable.interfaces.NoteListener;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityTrashBinding;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.Objects;

public class TrashActivity extends AppCompatActivity implements NoteListener {

    private ActivityTrashBinding binding;
    private AppDatabase appDatabase;
    private ArrayList<NoteModel> deletedNotes;
    private NoteAdapter noteAdapter;
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appDatabase = new AppDatabase(TrashActivity.this);
        sharedPrefs = new SharedPrefsUtil(TrashActivity.this);

        updateNoteList();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateNoteList();
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_trash, menu);
        menu.findItem(R.id.options_trash_clear).setVisible(!deletedNotes.isEmpty());
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.options_trash_clear).setVisible(!deletedNotes.isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.options_trash_clear) {
            showClearDialog();
        }
        return true;
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void showClearDialog() {
        final int colorInverted = getResources().getColor(R.color.primary_invert, getTheme());

        @SuppressLint("InflateParams")
        View customDialogTitle = LayoutInflater.from(this).inflate(R.layout.dialog_custom_title, null);

        TextView titleTV = customDialogTitle.findViewById(R.id.customDialogTitle);
        titleTV.setVisibility(View.GONE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCustomTitle(titleTV);
        dialogBuilder.setMessage("This action cannot be undone, and all trashed notes will be lost forever.");
        dialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        dialogBuilder.setPositiveButton("Clear", (dialogInterface, i) -> clear());

        AlertDialog clearDialog = dialogBuilder.create();
        clearDialog.show();

        TextView messageTV = clearDialog.findViewById(android.R.id.message);
        Objects.requireNonNull(messageTV).setTextColor(colorInverted);

        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(clearDialog.getWindow()).getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.86);
        clearDialog.getWindow().setAttributes(layoutParams);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable dialogBackground = getResources().getDrawable(R.drawable.dialog, getTheme());
        clearDialog.getWindow().setWindowAnimations(0);
        clearDialog.getWindow().setBackgroundDrawable(dialogBackground);

        clearDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorInverted);
        clearDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorInverted);
    }

    /* Clears all trashed notes in database
    *  Clears all notes in notes array list
    *  Updates the note adapter
    *  Updates the options menu */
    @SuppressLint("NotifyDataSetChanged")
    private void clear() {
        appDatabase.clear(NoteState.DELETED.getValue());
        deletedNotes.clear();
        noteAdapter.notifyDataSetChanged();
        updateNoteList();
    }

    private void updateNoteList() {
        deletedNotes = appDatabase.getDeletedNotes();
        invalidateOptionsMenu();
        binding.emptyIndicator.setVisibility((deletedNotes.isEmpty()) ? View.VISIBLE : View.GONE);

        noteAdapter = new NoteAdapter(this, this, deletedNotes);
        updateNoteDisplay();
    }

    private void updateNoteDisplay() {
        String display = sharedPrefs.getString("prefs_note_layout", "list");
        switch (display) {
            case "list":
                binding.noteList.setLayoutManager(new LinearLayoutManager(this));
                binding.noteList.setAdapter(noteAdapter);
                break;
            case "grid":
                binding.noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                binding.noteList.setAdapter(noteAdapter);
                break;
        }
    }

    @Override
    public void onNoteClick(int position) {
        NoteModel selectedNote = deletedNotes.get(position);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}