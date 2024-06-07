package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_ROLE;
import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Dialog;
import android.content.Context;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.PlaylistController;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.presenter.adapter.ProfileAdapter;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.activity.MainActivity;

import java.util.List;

public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getActivity());
    private TextView zeroPlaylistTextView;
    private List<Playlist> playlists;
    private Library library;
    private String email;
    private String username;
    private String token;
    private String role;
    private PlaylistController playlistController;
    private final Fragment profileFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout del fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d("ProfileFragment", "onCreateView called");

        playlistController = new PlaylistController();

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
        playlists = library.getPlaylists();

        initAdapter();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.blue);

        TextView usernameTextView = view.findViewById(R.id.username);
        usernameTextView.setText(username);

        // Ottieni il riferimento al pulsante addButton e crea un listener
        CustomButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addNewPlaylist());

        //Se non ci sono playlist crea la TextView zeroPlaylist
        if (library.isInitialized() && playlists.isEmpty()) {
            createZeroPlaylistTextView();
        }

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

    private void initAdapter() {
        // Inizializza l'adapter e passa la lista di playlist
        ProfileAdapter profileAdapter = new ProfileAdapter(this, playlists, token);

        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(profileAdapter);
    }


    private void addNewPlaylist() {

        Bundle bundle = new Bundle();
        bundle.putString("id", "1");
        bundle.putString("testo", "TestLog");
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");


        mFirebaseAnalytics.logEvent("event_test", bundle);

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
                    Toast.makeText(getActivity(), "Impossibile aggiungere la playlist. Adapter non valido.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Inserisci un nome e un genere validi.", Toast.LENGTH_SHORT).show();
            }
        });

        annulla.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void callCreatePlaylist (String name, String genre, Long libraryId, ProfileAdapter profileAdapter) {
        playlistController.createPlaylist(token, name, genre, libraryId, new ControllerCallback<Long>() {
            @Override
            public void onSuccess(Long playlistId) {
                Playlist newPlaylist = new Playlist(playlistId, name, genre);
                profileAdapter.addPlaylist(newPlaylist);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(profileFragment, errorMessage);
            }
        });
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