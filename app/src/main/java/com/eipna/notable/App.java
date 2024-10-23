package com.eipna.notable;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.eipna.notable.util.SharedPrefsUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPrefsUtil sharedPrefs = new SharedPrefsUtil(App.this);

        String themePrefs = sharedPrefs.getString("THEME", "system");
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
        }
    }
}