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
import com.eipna.notable.enums.NoteState;
import com.eipna.notable.interfaces.NoteListener;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityArchiveBinding;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity implements NoteListener {

    private ActivityArchiveBinding binding;
    private AppDatabase appDatabase;
    private ArrayList<NoteModel> archivedNotes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appDatabase = new AppDatabase(this);
        archivedNotes = appDatabase.getArchivedNotes();
        binding.emptyIndicator.setVisibility(archivedNotes.isEmpty() ? View.VISIBLE : View.GONE);

        String layoutMgr = new SharedPrefsUtil(this).getString("prefs_note_layout", "list");
        if (layoutMgr.equals("list")) {
            binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            binding.noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        noteAdapter = new NoteAdapter(this, this, archivedNotes);
        binding.noteList.setAdapter(noteAdapter);
    }

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadNewNotes();
        }
    });

    private void loadNewNotes() {
        archivedNotes = appDatabase.getActiveNotes();
        binding.emptyIndicator.setVisibility(archivedNotes.isEmpty() ? View.VISIBLE : View.GONE);
        noteAdapter.loadNotes(archivedNotes);
    }

    @Override
    public void onNoteClick(int position) {
        NoteModel selectedNote = archivedNotes.get(position);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("selected_note", selectedNote);
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