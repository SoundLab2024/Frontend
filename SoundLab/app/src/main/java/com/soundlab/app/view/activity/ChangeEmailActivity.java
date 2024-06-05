package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Activity;
import android.content.Context;
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
import com.soundlab.app.presenter.api.response.UserPayload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import retrofit2.Retrofit;

public class ChangeEmailActivity extends AppCompatActivity {
    private EditText editTextOldEmail;
    private EditText editTextNewEmail;
    private EditText editTextConfirmEmail;
    private SharedPreferences sharedPreferences;
    private ApiService apiService;
    private String token;
    private UserController userController;
    private final Activity changeEmailActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        userController = new UserController();

        editTextOldEmail = findViewById(R.id.email_input);
        editTextNewEmail = findViewById(R.id.new_email_input);
        editTextConfirmEmail = findViewById(R.id.confirm_email);
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        token = sharedPreferences.getString("authToken", "");
        String email = sharedPreferences.getString(USER_EMAIL, null);

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        apiService = retrofit.create(ApiService.class);

        Button btnSave = findViewById(R.id.salvaButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldEmail.getText().toString();
                String newPassword = editTextNewEmail.getText().toString();
                String confirmPassword = editTextConfirmEmail.getText().toString();

                if (validateInputs(oldPassword, newPassword, confirmPassword)) {
                    //changeMail(email, oldPassword, newPassword);
                }
            }
        });
    }

    private boolean validateInputs(String oldEmail, String newEmail, String confirmEmail) {
        if (oldEmail.isEmpty() || newEmail.isEmpty() || confirmEmail.isEmpty()) {
            Toast.makeText(ChangeEmailActivity.this, "Per favore, riempi tutti i campi.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newEmail.equals(confirmEmail)) {
            Toast.makeText(ChangeEmailActivity.this, "L'email di conferma non corrisponde con la nuova.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /*private void changeMail(String email, String oldPassword, String newPassword) {
        userController.changeMail(token, email, oldPassword, newPassword, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {

                Toast.makeText(getApplicationContext(), "Email cambiata con successo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangeEmailActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(changeEmailActivity, errorMessage);
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}