package com.eipna.notable.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.eipna.notable.R;
import com.eipna.notable.AppDatabase;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.databinding.ActivitySettingsBinding;
import com.eipna.notable.utils.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsContainer, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private SharedPrefsUtil prefs;

        private int easterEggCounter;

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            prefs = new SharedPrefsUtil(requireContext());

            Preference settingsAppVersion = findPreference("settings_app_version");
            Preference settingsLibrariesDialog = findPreference("settings_libraries_dialog");
            Preference settingsExport = findPreference("settings_export");
            Preference settingsImport = findPreference("settings_export");

            // If no shared preference found, use default values instead
            String defTheme = prefs.getString("prefs_app_theme", "system");
            String defNoteLayout = prefs.getString("prefs_note_layout", "list");

            ListPreference settingsAppTheme = findPreference("settings_app_theme");
            ListPreference settingsNoteLayout = findPreference("settings_note_layout");

            // Application easter egg :D
            assert settingsAppVersion != null;
            settingsAppVersion.setOnPreferenceClickListener(preference -> {
                easterEggCounter++;
                if (easterEggCounter == 5) {
                    Toast.makeText(requireContext(), getResources().getString(R.string.app_easter_egg), Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            assert settingsAppTheme != null;
            settingsAppTheme.setNegativeButtonText(""); // Removes negative button
            settingsAppTheme.setValue(defTheme);
            settingsAppTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                setTheme((String) newValue);
                return true;
            });

            assert settingsNoteLayout != null;
            settingsNoteLayout.setNegativeButtonText("");
            settingsNoteLayout.setValue(defNoteLayout);
            settingsNoteLayout.setOnPreferenceChangeListener((preference, newValue) -> {
                setDisplay((String) newValue);
                return true;
            });

            assert settingsLibrariesDialog != null;
            settingsLibrariesDialog.setOnPreferenceClickListener(preference -> {
                showLibrariesDialog();
                return true;
            });

            assert settingsExport != null;
            settingsExport.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                exportLauncher.launch(intent);
                return true;
            });

            assert settingsImport != null;
            settingsImport.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/json");
                importLauncher.launch(intent);
                return true;
            });
        }

        private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = (data != null) ? data.getData() : null;
                if (uri != null) {
                    exportNotes(uri);
                }
            }
        });

        private final ActivityResultLauncher<Intent> importLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = (data != null) ? data.getData() : null;
                if (uri != null) {
                    importNotes(uri);
                }
            }
        });

        private void importNotes(Uri uri) {
            try {
                @SuppressLint("Recycle")
                final InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    final StringBuilder builder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    final JSONArray jsonArray = new JSONArray(builder.toString());
                    final AppDatabase appDatabase = new AppDatabase(requireContext());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject object = jsonArray.getJSONObject(i);

                        final NoteModel note = new NoteModel();
                        note.setNoteTitle(object.getString(AppDatabase.COLUMN_NOTE_TITLE));
                        note.setNoteContent(object.getString(AppDatabase.COLUMN_NOTE_CONTENT));
                        note.setNoteDateCreated(object.getLong(AppDatabase.COLUMN_NOTE_DATE_CREATED));
                        note.setNoteLastUpdated(object.getLong(AppDatabase.COLUMN_NOTE_LAST_UPDATED));
                        note.setNoteState(object.getInt(AppDatabase.COLUMN_NOTE_STATE));
                        note.setIsFavorite(object.getInt(AppDatabase.COLUMN_NOTE_FAVORITE));
                        appDatabase.createNote(note);
                    }
                    Toast.makeText(requireContext(), "Import successful", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error: Import Failed", Toast.LENGTH_SHORT).show();
            }
        }

        private void exportNotes(Uri uri) {
            try {
                final AppDatabase appDatabase = new AppDatabase(requireContext());
                final JSONArray jsonArray = new JSONArray();
                final ArrayList<NoteModel> notes = appDatabase.readAllNotes();

                for (NoteModel note : notes) {
                    final JSONObject object = new JSONObject();
                    object.put(AppDatabase.COLUMN_NOTE_TITLE, note.getNoteTitle());
                    object.put(AppDatabase.COLUMN_NOTE_CONTENT, note.getNoteContent());
                    object.put(AppDatabase.COLUMN_NOTE_DATE_CREATED, note.getNoteDateCreated());
                    object.put(AppDatabase.COLUMN_NOTE_LAST_UPDATED, note.getNoteLastUpdated());
                    object.put(AppDatabase.COLUMN_NOTE_STATE, note.getNoteState());
                    object.put(AppDatabase.COLUMN_NOTE_FAVORITE, note.getIsFavorite());
                    jsonArray.put(object);
                }

                final DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext(), uri);

                final String FILE_NAME = "exported_notes.json";
                assert documentFile != null;
                final DocumentFile newFile = documentFile.createFile("application/json", FILE_NAME);

                if (newFile != null) {
                    @SuppressLint("Recycle")
                    final OutputStream outputStream = requireContext().getContentResolver().openOutputStream(newFile.getUri());
                    if (outputStream != null) {
                        outputStream.write(jsonArray.toString().getBytes());
                        outputStream.flush();
                        outputStream.close();
                        Toast.makeText(requireContext(), "Export successful", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error: Export Failed", Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint({"ResourceType", "SetTextI18n"})
        private void showLibrariesDialog() {
            final int colorInverted = getResources().getColor(R.color.primary_invert, requireContext().getTheme());

            @SuppressLint("InflateParams")
            View customDialogTitle = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom_title, null);

            TextView titleTV = customDialogTitle.findViewById(R.id.customDialogTitle);
            titleTV.setText("Libraries");

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            dialogBuilder.setCustomTitle(titleTV);
            dialogBuilder.setItems(R.array.libraries, (dialogInterface, i) -> linkLauncher(i));
            dialogBuilder.setPositiveButton("Back", (dialogInterface, i) -> dialogInterface.dismiss());

            AlertDialog librariesDialog = dialogBuilder.create();
            librariesDialog.show();

            WindowManager.LayoutParams layoutParams = Objects.requireNonNull(librariesDialog.getWindow()).getAttributes();
            layoutParams.width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.86);
            librariesDialog.getWindow().setAttributes(layoutParams);

            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable dialogBackground = requireContext().getResources().getDrawable(R.drawable.dialog, requireContext().getTheme());
            librariesDialog.getWindow().setWindowAnimations(0);
            librariesDialog.getWindow().setBackgroundDrawable(dialogBackground);

            librariesDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorInverted);
        }

        private void linkLauncher(int item) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            final String prettyTimeLink = getResources().getString(R.string.link_pretty_time);

            switch (item) {
                case 0:
                    intent.setData(Uri.parse(prettyTimeLink));
                    break;
            }
            startActivity(intent);
        }

        private void setDisplay(String display) {
            switch (display) {
                case "list":
                    prefs.setString("prefs_note_layout", "list");
                    break;
                case "grid":
                    prefs.setString("prefs_note_layout", "grid");
                    break;
            }
        }

        private void setTheme(String theme) {
            switch (theme) {
                case "system":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    prefs.setString("prefs_app_theme", "system");
                    break;
                case "light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefs.setString("prefs_app_theme", "light");
                    break;
                case "dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefs.setString("prefs_app_theme", "dark");
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}