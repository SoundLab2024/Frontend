package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.ALREADY_AUTH_KEY;
import static com.soundlab.app.utils.Constants.FIRST_RUN_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soundlab.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPrefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        if (sharedPrefs.getBoolean(FIRST_RUN_KEY, true)) {
            // Se è il primo avvio, imposta il flag a false e avvia la SplashActivity
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(FIRST_RUN_KEY, false);
            editor.apply();
            openActivity(SlideActivity.class);
        } else {
            if (!sharedPrefs.getBoolean(ALREADY_AUTH_KEY, false)) {
                openActivity(LoginActivity.class);
            } else {
                openActivity(MainActivity.class);
            }
        }
    }

    private void openActivity(Class<?> activityClass) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, activityClass);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
