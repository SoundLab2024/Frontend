package view.activity;

import static utils.Constants.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soundlab.R;

import presenter.api.endpoint.ApiService;
import presenter.api.request.LoginRequest;
import presenter.api.response.LoginResponse;
import presenter.api.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import utils.Constants;
import view.CustomButton;

public class LoginActivity extends AppCompatActivity {

    private CustomButton login;
    private CustomButton signup;
    private EditText email_input, password_input;
    private String email, password;

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

            //da eliminare
            email = "email@mail.com";
            password = "password";

            if(controlloCampi(email, password)){

                Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
                ApiService apiService = retrofit.create(ApiService.class);
                LoginRequest loginRequest = new LoginRequest(email, password);

                Call<LoginResponse> call = apiService.loginUser(loginRequest);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            // Login riuscito, prendiamo il body dalla risposta
                            LoginResponse loginResponse = response.body();

                            //gestiamo le risposte del body

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
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // Gestisci l'errore di rete o la conversione della risposta qui
                    }
                });

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