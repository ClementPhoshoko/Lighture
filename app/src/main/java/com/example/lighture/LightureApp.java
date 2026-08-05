package com.example.lighture;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class LightureApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Light mode by default. When the theme switch setting is implemented,
        // read the stored mode here instead of hardcoding it.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
