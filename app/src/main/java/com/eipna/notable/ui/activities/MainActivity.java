package com.eipna.notable.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eipna.notable.R;
import com.eipna.notable.AppDatabase;
import com.eipna.notable.enums.NoteState;
import com.eipna.notable.interfaces.NoteListener;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityMainBinding;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteListener {

    private ActivityMainBinding binding;
    private AppDatabase appDatabase;
    private ArrayList<NoteModel> activeNotes;
    private NoteAdapter noteAdapter;
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        appDatabase = new AppDatabase(MainActivity.this);
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
        for (NoteModel note : activeNotes) {
            if (note.getNoteTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredNotes.add(note);
            }
        }
        noteAdapter.searchNotes(filteredNotes);
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
        activeNotes = appDatabase.readNotes(NoteState.ACTIVE.getValue());
        binding.emptyIndicator.setVisibility((activeNotes.isEmpty()) ? View.VISIBLE : View.GONE);

        noteAdapter = new NoteAdapter(this, this, activeNotes);
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
        NoteModel selectedNote = activeNotes.get(position);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("NOTE", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment (Future feature: Multi select functions)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}