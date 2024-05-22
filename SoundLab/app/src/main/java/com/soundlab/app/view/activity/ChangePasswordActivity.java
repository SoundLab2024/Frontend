package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundlab.R;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.ChangePasswordRequest;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    private SharedPreferences sharedPreferences;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextOldPassword = findViewById(R.id.password_input);
        editTextNewPassword = findViewById(R.id.new_password_input);
        editTextConfirmPassword = findViewById(R.id.confirm_password_input);
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        token = sharedPreferences.getString("authToken", "");
        String email = sharedPreferences.getString(USER_EMAIL, null);

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        apiService = retrofit.create(ApiService.class);

        Button btnSave = findViewById(R.id.salvaButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (validateInputs(oldPassword, newPassword, confirmPassword)) {
                    changePassword(email, oldPassword, newPassword);
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Per favore, riempi tutti i campi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmPassword) {
        return !oldPassword.isEmpty() && !newPassword.isEmpty() && newPassword.equals(confirmPassword);
    }

    private void changePassword(String email, String oldPassword, String newPassword) {
        Log.d("ChangePassword", "ChangePassword chiamato");

        String authToken = "Bearer " + token;
        ChangePasswordRequest request = new ChangePasswordRequest(email, oldPassword, newPassword);
        Call<Payload> call = apiService.changePw(authToken, request);

        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(Call<Payload> call, Response<Payload> response) {
                if (response.isSuccessful()) {
                    Log.d("ChangePassword", "Password cambiata con successo");
                    Toast.makeText(ChangePasswordActivity.this, "Password cambiata con successo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Errore nel cambiare la password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payload> call, Throwable t) {
                Log.d("ChangePasswordActivity", "Errore", t);
                Toast.makeText(ChangePasswordActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
