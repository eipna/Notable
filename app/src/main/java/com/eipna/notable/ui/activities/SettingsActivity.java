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
import androidx.preference.SwitchPreferenceCompat;

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
import com.eipna.notable.constants.AppTheme;
import com.eipna.notable.constants.NoteList;
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
            Preference settingsImport = findPreference("settings_import");

            SwitchPreferenceCompat settingsRoundedNotes = findPreference("settings_rounded_notes");

            // If no shared preference found, use default values instead
            String defTheme = prefs.getString("prefs_app_theme", AppTheme.SYSTEM_MODE.getValue());
            String defNoteLayout = prefs.getString("prefs_note_layout", NoteList.LIST.getValue());
            boolean defRoundedNotes = prefs.getBoolean("prefs_rounded_notes", true);

            ListPreference settingsAppTheme = findPreference("settings_app_theme");
            ListPreference settingsNoteLayout = findPreference("settings_note_layout");

            settingsRoundedNotes.setChecked(defRoundedNotes);
            settingsRoundedNotes.setOnPreferenceChangeListener((preference, newValue) -> {
                prefs.setBoolean("prefs_rounded_notes", (boolean) newValue);
                return true;
            });

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
                setThemePrefs((String) newValue);
                return true;
            });

            assert settingsNoteLayout != null;
            settingsNoteLayout.setNegativeButtonText("");
            settingsNoteLayout.setValue(defNoteLayout);
            settingsNoteLayout.setOnPreferenceChangeListener((preference, newValue) -> {
                setListPrefs((String) newValue);
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
                        note.setTitle(object.getString(AppDatabase.COLUMN_NOTE_TITLE));
                        note.setContent(object.getString(AppDatabase.COLUMN_NOTE_CONTENT));
                        note.setDateCreated(object.getLong(AppDatabase.COLUMN_NOTE_DATE_CREATED));
                        note.setLastUpdated(object.getLong(AppDatabase.COLUMN_NOTE_LAST_UPDATED));
                        note.setState(object.getInt(AppDatabase.COLUMN_NOTE_STATE));
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
                final ArrayList<NoteModel> notes = appDatabase.getAllNotes();

                for (NoteModel note : notes) {
                    final JSONObject object = new JSONObject();
                    object.put(AppDatabase.COLUMN_NOTE_TITLE, note.getTitle());
                    object.put(AppDatabase.COLUMN_NOTE_CONTENT, note.getContent());
                    object.put(AppDatabase.COLUMN_NOTE_DATE_CREATED, note.getDateCreated());
                    object.put(AppDatabase.COLUMN_NOTE_LAST_UPDATED, note.getLastUpdated());
                    object.put(AppDatabase.COLUMN_NOTE_STATE, note.getState());
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
            dialogBuilder.setItems(R.array.libraries, (dialogInterface, i) -> linkOpener(i));
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

        private void linkOpener(int item) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            final String prettyTimeLink = getResources().getString(R.string.link_pretty_time);

            if (item == 0) {
                intent.setData(Uri.parse(prettyTimeLink));
            }
            startActivity(intent);
        }

        private void setListPrefs(String selectedDisplay) {
            if (selectedDisplay.equals(NoteList.LIST.getValue())) {
                prefs.setString("prefs_note_layout", NoteList.LIST.getValue());
            } else {
                prefs.setString("prefs_note_layout", NoteList.GRID.getValue());
            }
        }

        private void setThemePrefs(String selectedTheme) {
            if (selectedTheme.equals(AppTheme.SYSTEM_MODE.getValue())) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                prefs.setString("prefs_app_theme", AppTheme.SYSTEM_MODE.getValue());
            } else if (selectedTheme.equals(AppTheme.LIGHT_MODE.getValue())) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefs.setString("prefs_app_theme", AppTheme.LIGHT_MODE.getValue());
            } else if (selectedTheme.equals(AppTheme.DARK_MODE.getValue())) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefs.setString("prefs_app_theme", AppTheme.DARK_MODE.getValue());
            } else if (selectedTheme.equals(AppTheme.BATTERY_MODE.getValue())) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                prefs.setString("prefs_app_theme", AppTheme.BATTERY_MODE.getValue());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}