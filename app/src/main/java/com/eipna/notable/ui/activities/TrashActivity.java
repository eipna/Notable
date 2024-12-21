package com.eipna.notable.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eipna.notable.R;
import com.eipna.notable.Database;
import com.eipna.notable.constants.NoteList;
import com.eipna.notable.constants.NoteSort;
import com.eipna.notable.constants.NoteState;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivityTrashBinding;
import com.eipna.notable.ui.adapters.NoteAdapter;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.Objects;

public class TrashActivity extends AppCompatActivity implements NoteAdapter.Listener {

    private ActivityTrashBinding binding;
    private Database database;
    private ArrayList<NoteModel> deletedNotes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = new Database(this);
        deletedNotes = new ArrayList<>();
        deletedNotes.addAll(database.getNotes(NoteState.DELETED));
        deletedNotes.sort(NoteSort.getComparator(this));
        binding.emptyIndicator.setVisibility(deletedNotes.isEmpty() ? View.VISIBLE : View.GONE);

        String layoutMgr = new SharedPrefsUtil(this).getString("prefs_note_list", NoteList.LIST.getValue());
        if (layoutMgr.equals(NoteList.LIST.getValue())) {
            binding.noteList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            binding.noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        noteAdapter = new NoteAdapter(this, this, deletedNotes);
        binding.noteList.setAdapter(noteAdapter);
    }

    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            loadNewNotes();
        }
    });

    private void loadNewNotes() {
        deletedNotes.clear();
        deletedNotes.addAll(database.getNotes(NoteState.DELETED));
        binding.emptyIndicator.setVisibility(deletedNotes.isEmpty() ? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
        noteAdapter.loadNotes(deletedNotes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_trash, menu);
        menu.findItem(R.id.options_trash_clear).setVisible(!deletedNotes.isEmpty());

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
        for (NoteModel note : deletedNotes) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                queriedNotes.add(note);
            }
        }
        noteAdapter.search(queriedNotes);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.options_trash_clear).setVisible(!deletedNotes.isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.options_trash_clear) {
            showClearDialog();
        }
        return true;
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void showClearDialog() {
        final int colorInverted = getResources().getColor(R.color.primary_invert, getTheme());

        @SuppressLint("InflateParams")
        View customDialogTitle = LayoutInflater.from(this).inflate(R.layout.dialog_custom_title, null);

        TextView titleTV = customDialogTitle.findViewById(R.id.customDialogTitle);
        titleTV.setVisibility(View.GONE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCustomTitle(titleTV);
        dialogBuilder.setMessage("This action cannot be undone, and all trashed notes will be lost forever.");
        dialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        dialogBuilder.setPositiveButton("Clear", (dialogInterface, i) -> clear());

        AlertDialog clearDialog = dialogBuilder.create();
        clearDialog.show();

        TextView messageTV = clearDialog.findViewById(android.R.id.message);
        Objects.requireNonNull(messageTV).setTextColor(colorInverted);

        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(clearDialog.getWindow()).getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.86);
        clearDialog.getWindow().setAttributes(layoutParams);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable dialogBackground = getResources().getDrawable(R.drawable.bg_dialog, getTheme());
        clearDialog.getWindow().setWindowAnimations(0);
        clearDialog.getWindow().setBackgroundDrawable(dialogBackground);

        clearDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorInverted);
        clearDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorInverted);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clear() {
        database.clear(NoteState.DELETED.getValue());
        loadNewNotes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void OnItemClick(int adapterPos) {
        NoteModel selectedNote = deletedNotes.get(adapterPos);
        Intent updateNoteIntent = new Intent(this, UpdateActivity.class);
        updateNoteIntent.putExtra("selected_note", selectedNote);
        updateNoteLauncher.launch(updateNoteIntent);
    }

    @Override
    public void OnItemLongClick(int adapterPos) {

    }
}