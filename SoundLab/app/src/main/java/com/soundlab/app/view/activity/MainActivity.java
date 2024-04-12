package com.soundlab.app.view.activity;

import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.soundlab.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soundlab.app.model.User;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.fragment.HomeFragment;
import com.soundlab.app.view.fragment.ProfileFragment;
import com.soundlab.app.view.fragment.SearchFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "MAIN";

    private String email;
    private String username;
    private String role;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Per recuperare l'utente
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);
        email = sharedPreferences.getString(USER_EMAIL, null);
        username = sharedPreferences.getString(USER_NAME, null);
        role = sharedPreferences.getString(USER_ROLE, null);

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

        } else if (Objects.equals(currentfragmentTag, Utilities.playerFragmentTag)) {
            // Controlla il fragment immediatamente sotto il playerFragment
            String previousFragmentTag = getPreviousFragmentTag();

            if (Objects.equals(previousFragmentTag, Utilities.searchFragmentTag)) {
                showBottomNavigationView();
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFragment(new SearchFragment(), Utilities.searchFragmentTag); // Sostituisci con il tuo fragment desiderato e il suo tag

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