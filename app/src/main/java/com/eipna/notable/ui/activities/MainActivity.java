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

import com.eipna.notable.AppDatabase;
import com.eipna.notable.R;
import com.eipna.notable.constants.NoteList;
import com.eipna.notable.databinding.ActivityMainBinding;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteAdapter.Listener {

    private ActivityMainBinding binding;
    private AppDatabase appDatabase;
    private ArrayList<NoteModel> activeNotes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        appDatabase = new AppDatabase(MainActivity.this);
        activeNotes = new ArrayList<>();
        activeNotes.addAll(appDatabase.getActiveNotes());
        binding.emptyIndicator.setVisibility(activeNotes.isEmpty() ? View.VISIBLE : View.GONE);

        String layoutMgr = new SharedPrefsUtil(this).getString("prefs_note_layout", NoteList.LIST.getValue());
        if (layoutMgr.equals(NoteList.LIST.getValue())) {
            binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            binding.noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        noteAdapter = new NoteAdapter(this, this, activeNotes);
        binding.noteList.setAdapter(noteAdapter);

        binding.createNote.setOnClickListener(view -> {
            Intent createNoteIntent = new Intent(MainActivity.this, CreateActivity.class);
            createNoteLauncher.launch(createNoteIntent);
        });
    }

    private final ActivityResultLauncher<Intent> createNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadNote();
        }
    });

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadNote();
        }
    });

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            resetAdapter();
        }
    });

    private void loadNote() {
        activeNotes.clear();
        activeNotes.addAll(appDatabase.getActiveNotes());
        binding.emptyIndicator.setVisibility(activeNotes.isEmpty() ? View.VISIBLE : View.GONE);
        noteAdapter.loadNotes(activeNotes);
    }

    private void resetAdapter() {
        String layoutMgr = new SharedPrefsUtil(this).getString("prefs_note_layout", NoteList.LIST.getValue());
        if (layoutMgr.equals(NoteList.LIST.getValue())) {
            binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            binding.noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        binding.noteList.setAdapter(noteAdapter);
    }

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
            public boolean onQueryTextChange(String query) {
                queryNotesFromSearch(query);
                return true;
            }
        });

        return true;
    }

    private void queryNotesFromSearch(String query) {
        final ArrayList<NoteModel> queriedNotes = new ArrayList<>();
        for (NoteModel note : activeNotes) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                queriedNotes.add(note);
            }
        }
        noteAdapter.searchNotes(queriedNotes);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void launchUpdateActivity(int adapterPos) {
        NoteModel selectedNote = activeNotes.get(adapterPos);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("selected_note", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }
}