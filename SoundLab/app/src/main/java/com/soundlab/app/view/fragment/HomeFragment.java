package com.soundlab.app.view.fragment;

import android.os.Bundle;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.soundlab.R;
import com.soundlab.app.utils.Utilities;
import android.content.SharedPreferences;
import android.widget.TextView;

import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_ROLE;


public class HomeFragment extends Fragment {

    private String userName;
    private TextView welcomeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflater per trovare gli elementi
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HomeFragment", "onCreateView called");

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_NAME, null);

        welcomeView = view.findViewById(R.id.welcome);
        welcomeView.setText("Benvenuto, " + userName);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);
    }
}