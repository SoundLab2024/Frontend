package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.ALREADY_AUTH_KEY;
import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_PAS;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.soundlab.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soundlab.app.model.User;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.response.UserPayload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.service.PlayerService;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.fragment.HomeFragment;
import com.soundlab.app.view.fragment.PlayerFragment;
import com.soundlab.app.view.fragment.ProfileFragment;
import com.soundlab.app.view.fragment.SearchFragment;

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
    private String token;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askNotificationPermission();

        // Per recuperare l'utente
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);
        email = sharedPreferences.getString(USER_EMAIL, null);
        username = sharedPreferences.getString(USER_NAME, null);
        role = sharedPreferences.getString(USER_ROLE, null);
        password = sharedPreferences.getString(USER_PAS, null);

        Log.d(TAG, "UTENTE:" + email);
        Log.d(TAG, "UTENTE:" + password);

        refreshToken();

        User u = new User(email, username, role);

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

        if (getIntent() != null && getIntent().hasExtra("openPlayerFragment")) {
            loadPlayerFragment();
        }
    }

    private void refreshToken() {

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<UserPayload> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<UserPayload>() {
            @Override
            public void onResponse(Call<UserPayload> call, Response<UserPayload> response) {
                if (response.isSuccessful()) {
                    // Login riuscito, prendiamo il body dalla risposta
                    UserPayload payload = response.body();

                    // Per salvare l'utente
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_TOKEN, payload.getToken());
                    editor.putString(USER_NAME, payload.getUsername());
                    editor.putString(USER_EMAIL, payload.getEmail());
                    editor.putString(USER_ROLE, payload.getRole());
                    editor.putLong(USER_LIB, payload.getLibraryId());
                    editor.putBoolean(ALREADY_AUTH_KEY, true);
                    editor.apply();


                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(MainActivity.this, "Login fallito, riprova.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPayload> call, Throwable t) {
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d(TAG, "Richiesta fallita.");
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("openPlayerFragment")) {
            String currentfragmentTag = getTopFragmentTag();
            String previousFragmentTag = getPreviousFragmentTag();
            if (!Objects.equals(currentfragmentTag, Utilities.playerFragmentTag)) {

                if (Objects.equals(currentfragmentTag, Utilities.addToPlaylistFragmentTag) && Objects.equals(previousFragmentTag, Utilities.playerFragmentTag)) {
                    getSupportFragmentManager().popBackStackImmediate();
                    getSupportFragmentManager().popBackStackImmediate();
                } else if (Objects.equals(currentfragmentTag, Utilities.addToPlaylistFragmentTag)) {
                    getSupportFragmentManager().popBackStackImmediate();
                }

                loadPlayerFragment();
            }
        }
    }

    private void loadPlayerFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("avoidServiceRestart", true);

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(bundle);

        replaceFragmentWithoutPopStack(playerFragment, Utilities.playerFragmentTag);
    }

    @Override
    public void onBackPressed() {
        String currentfragmentTag = getTopFragmentTag();
        String previousFragmentTag = getPreviousFragmentTag();
        Log.d(TAG, "Numero di fragment nello stack: " + getSupportFragmentManager().getBackStackEntryCount());

        if (Objects.equals(currentfragmentTag, Utilities.searchFragmentTag) || Objects.equals(currentfragmentTag, Utilities.profileFragmentTag)) {
            // Seleziono l'item home nella bottomNavigationView viene invocato quindi il listener(setOnItemSelectedListener) che chiama replaceFragment
            selectRightItemBottomNavView(Utilities.homeFragmentTag);

        } else if (Objects.equals(currentfragmentTag, Utilities.homeFragmentTag)) {
            this.finish();

        } else if (Objects.equals(currentfragmentTag, Utilities.playlistFragmentTag)) {
            showBottomNavigationView();
            super.onBackPressed();
            //selectRightItemBottomNavView(Utilities.profileFragmentTag);

        } else if (Objects.equals(currentfragmentTag, Utilities.playerFragmentTag)) {

            if (Objects.equals(previousFragmentTag, Utilities.searchFragmentTag)) {
                showBottomNavigationView();
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                selectRightItemBottomNavView(Utilities.searchFragmentTag);
            } else if (Objects.equals(previousFragmentTag, Utilities.homeFragmentTag)) {
                showBottomNavigationView();
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                selectRightItemBottomNavView(Utilities.homeFragmentTag);
            } else if (Objects.equals(previousFragmentTag, Utilities.profileFragmentTag)) {
                showBottomNavigationView();
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                selectRightItemBottomNavView(Utilities.profileFragmentTag);
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }

    // Metodo per ottenere il tag del fragment immediatamente sotto il fragment corrente
    private String getPreviousFragmentTag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        if (backStackEntryCount > 1) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backStackEntryCount - 2);
            return backStackEntry.getName();
        }

        return null;
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
        } else if (Objects.equals(fragmentTag, Utilities.searchFragmentTag)) {
            bottomNavigationView.setSelectedItemId(R.id.search);
        } else if (Objects.equals(fragmentTag, Utilities.profileFragmentTag)) {
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });


    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment playerFragment = fragmentManager.findFragmentByTag(Utilities.playerFragmentTag);
        if (playerFragment != null && playerFragment.isAdded()) {
            fragmentManager.beginTransaction().remove(playerFragment).commitAllowingStateLoss();
        }
        Intent intent = new Intent(this, PlayerService.class);
        stopService(intent);
    }

}