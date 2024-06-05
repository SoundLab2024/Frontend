package com.soundlab.app.view.fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.UserController;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.view.activity.ChangePasswordActivity;
import com.soundlab.app.view.activity.SettingsActivity;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CambioUsernameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CambioUsernameFragment extends Fragment {
    private EditText usernameEditText;
    private SharedPreferences sharedPreferences;
    private ApiService apiService;
    private String token;
    private UserController userController;
    private final Fragment cambioUsernameFragment = this;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CambioUsernameFragment() {
        // Required empty public constructor
    }

    public static CambioUsernameFragment newInstance(String param1, String param2) {
        CambioUsernameFragment fragment = new CambioUsernameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // This callback will handle the back button event
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Avvia l'Intent per la SettingsActivity
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                getActivity().finish(); // Chiude l'attività corrente
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cambio_foto, container, false);

        userController = new UserController();

        usernameEditText = view.findViewById(R.id.usernameEditText);
        sharedPreferences = getActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE);

        token = sharedPreferences.getString("authToken", "");
        String email = sharedPreferences.getString(USER_EMAIL, null);
        //String username = sharedPreferences.getString(USER_NAME, null);

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        apiService = retrofit.create(ApiService.class);

        Button salvaButton = view.findViewById(R.id.salva);

        // Aggiungi un listener al pulsante "Salva"
        salvaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = usernameEditText.getText().toString();
                String oldPassword = sharedPreferences.getString(USER_NAME, null);

                if (validateInputs(newPassword)) {
                    changeUsername(email, newPassword, oldPassword);
                }
            }
        });

        return view;
    }

    private boolean validateInputs(String newPassword) {
        if (newPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Per favore, riempi tutti i campi.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void changeUsername(String email, String newPassword, String oldPassword) {
        userController.changeUsername(token, email, oldPassword, newPassword, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                Toast.makeText(getActivity(), "Username cambiato con successo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(cambioUsernameFragment, errorMessage);
            }
        });
    }
}
