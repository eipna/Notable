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
import com.eipna.notable.constants.NoteList;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityFavoriteBinding;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements NoteAdapter.Listener {

    private ActivityFavoriteBinding binding;
    private AppDatabase appDatabase;
    private ArrayList<NoteModel> favoriteNotes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appDatabase = new AppDatabase(this);
        favoriteNotes = new ArrayList<>();
        favoriteNotes.addAll(appDatabase.getFavoriteNotes());
        binding.emptyIndicator.setVisibility(favoriteNotes.isEmpty() ? View.VISIBLE : View.GONE);

        String layoutMgr = new SharedPrefsUtil(this).getString("prefs_note_layout", NoteList.LIST.getValue());
        if (layoutMgr.equals(NoteList.LIST.getValue())) {
            binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            binding.noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        noteAdapter = new NoteAdapter(this, this, favoriteNotes);
        binding.noteList.setAdapter(noteAdapter);
    }

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadNewNotes();
        }
    });

    private void loadNewNotes() {
        favoriteNotes.clear();
        favoriteNotes.addAll(appDatabase.getFavoriteNotes());
        binding.emptyIndicator.setVisibility(favoriteNotes.isEmpty() ? View.VISIBLE : View.GONE);
        noteAdapter.loadNotes(favoriteNotes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void launchUpdateActivity(int adapterPos) {
        NoteModel selectedNote = favoriteNotes.get(adapterPos);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("selected_note", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }
}