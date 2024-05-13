package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.ALREADY_AUTH_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.soundlab.R;
import com.soundlab.app.view.fragment.CambioFotoFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView account = findViewById(R.id.account);
        TextView modificaEmail = findViewById(R.id.modificaEmail);
        TextView modificaPassword = findViewById(R.id.modificaPassword);
        TextView logout = findViewById(R.id.logout);
        TextView cancellaAccount = findViewById(R.id.cancellaAccount);

        modificaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'activity ChangeEmailActivity
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        modificaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'activity ChangePasswordActivity
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'activity LoginActivity per il logout
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                SharedPreferences sharedPrefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean(ALREADY_AUTH_KEY, false);
                editor.apply();
                // Imposta il flag per eliminare tutte le activity precedenti dallo stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        cancellaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'activity SplashActivity per cancellare l'account
                Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                // Imposta il flag per eliminare tutte le activity precedenti dallo stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CambioFotoFragment cambioFotoFragment = new CambioFotoFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(android.R.id.content,cambioFotoFragment,"cambio_foto_fragment_tag");
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();
            }
        });
    }

}