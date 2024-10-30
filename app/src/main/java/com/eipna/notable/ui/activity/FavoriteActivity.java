package com.eipna.notable.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityFavoriteBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;
import com.eipna.notable.util.SharedPrefsUtil;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements NoteListener {

    private ActivityFavoriteBinding binding;
    private SharedPrefsUtil sharedPrefs;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(FavoriteActivity.this);
        sharedPrefs = new SharedPrefsUtil(FavoriteActivity.this);

        updateNoteList();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateNoteList() {
        notes = database.readFavoriteNotes();
        binding.emptyIndicator.setVisibility((notes.isEmpty()) ? View.VISIBLE : View.GONE);

        adapter = new NoteAdapter(this, this, notes);
        updateNoteDisplay();
    }

    private void updateNoteDisplay() {
        String display = sharedPrefs.getString("DISPLAY", "list");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FavoriteActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(FavoriteActivity.this, 2);
        gridLayoutManager.setReverseLayout(true);

        switch (display) {
            case "list":
                binding.noteList.setLayoutManager(linearLayoutManager);
                binding.noteList.setAdapter(adapter);
                break;
            case "grid":
                binding.noteList.setLayoutManager(gridLayoutManager);
                binding.noteList.setAdapter(adapter);
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
        NoteModel note = notes.get(position);
        int noteId = note.getNoteId();
        String noteTitle = note.getNoteTitle();
        String noteContent = note.getNoteContent();
        long noteDateCreated = note.getNoteDateCreated();
        int noteStatus = note.getNoteStatus();
        int noteIsFavorite = note.getIsFavorite();

        Intent updateNoteIntent = new Intent(FavoriteActivity.this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE_ID", noteId);
        updateNoteIntent.putExtra("NOTE_TITLE", noteTitle);
        updateNoteIntent.putExtra("NOTE_CONTENT", noteContent);
        updateNoteIntent.putExtra("NOTE_DATE_CREATED", noteDateCreated);
        updateNoteIntent.putExtra("NOTE_STATUS", noteStatus);
        updateNoteIntent.putExtra("NOTE_FAVORITE", noteIsFavorite);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment;
    }
}