package com.soundlab.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.soundlab.R;
import com.soundlab.app.view.activity.SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CambioUsernameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CambioUsernameFragment extends Fragment {

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

        Button salvaButton = view.findViewById(R.id.salva);

        // Aggiungi un listener al pulsante "Salva"
        salvaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                getActivity().finish(); // Chiude l'attività corrente
            }
        });

        return view;
    }
}