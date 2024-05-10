package com.soundlab.app.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soundlab.R;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_RUN_KEY = "first_run";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (sharedPrefs.getBoolean(FIRST_RUN_KEY, true)) {
            // Se è il primo avvio, imposta il flag a false e avvia la SplashActivity
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(FIRST_RUN_KEY, false);
            editor.apply();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, SlideActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        } else {
            // Se non è il primo avvio, avvia direttamente la MainActivity
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        }
    }
}
