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

import com.eipna.notable.Database;
import com.eipna.notable.R;
import com.eipna.notable.constants.NoteList;
import com.eipna.notable.constants.NoteSort;
import com.eipna.notable.databinding.ActivityMainBinding;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteAdapter.Listener {

    private ActivityMainBinding binding;
    private Database database;
    private ArrayList<NoteModel> activeNotes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        database = new Database(MainActivity.this);
        activeNotes = new ArrayList<>();
        activeNotes.addAll(database.getActiveNotes());
        activeNotes.sort(NoteSort.getComparator(this));
        binding.emptyIndicator.setVisibility(activeNotes.isEmpty() ? View.VISIBLE : View.GONE);

        String layoutMgr = new SharedPrefsUtil(this).getString("prefs_note_list", NoteList.LIST.getValue());
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
            recreate();
        }
    });

    private void loadNote() {
        activeNotes.clear();
        activeNotes.addAll(database.getActiveNotes());
        binding.emptyIndicator.setVisibility(activeNotes.isEmpty() ? View.VISIBLE : View.GONE);
        noteAdapter.loadNotes(activeNotes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);

        MenuItem searchItem = menu.findItem(R.id.options_main_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        MenuItem checkedSortingMenuItem = menu.findItem(NoteSort.getMenuItem(this));
        checkedSortingMenuItem.setChecked(true);

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
        noteAdapter.search(queriedNotes);
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

        if (item.getItemId() == R.id.sort_title_asc) {
            noteAdapter.sort(NoteSort.TITLE_ASCENDING);
            item.setChecked(true);
        }

        if (item.getItemId() == R.id.sort_title_desc) {
            noteAdapter.sort(NoteSort.TITLE_DESCENDING);
            item.setChecked(true);
        }

        if (item.getItemId() == R.id.sort_date_created_asc) {
            noteAdapter.sort(NoteSort.DATE_CREATED_ASCENDING);
            item.setChecked(true);
        }

        if (item.getItemId() == R.id.sort_date_created_desc) {
            noteAdapter.sort(NoteSort.DATE_CREATED_DESCENDING);
            item.setChecked(true);
        }

        if (item.getItemId() == R.id.sort_last_updated_asc) {
            noteAdapter.sort(NoteSort.LAST_UPDATED_ASCENDING);
            item.setChecked(true);
        }

        if (item.getItemId() == R.id.sort_last_updated_desc) {
            noteAdapter.sort(NoteSort.LAST_UPDATED_DESCENDING);
            item.setChecked(true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void OnItemClick(int adapterPos) {
        NoteModel selectedNote = activeNotes.get(adapterPos);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("selected_note", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void OnItemLongClick(int adapterPos) {

    }
}