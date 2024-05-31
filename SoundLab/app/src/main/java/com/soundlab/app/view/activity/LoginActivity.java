package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.ALREADY_AUTH_KEY;
import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_PAS;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soundlab.R;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.response.UserPayload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.utils.Debouncer;
import com.soundlab.app.view.CustomButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity {

    private CustomButton login;
    private CustomButton signup;
    private EditText email_input, password_input;
    private String email, password;
    private static final String TAG = "LOGIN";
    private final Debouncer debouncer = new Debouncer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUI();
    }

    private void setUI() {
        login = findViewById(R.id.login_button);
        signup = findViewById(R.id.signup_button);

        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);

        signup.setOnClickListener(view -> {
            Intent intent= new Intent(LoginActivity.this, RegistrationActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        });


        login.setOnClickListener(view -> {
            email = email_input.getText().toString();
            password = password_input.getText().toString();

            if(controlloCampi(email, password)){
                debouncer.debounce(this::callLoginUser, 800);
            }

        });
    }

    private void callLoginUser() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<UserPayload> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<UserPayload>() {
            @Override
            public void onResponse(@NonNull Call<UserPayload> call, @NonNull Response<UserPayload> response) {
                if (response.isSuccessful()) {
                    // Login riuscito, prendiamo il body dalla risposta
                    UserPayload payload = response.body();

                    // Per salvare l'utente
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_TOKEN, payload.getToken());
                    editor.putString(USER_NAME, payload.getUsername());
                    editor.putString(USER_EMAIL, payload.getEmail());
                    editor.putString(USER_ROLE, payload.getRole());
                    editor.putLong(USER_LIB, payload.getLibraryId());
                    editor.putString(USER_PAS, password);
                    editor.putBoolean(ALREADY_AUTH_KEY, true);
                    editor.apply();

                    // Vado avanti...
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();

                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(LoginActivity.this, "Login fallito, riprova.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPayload> call, @NonNull Throwable t) {
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d(TAG, "Richiesta fallita.");
            }
        });

    }

    // Validazione dei campi
    private boolean controlloCampi(String email, String password){
        if((email.isEmpty()) || (password.isEmpty())) {
            Toast.makeText(LoginActivity.this, "I campi Email e Password sono obbligatori.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}