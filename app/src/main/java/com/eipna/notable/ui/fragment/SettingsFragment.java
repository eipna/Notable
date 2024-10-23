package com.eipna.notable.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.eipna.notable.R;
import com.eipna.notable.util.SharedPrefsUtil;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ListPreference listTheme;
    private SharedPrefsUtil sharedPrefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setPreferences();

        listTheme.setNegativeButtonText(""); // Removes negative button
        listTheme.setOnPreferenceChangeListener((preference, newValue) -> {
            setTheme((String) newValue);
            return true;
        });
    }

    private void setPreferences() {
        sharedPrefs = new SharedPrefsUtil(requireContext());
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