package view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundlab.R;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private Button datadinascita;
    private TextView text;

    // Variabile di controllo
    private boolean userSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        datadinascita = findViewById(R.id.datadinascita);
        text = findViewById(R.id.showText);
        datadinascita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Seleziona, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        Button continuaButton = findViewById(R.id.continua_button);
        continuaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });
    }

    private void showPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.mainpopup, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // TextView popupText = popupView.findViewById(R.id.popupText);
        Button indietroButton = popupView.findViewById(R.id.indietro_button);

        indietroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss(); // Chiudere il popup
                goToLoginActivity();
            }
        });

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
                text.setText(String.valueOf(anno) + "." + String.valueOf(mese + 1) + "." + String.valueOf(giorno));
                datadinascita.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                datadinascita.setText("Modifica data");
            }
        }, 2022, 0, 15);
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (userSelected) {
            String scelta = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(getApplicationContext(), scelta, Toast.LENGTH_LONG).show();
        } else {
            userSelected = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Questo metodo è chiamato quando nessun elemento è selezionato nello spinner
    }
}

