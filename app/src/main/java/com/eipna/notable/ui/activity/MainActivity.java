package com.eipna.notable.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityMainBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;
import com.eipna.notable.util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements NoteListener {

    private ActivityMainBinding binding;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        database = new Database(MainActivity.this);
        sharedPrefs = new SharedPrefsUtil(MainActivity.this);

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

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            updateNoteList();
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);

        MenuItem searchItem = menu.findItem(R.id.options_main_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setQueryHint("Search notes...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return true;
            }
        });

        return true;
    }

    private void filterNotes(String query) {
        final ArrayList<NoteModel> filteredNotes = new ArrayList<>();
        for (NoteModel note : notes) {
            if (note.getNoteTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredNotes.add(note);
            }
        }

        if (filteredNotes.isEmpty()) {
            Toast.makeText(this, "No notes matched", Toast.LENGTH_SHORT).show();
        } else {
            adapter.searchNotes(filteredNotes);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.options_main_favorite) {
            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
        }

        if (item.getItemId() == R.id.options_main_archive) {
            startActivity(new Intent(MainActivity.this, ArchiveActivity.class));
        }

        if (item.getItemId() == R.id.options_main_trash) {
            startActivity(new Intent(MainActivity.this, TrashActivity.class));
        }

        if (item.getItemId() == R.id.options_main_settings) {
            settingsLauncher.launch(new Intent(MainActivity.this, SettingsActivity.class));
        }
        return true;
    }

    private void updateNoteList() {
        notes = database.readNotes(NoteModel.STATUS_DEFAULT);
        binding.emptyIndicator.setVisibility((notes.isEmpty()) ? View.VISIBLE : View.GONE);

        adapter = new NoteAdapter(this, this, notes);
        updateNoteDisplay();
    }

    private void updateNoteDisplay() {
        String display = sharedPrefs.getString("DISPLAY", "list");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
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

    @Override
    public void onNoteClick(int position) {
        NoteModel note = notes.get(position);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE_ID", note.getNoteId());
        updateNoteIntent.putExtra("NOTE_TITLE", note.getNoteTitle());
        updateNoteIntent.putExtra("NOTE_CONTENT", note.getNoteContent());
        updateNoteIntent.putExtra("NOTE_DATE_CREATED", note.getNoteDateCreated());
        updateNoteIntent.putExtra("NOTE_STATUS", note.getNoteStatus());
        updateNoteIntent.putExtra("NOTE_FAVORITE", note.getIsFavorite());
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment (Future feature: Multi select functions)
    }
}