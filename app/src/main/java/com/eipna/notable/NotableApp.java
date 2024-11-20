package com.eipna.notable;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.eipna.notable.utils.SharedPrefsUtil;

public class NotableApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefsUtil sharedPrefs = new SharedPrefsUtil(NotableApp.this);
        String themePrefs = sharedPrefs.getString("prefs_app_theme", "system");
        switch (themePrefs) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "battery_saving":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
        }
    }
}