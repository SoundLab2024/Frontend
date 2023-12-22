package view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.example.soundlab.R;
import view.CustomButton;

public class LoginActivity extends AppCompatActivity {

    private CustomButton login;
    private CustomButton signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login_button);
        signup = findViewById(R.id.signup_button);

        signup.setOnClickListener(view -> {
            Intent intent= new Intent(LoginActivity.this, RegistrationActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        });

        login.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        });

    }

}