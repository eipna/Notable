package com.eipna.notable;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.eipna.notable.constants.AppTheme;
import com.eipna.notable.utils.SharedPrefsUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String defAppTheme = new SharedPrefsUtil(this).getString("prefs_app_theme", AppTheme.SYSTEM_MODE.getValue());
        if (defAppTheme.equals(AppTheme.SYSTEM_MODE.getValue())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (defAppTheme.equals(AppTheme.LIGHT_MODE.getValue())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (defAppTheme.equals(AppTheme.DARK_MODE.getValue())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (defAppTheme.equals(AppTheme.BATTERY_MODE.getValue())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
    }
}