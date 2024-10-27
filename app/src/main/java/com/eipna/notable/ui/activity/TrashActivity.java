package com.eipna.notable.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityTrashBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;
import com.eipna.notable.util.SharedPrefsUtil;

import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity implements NoteListener {

    private ActivityTrashBinding binding;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(TrashActivity.this);
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
        menu.findItem(R.id.options_trash_clear).setVisible(!notes.isEmpty());
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.options_trash_clear).setVisible(!notes.isEmpty());
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

    private void showClearDialog() {
        @SuppressLint("NotifyDataSetChanged")
        AlertDialog.Builder builder = new AlertDialog.Builder(TrashActivity.this)
                .setTitle("Clear Notes")
                .setMessage("This operation will clear all trashed notes in your device")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear", (dialogInterface, i) -> {
                    database.clearTrashNotes();
                    notes.clear();
                    adapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
                    updateNoteList();
                });

        AlertDialog clearDialog = builder.create();
        clearDialog.show();
        clearDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.delete, getTheme()));
        clearDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.delete, getTheme()));
    }

    private void updateNoteList() {
        notes = database.readNotes(NoteModel.STATUS_DELETED);
        invalidateOptionsMenu();
        binding.emptyIndicator.setVisibility((notes.isEmpty()) ? View.VISIBLE : View.GONE);

        adapter = new NoteAdapter(this, this, notes);
        updateNoteDisplay();
    }

    private void updateNoteDisplay() {
        String display = sharedPrefs.getString("DISPLAY", "list");
        switch (display) {
            case "list":
                binding.noteList.setLayoutManager(new LinearLayoutManager(this));
                binding.noteList.setAdapter(adapter);
                break;
            case "grid":
                binding.noteList.setLayoutManager(new GridLayoutManager(this, 2));
                binding.noteList.setAdapter(adapter);
                break;
        }
    }

    @Override
    public void onNoteClick(int position) {
        NoteModel note = notes.get(position);
        int noteId = note.getNoteId();
        String noteTitle = note.getNoteTitle();
        String noteContent = note.getNoteContent();
        long noteDateCreated = note.getNoteDateCreated();
        int noteStatus = note.getNoteStatus();

        Intent updateNoteIntent = new Intent(TrashActivity.this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE_ID", noteId);
        updateNoteIntent.putExtra("NOTE_TITLE", noteTitle);
        updateNoteIntent.putExtra("NOTE_CONTENT", noteContent);
        updateNoteIntent.putExtra("NOTE_DATE_CREATED", noteDateCreated);
        updateNoteIntent.putExtra("NOTE_STATUS", noteStatus);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment
    }
}