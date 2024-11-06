package com.eipna.notable.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eipna.notable.R;
import com.eipna.notable.data.Database;
import com.eipna.notable.data.interfaces.NoteListener;
import com.eipna.notable.data.model.NoteModel;
import com.eipna.notable.databinding.ActivityTrashBinding;
import com.eipna.notable.ui.adapter.NoteAdapter;
import com.eipna.notable.util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.Objects;

public class TrashActivity extends AppCompatActivity implements NoteListener {

    private ActivityTrashBinding binding;
    private Database database;
    private ArrayList<NoteModel> notes;
    private NoteAdapter adapter;
    private SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = new Database(TrashActivity.this);
        sharedPrefs = new SharedPrefsUtil(TrashActivity.this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_trash, menu);
        menu.findItem(R.id.options_trash_clear).setVisible(!notes.isEmpty());
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.options_trash_clear).setVisible(!notes.isEmpty());
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
        final int colorInvert = getResources().getColor(R.color.primary_invert, getTheme());

        @SuppressLint("InflateParams")
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null);

        TextView titleTV = dialogView.findViewById(R.id.dialogTitle);
        titleTV.setVisibility(View.GONE);

        TextView messageTV = dialogView.findViewById(R.id.dialogMessage);
        messageTV.setText("This action cannot be undone, and all trashed notes will be lost forever.");
        messageTV.setTextColor(colorInvert);

        AlertDialog.Builder clearDialogBuilder = new AlertDialog.Builder(this);
        clearDialogBuilder.setView(dialogView);
        clearDialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        clearDialogBuilder.setPositiveButton("Clear", (dialogInterface, i) -> {
            database.clearTrashNotes();
            notes.clear();
            adapter.notifyDataSetChanged();
            invalidateOptionsMenu();
            updateNoteList();
        });

        AlertDialog clearDialog = clearDialogBuilder.create();
        clearDialog.show();

        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(clearDialog.getWindow()).getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        clearDialog.getWindow().setAttributes(layoutParams);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable dialogBG = getResources().getDrawable(R.drawable.popup_menu, getTheme());
        clearDialog.getWindow().setWindowAnimations(0);
        clearDialog.getWindow().setBackgroundDrawable(dialogBG);

        clearDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorInvert);
        clearDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorInvert);
    }

    private void updateNoteList() {
        notes = database.readNotes(NoteModel.STATUS_DELETED);
        invalidateOptionsMenu();
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
        StaggeredGridLayoutManager gridLayout = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);

        switch (display) {
            case "list":
                binding.noteList.setLayoutManager(listLayout);
                binding.noteList.setAdapter(adapter);
                break;
            case "grid":
                binding.noteList.setLayoutManager(gridLayout);
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