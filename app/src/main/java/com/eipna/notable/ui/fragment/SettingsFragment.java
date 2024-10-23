package com.eipna.notable.ui.fragment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.eipna.notable.R;
import com.eipna.notable.util.SharedPrefsUtil;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ListPreference listTheme;
    private Preference prefsVersion;
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
    }

    private void setPreferences() {
        sharedPrefs = new SharedPrefsUtil(requireContext());
        prefsVersion = findPreference("prefs_version");
        listTheme = findPreference("list_theme");
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