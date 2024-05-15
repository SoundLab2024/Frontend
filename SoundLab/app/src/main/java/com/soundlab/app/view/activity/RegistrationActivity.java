package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_PAS;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundlab.R;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.RegisterRequest;
import com.soundlab.app.presenter.api.response.UserPayload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import java.sql.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner SceltaGenderSpinner;
    private Button dataDiNascitaButton;
    private TextView dataNascitaText;
    private String scelta;
    private String dataStringa = "";
    private Date dataConverted;
    private EditText email_input, password_input, username_input;
    private String email, password, username;
    private static final String TAG = "REGISTER";

    // Variabile di controllo
    private boolean userSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dataDiNascitaButton = findViewById(R.id.datadinascita);
        dataNascitaText = findViewById(R.id.showText);
        dataDiNascitaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        SceltaGenderSpinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Seleziona, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SceltaGenderSpinner.setAdapter(adapter);
        SceltaGenderSpinner.setOnItemSelectedListener(this);

        Button continuaButton = findViewById(R.id.continua_button);

        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        username_input = findViewById(R.id.username_input);

        continuaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = email_input.getText().toString();
                password = password_input.getText().toString();
                username = username_input.getText().toString();

                if(controlloCampi(email, password, username, scelta, dataStringa)){

                    Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
                    ApiService apiService = retrofit.create(ApiService.class);
                    RegisterRequest registerRequest = new RegisterRequest(email, password, username, scelta, dataConverted);
                    Log.d(TAG, registerRequest.getBirth().toString());

                    Call<UserPayload> call = apiService.registerUser(registerRequest);
                    call.enqueue(new Callback<UserPayload>() {
                        @Override
                        public void onResponse(Call<UserPayload> call, Response<UserPayload> response) {
                            if (response.isSuccessful()) {
                                // Registrazione riuscita, prendiamo il body dalla risposta
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
                                editor.apply();

                                // Vado col popup
                                showPopup();

                            } else {
                                // Gestisci la risposta di errore, es. credenziali non valide
                                Toast.makeText(RegistrationActivity.this, "Login fallito, riprova.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserPayload> call, Throwable t) {
                            // Gestisci l'errore di rete o la conversione della risposta qui
                            Log.d(TAG, "Richiesta fallita.");
                        }
                    });

                }

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

        Button nextButton = popupView.findViewById(R.id.indietro_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss(); // Chiudere il popup
                goToHome();
            }
        });

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anno, int mese, int giorno) {
                dataStringa = String.valueOf(anno) + "-" + String.valueOf(mese + 1) + "-" + String.valueOf(giorno);
                dataConverted = Date.valueOf(dataStringa);

                Log.d("DATA", dataStringa);
                Log.d("DATA SQL", String.valueOf(dataConverted));
                dataNascitaText.setText(dataStringa);
                dataDiNascitaButton.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                dataDiNascitaButton.setText("Modifica data");
            }
        }, 1985, 0, 15);
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        scelta = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(getApplicationContext(), scelta, Toast.LENGTH_LONG).show();
        Log.d("GENDER", scelta);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Questo metodo è chiamato quando nessun elemento è selezionato nello spinner
    }

    // Validazione dei campi
    private boolean controlloCampi(String email, String password, String username, String scelta, String dataStringa){
        if((email.isEmpty()) || (password.isEmpty()) || (username.isEmpty()) || (scelta.isEmpty()) || (dataStringa.isEmpty())) {
            Toast.makeText(this, "Tutti i campi sono obbligatori, riempili!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}

