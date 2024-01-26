package view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.soundlab.R;
import view.fragment.CambioFotoFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView logout = findViewById(R.id.logout);
        TextView cambiaEmail = findViewById(R.id.cambio_email);
        TextView cambioPassword = findViewById(R.id.cambio_password);
        TextView modificaButton = findViewById(R.id.modifica);

        modificaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creazione di un'istanza del fragment desiderato
                CambioFotoFragment cambioFotoFragment = new CambioFotoFragment();

                // Ottieni il FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Inizia una transazione fragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Sostituisci il fragment corrente con il nuovo fragment
                fragmentTransaction.replace(android.R.id.content, cambioFotoFragment, "cambio_foto_fragment_tag");

                // Aggiungi la transazione al back stack
                fragmentTransaction.addToBackStack(null);

                // Esegui la transazione
                fragmentTransaction.commit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cambiaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        cambioPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}






