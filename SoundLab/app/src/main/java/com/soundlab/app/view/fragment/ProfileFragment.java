package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.presenter.adapter.ProfileAdapter;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.InsertPlaylistRequest;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.activity.MainActivity;
import com.soundlab.app.view.activity.SettingsActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    private TextView zeroPlaylistTextView;
    private List<Playlist> playlists;
    private Library library;
    private String email;
    private String username;
    private String token;
    private String role;
    private String TAG = "PROFILE_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout del fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d("ProfileFragment", "onCreateView called");

        // roba dell'utente
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        role = sharedPreferences.getString(USER_ROLE, null);
        email = sharedPreferences.getString(USER_EMAIL, null);
        username = sharedPreferences.getString(USER_NAME, null);
        token = sharedPreferences.getString(USER_TOKEN, null);


        // Ottiene la RecyclerView dal layout
        recyclerView = view.findViewById(R.id.playlists_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        library = Library.getInstance();

        // Crea una nuova lista di playlist
        playlists = library.getPlaylists();

        // Inizializza l'adapter e passa la lista di playlist
        ProfileAdapter profileAdapter = new ProfileAdapter(this, playlists, token);

        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(profileAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.blue);

        // Ottieni il riferimento al pulsante addButton e crea un listener
        CustomButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addNewPlaylist());

        //Se non ci sono playlist crea la TextView zeroPlaylist
        if (playlists.isEmpty()) {
            createZeroPlaylistTextView();
        }

        // Get the reference to the settings button
        CustomButton settingsButton = view.findViewById(R.id.settings);

        // Set an OnClickListener for the settings button
        settingsButton.setOnClickListener(v -> openSettingsActivity());

        CustomButton analiticheButton = view.findViewById(R.id.analiticheButton);
        if (role != null) {
            if (!role.equals("ADMIN")) {
                analiticheButton.setVisibility(View.GONE);
            }
        }

        analiticheButton.setOnClickListener(v -> {
            AnaliticheFragment analiticheFragment = new AnaliticheFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();


            // Sostituisci il fragment corrente con il nuovo fragment
            transaction.replace(android.R.id.content, new AnaliticheFragment());

            // Aggiungi la transazione al back stack, così l'utente può tornare indietro
            transaction.addToBackStack(null);

            // Esegui la transazione
            transaction.commit();
        });
    }

    private void openSettingsActivity() {
        // Create an Intent to start the SettingsActivity
        Intent intent = new Intent(requireContext(), SettingsActivity.class);

        // Start the SettingsActivity
        startActivity(intent);
    }

    private void addNewPlaylist() {
        Dialog dialog = new Dialog(this.requireContext(), R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_add_playlist);

        EditText nome_input = dialog.findViewById(R.id.nome);
        EditText genere_input = dialog.findViewById(R.id.genere);
        CustomButton aggiungi = dialog.findViewById(R.id.elimina);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        aggiungi.setOnClickListener(view -> {
            String nome_playlist = nome_input.getText().toString();
            String genere_playlist = genere_input.getText().toString();

            if (!nome_playlist.isEmpty() && !genere_playlist.isEmpty()) {
                ProfileAdapter profileAdapter = (ProfileAdapter) recyclerView.getAdapter();

                if (profileAdapter != null) {
                    callCreatePlaylist(nome_playlist, genere_playlist, Library.getInstance().getId(), profileAdapter);
                    dialog.dismiss();
                    destroyZeroPlaylistTextView();
                } else {
                    showErrorToast("Impossibile aggiungere la playlist. Adapter non valido.");
                }
            } else {
                showErrorToast("Inserisci un nome e un genere validi.");
            }
        });

        annulla.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void callCreatePlaylist(String name, String genre, Long libraryId, ProfileAdapter profileAdapter) {
        InsertPlaylistRequest request = new InsertPlaylistRequest(name, genre, libraryId);

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.createPlaylist(authToken, request);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    Payload payload = response.body();
                    Long playlistId = Long.parseLong(payload.getMsg());

                    Log.d(TAG, "createPlaylist - status code: " + payload.getStatusCode());

                    Playlist newPlaylist = new Playlist(playlistId, name, genre);
                    profileAdapter.addPlaylist(newPlaylist);
                } else {
                    showErrorToast("Impossibile aggiungere la playlist.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                showErrorToast("Impossibile aggiungere la playlist. Errore: " + t.getMessage());
            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void createZeroPlaylistTextView() {
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.constraintLayout1);
        ScrollView scrollView = requireView().findViewById(R.id.scrollView);

        // Crea la TextView
        zeroPlaylistTextView = new TextView(requireContext());
        zeroPlaylistTextView.setId(R.id.zeroPlaylistTextView);
        zeroPlaylistTextView.setText("Non hai aggiunto ancora nessuna playlist");
        zeroPlaylistTextView.setTextSize(14);
        zeroPlaylistTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        zeroPlaylistTextView.setGravity(Gravity.CENTER);

        // Aggiungi la TextView al ConstraintLayout
        constraintLayout.addView(zeroPlaylistTextView);

        // Imposta "wrap_content"
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) zeroPlaylistTextView.getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        zeroPlaylistTextView.setLayoutParams(layoutParams);

        // Creazione di un oggetto ConstraintSet per impostare i constraint
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        // Impostazione dei constraint per la TextView
        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.TOP, scrollView.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        // Applicazione dei constraint al ConstraintLayout
        constraintSet.applyTo(constraintLayout);
    }

    private void destroyZeroPlaylistTextView() {
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.constraintLayout1);

        // Rimuovi la TextView se esiste
        if (zeroPlaylistTextView != null) {
            constraintLayout.removeView(zeroPlaylistTextView);
            zeroPlaylistTextView = null;
        }
    }

    public void loadPlaylistFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlist", playlist);

        Fragment playlistFragment = new PlaylistFragment();
        playlistFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(playlistFragment, Utilities.playlistFragmentTag);
        }
    }


}