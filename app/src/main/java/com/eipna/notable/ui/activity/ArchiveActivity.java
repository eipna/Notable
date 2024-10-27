package com.eipna.notable.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityArchiveBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity implements NoteListener {

    private ActivityArchiveBinding binding;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(ArchiveActivity.this);

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
        notes = database.readNotes(NoteModel.STATUS_ARCHIVED);
        adapter = new NoteAdapter(this, this, notes);
        binding.noteList.setLayoutManager(new LinearLayoutManager(ArchiveActivity.this));
        binding.noteList.setAdapter(adapter);
    }

    @Override
    public void onNoteClick(int position) {
        NoteModel note = notes.get(position);
        int noteId = note.getNoteId();
        String noteTitle = note.getNoteTitle();
        String noteContent = note.getNoteContent();
        long noteDateCreated = note.getNoteDateCreated();
        int noteStatus = note.getNoteStatus();

        Intent updateNoteIntent = new Intent(ArchiveActivity.this, UpdateActivity.class);
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