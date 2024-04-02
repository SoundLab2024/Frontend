package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soundlab.app.model.User;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.UserFromTokenResponse;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.fragment.HomeFragment;
import com.soundlab.app.view.fragment.ProfileFragment;

import com.example.soundlab.R;

import com.soundlab.app.view.fragment.SearchFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "MAIN";

    private String email;
    private String username;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Per recuperare il token e l'utente
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String tokenDevice = sharedPreferences.getString("AuthToken", null); //null è il valore di default se il token non esiste
        Log.d(TAG, tokenDevice);
        String token = "Bearer " + tokenDevice;

        // Metodo che contiene la chiamata al retrieve dell'utente
        retrieveUser(token, tokenDevice);

        User u = new User(email, username, role);

        //TODO Implementare chiamata di retrieve della Libreria

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new HomeFragment(), Utilities.homeFragmentTag);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            String currentfragmentTag = getTopFragmentTag();

            if (item.getItemId() == R.id.home && !Objects.equals(currentfragmentTag, Utilities.homeFragmentTag)) {
                replaceFragment(new HomeFragment(), Utilities.homeFragmentTag);
            } else if (item.getItemId() == R.id.search && !Objects.equals(currentfragmentTag, Utilities.searchFragmentTag)) {
                replaceFragment(new SearchFragment(), Utilities.searchFragmentTag);
            } else if (item.getItemId() == R.id.profile && !Objects.equals(currentfragmentTag, Utilities.profileFragmentTag)) {
                replaceFragment(new ProfileFragment(), Utilities.profileFragmentTag);
            }

            return true;
        });

    }

    private void retrieveUser(String token, String tokenDevice) {

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        Call<UserFromTokenResponse> call = apiService.userToken(token, tokenDevice);
        call.enqueue(new Callback<UserFromTokenResponse>() {
            @Override
            public void onResponse(Call<UserFromTokenResponse> call, Response<UserFromTokenResponse> response) {
                if (response.isSuccessful()) {
                    // Riuscito, prendiamo il body dalla risposta
                    UserFromTokenResponse payload = response.body();

                    // Gestiamo le risposte del body
                    email = payload.getEmail();
                    username = payload.getUsername();
                    role = payload.getRole();
                    Log.d(TAG, email + " "+ username + " " + role);


                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(MainActivity.this, "Utente non recuperato.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserFromTokenResponse> call, Throwable t) {
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d(TAG, "Richiesta fallita.");
            }
        });

    }

    @Override
    public void onBackPressed() {
        String currentfragmentTag = getTopFragmentTag();

        if (Objects.equals(currentfragmentTag, Utilities.searchFragmentTag) || Objects.equals(currentfragmentTag, Utilities.profileFragmentTag)) {

            // Seleziono l'item home nella bottomNavigationView viene invocato quindi il listener(setOnItemSelectedListener) che chiama replaceFragment
            selectRightItemBottomNavView(Utilities.homeFragmentTag);

        } else if (Objects.equals(currentfragmentTag, Utilities.homeFragmentTag)) {
            this.finish();

        } else if (Objects.equals(currentfragmentTag, Utilities.playlistFragmentTag)) {
            showBottomNavigationView();
            replaceFragment(new ProfileFragment(), Utilities.profileFragmentTag);

        } else {
            super.onBackPressed();
        }
    }

    /**
     * Ottiene il tag del fragment in cima allo stack.
     *
     * @return Il tag del fragment in cima allo stack o null se lo stack è vuoto.
     */
    private String getTopFragmentTag() {
        // Ottiene il gestore dei fragment per l'attività corrente
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Ottiene il numero di fragment nello stack
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        // Verifica se ci sono fragment nello stack
        if (backStackEntryCount > 0) {
            // Ottiene l'entry del fragment in cima allo stack
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backStackEntryCount - 1);

            // Restituisce il tag del fragment in cima allo stack
            return backStackEntry.getName();
        }
        // Restituisce null se lo stack è vuoto
        return null;
    }


    public void hideBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    public void showBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    public void selectRightItemBottomNavView(String fragmentTag) {
        if (Objects.equals(fragmentTag, Utilities.homeFragmentTag)) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
        else if (Objects.equals(fragmentTag, Utilities.searchFragmentTag)){
            bottomNavigationView.setSelectedItemId(R.id.search);
        }
        else if (Objects.equals(fragmentTag, Utilities.profileFragmentTag)) {
            bottomNavigationView.setSelectedItemId(R.id.profile);
        }
    }


    /**
     * Rimpiazza il fragment attuale con quello passato per parametro
     *
     * @param fragment Fragment
     */
    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_fragments, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void replaceFragmentWithoutPopStack(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_fragments, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}