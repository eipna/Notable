package com.eipna.notable.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityMainBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteListener {

    private ActivityMainBinding binding;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        database = new Database(MainActivity.this);

        updateNoteList();

        binding.createNote.setOnClickListener(view -> createNoteLauncher.launch(new Intent(MainActivity.this, CreateActivity.class)));
    }

    private final ActivityResultLauncher<Intent> createNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateNoteList();
        }
    });

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateNoteList();
        }
    });

    private void updateNoteList() {
        notes = database.readNotes();
        adapter = new NoteAdapter(this, this, notes);
        binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        binding.noteList.setAdapter(adapter);
    }

    @Override
    public void onNoteClick(int position) {
        NoteModel note = notes.get(position);
        int noteId = note.getNoteId();
        String noteTitle = note.getNoteTitle();
        String noteContent = note.getNoteContent();
        long noteDateCreated = note.getNoteDateCreated();

        Intent updateNoteIntent = new Intent(MainActivity.this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE_ID", noteId);
        updateNoteIntent.putExtra("NOTE_TITLE", noteTitle);
        updateNoteIntent.putExtra("NOTE_CONTENT", noteContent);
        updateNoteIntent.putExtra("NOTE_DATE_CREATED", noteDateCreated);
        updateNoteLauncher.launch(updateNoteIntent);
    }
}