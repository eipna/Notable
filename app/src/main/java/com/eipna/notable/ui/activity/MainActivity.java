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
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityMainBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        adapter = new NoteAdapter(MainActivity.this, notes);
        binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        binding.noteList.setAdapter(adapter);
    }
}