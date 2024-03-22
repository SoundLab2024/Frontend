package com.soundlab.app.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundlab.R;

import java.util.ArrayList;

import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.User;
import com.soundlab.app.presenter.adapter.ProfileAdapter;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;
import com.soundlab.app.view.activity.SettingsActivity;

public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    private TextView zeroPlaylistTextView;
    private ArrayList<Playlist> playlistArrayList;
    User utente = new User(20,"lucia",true);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout del fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d("ProfileFragment", "onCreateView called");

        // Ottiene la RecyclerView dal layout
        recyclerView = view.findViewById(R.id.playlists_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di playlist
        playlistArrayList = new ArrayList<>();

        // TODO: Carica le playlist dal backend

        // Aggiungi le playlist
        playlistArrayList.add(new Playlist(1, "Playlist1", "Rock", R.drawable.playlist_default, false));

        // Inizializza l'adapter e passa la lista di playlist
        ProfileAdapter profileAdapter = new ProfileAdapter(this, playlistArrayList);

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
        if (playlistArrayList.isEmpty()) {
            createZeroPlaylistTextView();
        }

        // Get the reference to the settings button
        CustomButton settingsButton = view.findViewById(R.id.settings);

        // Set an OnClickListener for the settings button
        settingsButton.setOnClickListener(v -> openSettingsActivity());

        CustomButton analiticheButton = view.findViewById(R.id.analiticheButton);
        if(!utente.isAdmin()){
            analiticheButton.setVisibility(View.GONE);
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

        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(this.requireContext(), R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_add_playlist);

        // Inizializzazione degli elementi di input e bottoni del Dialog
        EditText nome_input = dialog.findViewById(R.id.nome);
        EditText genere_input = dialog.findViewById(R.id.genere);
        CustomButton aggiungi = dialog.findViewById(R.id.elimina);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante di aggiunta
        aggiungi.setOnClickListener(view -> {
            // Ottiene nome e genere dalle caselle di input
            String nome_playlist = nome_input.getText().toString();
            String genere_playlist = genere_input.getText().toString();

            if (!nome_playlist.isEmpty() && !genere_playlist.isEmpty()) {
                // Crea una nuova playlist con i dati inseriti dall'utente

                // TODO: Inserire la playlist nel backend ed ottenere l'id.

                int idPlaylist = 1; // Da cambiare con l'id ottenuto nel backend
                Playlist newPlaylist = new Playlist(idPlaylist, nome_playlist, genere_playlist, R.drawable.playlist_default, false);
                // Ottiene l'adapter dalla RecyclerView
                ProfileAdapter profileAdapter = (ProfileAdapter) recyclerView.getAdapter();
                // Aggiungi la nuova playlist all'adapter
                if (profileAdapter != null) {
                    profileAdapter.addPlaylist(newPlaylist);
                }
                //Chiude il Dialog
                dialog.dismiss();
                //Rimuove la TextView zeroPlaylist
                destroyZeroPlaylistTextView();
            } else {
                // Visualizza un messaggio Toast se il nome o il genere sono vuoti
                Toast toast = Toast.makeText(this.requireContext(), "Inserisci un nome e un genere validi", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Listener per il pulsante di annulla
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });
        //Mostra il dialog
        dialog.show();
    }

    public void createZeroPlaylistTextView() {
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.constraintLayout1);
        ScrollView scrollView = requireView().findViewById(R.id.scrollView);

        // Crea la TextView
        zeroPlaylistTextView = new TextView(requireContext());
        zeroPlaylistTextView.setId(R.id.zeroPlaylistTextView);
        zeroPlaylistTextView.setText( "Non hai aggiunto ancora nessuna playlist");
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
            ((MainActivity) getActivity()).replaceFragment(playlistFragment, Utilities.playlistFragmentTag);
        }
    }


}