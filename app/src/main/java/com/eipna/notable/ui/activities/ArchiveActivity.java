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
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appDatabase = new AppDatabase(ArchiveActivity.this);
        sharedPrefs = new SharedPrefsUtil(ArchiveActivity.this);

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

    private void updateNoteList() {
        archivedNotes = appDatabase.readNotes(NoteState.ARCHIVED.getValue());
        binding.emptyIndicator.setVisibility((archivedNotes.isEmpty()) ? View.VISIBLE : View.GONE);

        noteAdapter = new NoteAdapter(this, this, archivedNotes);
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
        NoteModel selectedNote = archivedNotes.get(position);
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