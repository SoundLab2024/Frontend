package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.UserController;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.view.fragment.CambioUsernameFragment;

public class SettingsActivity extends AppCompatActivity {

    private String email;
    private String username;
    private String token;
    private String role;
    private UserController userController;
    private final Activity settingsActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userController = new UserController();

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        role = sharedPreferences.getString(USER_ROLE, null);
        email = sharedPreferences.getString(USER_EMAIL, null);
        username = sharedPreferences.getString(USER_NAME, null);
        token = sharedPreferences.getString(USER_TOKEN, null);

        TextView account = findViewById(R.id.account);
        TextView modificaEmail = findViewById(R.id.modificaEmail);
        TextView modificaPassword = findViewById(R.id.modificaPassword);
        TextView logout = findViewById(R.id.logout);
        TextView cancellaAccount = findViewById(R.id.cancellaAccount);

        // Campo per modificare l'email
        modificaEmail.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
        });

        // Campo per modificare la password
        modificaPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Campo per effettuare il logout
        logout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            SharedPreferences sharedPrefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.apply();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Cancellazione di un utente
        cancellaAccount.setOnClickListener(v -> showDeleteAccountPopup());

        // Cambio username
        account.setOnClickListener(view -> {
            CambioUsernameFragment cambioUsernameFragment = new CambioUsernameFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, cambioUsernameFragment, "cambio_username_fragment_tag")
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("cambio_username_fragment_tag");
        if (fragment != null && fragment.isVisible()) {
            // Torna al fragment precedente nel back stack
            getSupportFragmentManager().popBackStack();
        } else {
            // Se non ci sono fragment nel back stack, torna alla HomePageActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Chiude l'attività corrente
        }
    }


    private void showDeleteAccountPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_cancella_utente, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        Button annullaButton = popupView.findViewById(R.id.annulla_button);
        Button continuaButton = popupView.findViewById(R.id.continua_button);

        annullaButton.setOnClickListener(view -> popupWindow.dismiss());

        continuaButton.setOnClickListener(view -> {
            Log.d("Popup mostrato:", "Continua button clicked");
            deleteAccount();
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(USER_EMAIL, null);
        String token = sharedPreferences.getString(USER_TOKEN, null);

        userController.deleteUser(email, token, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Ritorna alla schermata di login
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(settingsActivity, errorMessage);
            }
        });

    }
}
