package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.PlaylistController;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.AddToPlaylistAdapter;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;

import java.util.List;

public class AddToPlaylistFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaylistController playlistController;
    private String token;
    private final Fragment addToPlaylistFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_playlist, container, false);

        Log.d("AddToPlaylistFragment", "onCreateView called");

        playlistController = new PlaylistController();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);

        Bundle bundle = getArguments();

        if (bundle != null) {
            Song song = (Song) bundle.getSerializable("song");
            Log.d("AddToPlaylistFragfment", song.getTitle());

            if (song != null) {
                // Ottiene la RecyclerView dal layout
                recyclerView = view.findViewById(R.id.playlists_recyclerView);
                recyclerView.setNestedScrollingEnabled(false);

                // Crea una nuova lista di playlist
                List<Playlist> playlists = Library.getInstance().getPlaylists();

                // Inizializza l'adapter e passa la lista di playlist
                AddToPlaylistAdapter addToPlaylistAdapter = new AddToPlaylistAdapter(this, playlists, song, token);

                // Imposta un layout manager per la RecyclerView (lista verticale)
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

                // Imposta il layout manager e l'adapter per la RecyclerView
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(addToPlaylistAdapter);
            }
        }


        CardView nuovaPlaylist_cardView = view.findViewById(R.id.nuovaPlaylistCardView);

        // Imposta il listener per il click sulla CardView
        nuovaPlaylist_cardView.setOnClickListener(v -> addNewPlaylist());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);
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
                callCreatePlaylist(nome_playlist, genere_playlist, Library.getInstance().getId());

                //Chiude il Dialog
                dialog.dismiss();

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


    private void callCreatePlaylist (String name, String genre, Long libraryId) {
        playlistController.createPlaylist(token, name, genre, libraryId, new ControllerCallback<Long>() {
            @Override
            public void onSuccess(Long playlistId) {
                Playlist newPlaylist = new Playlist(playlistId, name, genre, false, null);

                AddToPlaylistAdapter addToPlaylistAdapter = (AddToPlaylistAdapter) recyclerView.getAdapter();
                // Aggiungi la nuova playlist all'adapter
                if (addToPlaylistAdapter != null) {
                    addToPlaylistAdapter.addPlaylist(newPlaylist);
                }
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(addToPlaylistFragment, errorMessage);
            }
        });
    }

}
