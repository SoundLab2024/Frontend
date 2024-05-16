package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.ALREADY_AUTH_KEY;
import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.soundlab.R;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.view.fragment.CambioUsernameFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsActivity extends AppCompatActivity {

    private String email;
    private String username;
    private String token;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
            editor.putBoolean(ALREADY_AUTH_KEY, false);
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
        // Controlla se il fragment corrente è il CambioUsernameFragment
        CambioUsernameFragment fragment = (CambioUsernameFragment) getSupportFragmentManager().findFragmentByTag("CambioUsernameFragment");
        if (fragment != null) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
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
        Log.d("SettingsActivity", "deleteAccount() chiamato");
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(USER_EMAIL, null);
        String token = sharedPreferences.getString(USER_TOKEN, null);

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.deleteUser(authToken, id);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    Log.d("SettingsActivity", "Account eliminato con successo dal backend");

                    // Rimuovi i dati dalle SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(USER_EMAIL);
                    editor.remove(USER_NAME);
                    editor.remove(USER_ROLE);
                    editor.remove(USER_TOKEN);
                    editor.apply();

                    // Ritorna alla schermata di login
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Log.d("SettingsActivity", "Errore nell'eliminazione dell'account: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                Log.d("SettingsActivity", "Errore nella chiamata API: ", t);
            }
        });
    }
}
