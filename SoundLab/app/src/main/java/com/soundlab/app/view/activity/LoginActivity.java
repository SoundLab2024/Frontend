package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.ALREADY_AUTH_KEY;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_PAS;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundlab.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.UserController;
import com.soundlab.app.presenter.api.response.UserPayload;
import com.soundlab.app.utils.Debouncer;
import com.soundlab.app.view.CustomButton;


public class LoginActivity extends AppCompatActivity {

    private CustomButton login;
    private CustomButton signup;
    private EditText email_input, password_input;
    private String email, password;
    private final Debouncer debouncer = new Debouncer();
    private UserController userController;
    private final LoginActivity loginActivity = this;

    private FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALREADY_AUTH_KEY, false);
        editor.apply();

        userController = new UserController();

        // test per firebase
        Bundle bundle = new Bundle();
        bundle.putString("id", "1");
        bundle.putString("testo", "TestLog");
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");


        mFirebaseAnalytics.logEvent("event_test", bundle);

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
                debouncer.debounce(() -> callLoginUser(email, password), 800);
            }

        });
    }

    private void callLoginUser(String email, String password) {
        userController.login(email, password, new ControllerCallback<UserPayload>() {
            @Override
            public void onSuccess(UserPayload payload) {
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

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(loginActivity, errorMessage);
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