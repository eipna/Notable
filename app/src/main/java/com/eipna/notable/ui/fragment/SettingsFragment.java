package com.eipna.notable.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.eipna.notable.R;
import com.eipna.notable.util.SharedPrefsUtil;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ListPreference listTheme;
    private ListPreference listDisplay;
    private Preference prefsVersion;
    private Preference prefsLibraries;
    private SharedPrefsUtil sharedPrefs;

    private int easterEggCounter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setPreferences();

        // Little easter egg :D
        prefsVersion.setOnPreferenceClickListener(preference -> {
            easterEggCounter++;
            if (easterEggCounter == 5) {
                Toast.makeText(requireContext(), getResources().getString(R.string.app_easter_egg), Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        listTheme.setNegativeButtonText(""); // Removes negative button
        listTheme.setOnPreferenceChangeListener((preference, newValue) -> {
            setTheme((String) newValue);
            return true;
        });

        listDisplay.setNegativeButtonText("");
        listDisplay.setOnPreferenceChangeListener((preference, newValue) -> {
            setDisplay((String) newValue);
            return true;
        });

        prefsLibraries.setOnPreferenceClickListener(preference -> {
            showLibrariesDialog();
            return true;
        });
    }

    private void showLibrariesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("Libraries")
                .setItems(R.array.libraries, (dialogInterface, item) -> linkLauncher(item));

        AlertDialog librariesDialog = builder.create();
        librariesDialog.setOnShowListener(dialogInterface -> {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable popupMenuBG = getResources().getDrawable(R.drawable.popup_menu, requireContext().getTheme());
            Objects.requireNonNull(librariesDialog.getWindow()).setBackgroundDrawable(popupMenuBG);
        });
        librariesDialog.show();
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

    private void setPreferences() {
        sharedPrefs = new SharedPrefsUtil(requireContext());

        prefsVersion = findPreference("prefs_version");
        prefsLibraries = findPreference("prefs_libraries");

        listTheme = findPreference("list_theme");
        listDisplay = findPreference("list_display");
    }

    private void setDisplay(String display) {
        switch (display) {
            case "list":
                sharedPrefs.setString("DISPLAY", "list");
                break;
            case "grid":
                sharedPrefs.setString("DISPLAY", "grid");
                break;
        }
    }

    private void setTheme(String theme) {
        switch (theme) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                sharedPrefs.setString("THEME", "system");
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPrefs.setString("THEME", "light");
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPrefs.setString("THEME", "dark");
                break;
        }
    }
}