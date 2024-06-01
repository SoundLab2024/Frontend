package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.UserController;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import retrofit2.Retrofit;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    private SharedPreferences sharedPreferences;
    private ApiService apiService;
    private String token;
    private UserController userController;
    private final Activity changePasswordActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userController = new UserController();

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
                }
            }
        });
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Per favore, riempi tutti i campi.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!newPassword.equals(confirmPassword)){
            Toast.makeText(ChangePasswordActivity.this, "La password di conferma non corrisponde con la nuova.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    private void changePassword(String email, String oldPassword, String newPassword) {
        userController.changePassword(token, email, oldPassword, newPassword, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                Toast.makeText(getApplicationContext(), "Password cambiata con successo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(changePasswordActivity, errorMessage);
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
