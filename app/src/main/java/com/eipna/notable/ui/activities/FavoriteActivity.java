package com.eipna.notable.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eipna.notable.AppDatabase;
import com.eipna.notable.interfaces.NoteListener;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityFavoriteBinding;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements NoteListener {

    private ActivityFavoriteBinding binding;
    private SharedPrefsUtil sharedPrefs;
    private AppDatabase appDatabase;
    private ArrayList<NoteModel> notes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appDatabase = new AppDatabase(FavoriteActivity.this);
        sharedPrefs = new SharedPrefsUtil(FavoriteActivity.this);

        updateNoteList();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateNoteList() {
        notes = appDatabase.readFavoriteNotes();
        binding.emptyIndicator.setVisibility((notes.isEmpty()) ? View.VISIBLE : View.GONE);

        noteAdapter = new NoteAdapter(this, this, notes);
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

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateNoteList();
        }
    });

    @Override
    public void onNoteClick(int position) {
        NoteModel selectedNote = notes.get(position);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}