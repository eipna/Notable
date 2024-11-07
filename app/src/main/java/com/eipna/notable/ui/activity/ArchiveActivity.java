package com.eipna.notable.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eipna.notable.R;
import com.eipna.notable.custom.ItemSpacingDecoration;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityArchiveBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;
import com.eipna.notable.util.SharedPrefsUtil;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity implements NoteListener {

    private ActivityArchiveBinding binding;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(ArchiveActivity.this);
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
        notes = database.readNotes(NoteModel.STATUS_ARCHIVED);
        binding.emptyIndicator.setVisibility((notes.isEmpty()) ? View.VISIBLE : View.GONE);

        adapter = new NoteAdapter(this, this, notes);
        updateNoteDisplay();
    }

    private void updateNoteDisplay() {
        String display = sharedPrefs.getString("DISPLAY", "list");

        LinearLayoutManager listLayout = new LinearLayoutManager(this);
        listLayout.setReverseLayout(true);
        listLayout.setStackFromEnd(true);

        final int SPAN_COUNT = 2;
        final int ITEM_SPACING = 0;
        StaggeredGridLayoutManager gridLayout = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);

        switch (display) {
            case "list":
                binding.noteList.setLayoutManager(listLayout);
                binding.noteList.addItemDecoration(new ItemSpacingDecoration(ITEM_SPACING));
                binding.noteList.setAdapter(adapter);
                break;
            case "grid":
                binding.noteList.setLayoutManager(gridLayout);
                binding.noteList.addItemDecoration(new ItemSpacingDecoration(ITEM_SPACING));
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
        updateNoteIntent.putExtra("NOTE_LAST_UPDATED", note.getNoteLastUpdated());
        updateNoteIntent.putExtra("NOTE_STATUS", note.getNoteStatus());
        updateNoteIntent.putExtra("NOTE_FAVORITE", note.getIsFavorite());
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Do nothing at the moment
    }
}