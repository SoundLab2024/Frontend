package com.soundlab.app.view.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.soundlab.R;

import java.util.ArrayList;
import java.util.Date;

import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.PlaylistAdapter;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;

public class PlaylistFragment extends Fragment {

    private Playlist playlist;
    private TextView nomePlaylist;
    private TextView genere;
    private TextView numeroBrani;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        Log.d("PlaylistFragment", "onCreateView called");

        // Necessario per il funzionamento del menuItem
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            playlist = (Playlist) bundle.getSerializable("playlist");
        }

        ToggleButton favouriteButton = view.findViewById(R.id.favourite_button);
        favouriteButton.setChecked(playlist.isFavorite());

        favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // TODO: Aggiorna la preferenza della playlist nel backend

            playlist.setFavorite(isChecked);
        });

        // Ottiene la RecyclerView dal layout
        RecyclerView recyclerView = view.findViewById(R.id.songs_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di canzoni
        ArrayList<Song> songArrayList = new ArrayList<>();

        //TODO: Carica le tracce e relativi artisti dal backend

        // Aggiunge le tracce alla lista ed aggiunge alla traccia i relativi artisti
        Song song1 = new Song(1, "Canzone1","Rock", R.drawable.cover_default);
        song1.addArtist(new Artist(7, "Gio", new Date(5 / 1985), "Inghilterra"));
        songArrayList.add(song1);

        Song song2 = new Song(2, "Canzone2","Rock", R.drawable.cover_default);
        song2.addArtist(new Artist(36, "Ale", new Date(7 / 1995), "Italia"));
        song2.addArtist(new Artist(31, "Ren", new Date(3 / 1998), "Italia"));
        songArrayList.add(song2);

        playlist.setNumberOfSongs(2);


        // Inizializza l'adapter e passa la lista di tracce
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, songArrayList, playlist);
        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(playlistAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.alternative_purple);

        if (playlist != null) {
            nomePlaylist = view.findViewById(R.id.nomePlaylist);
            genere = view.findViewById(R.id.genere);
            nomePlaylist.setText(playlist.getName());
            genere.setText(playlist.getGenere());
            numeroBrani = view.findViewById(R.id.numeroBrani);
            aggiornaTextViewNumeroBraniPlaylist(playlist);
        }

        CardView add_songCardView = view.findViewById(R.id.add_songcardview);
        add_songCardView.setOnClickListener(v -> loadFragment(Utilities.searchFragmentTag));
    }

    public void aggiornaTextViewNumeroBraniPlaylist(Playlist playlist){
        String numBrani = playlist.getNumberOfSongs() + " brani";
        numeroBrani.setText(numBrani);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Infla il menu; questo aggiunge elementi al tuo action bar, se presente.
        inflater.inflate(R.menu.menu_playlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.elimina) {
            showDialog_confermaElimina();
            return true;
        } else if (itemId == R.id.rinomina) {
            showDialog_rinomina();
            return true;
        } else if (itemId == R.id.cambia_genere) {
            showDialog_cambiaGenere();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog_confermaElimina() {
        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_elimina_playlist);

        // Modifica del layout del popup
        CardView cardViewToRemove = dialog.findViewById(R.id.playlistCardView);
        if (cardViewToRemove != null) {
            cardViewToRemove.setVisibility(View.GONE);
        }

        // Inizializzazione dei bottoni del Dialog
        CustomButton conferma_elimina = dialog.findViewById(R.id.elimina);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante di conferma eliminazione
        conferma_elimina.setOnClickListener(view -> {

            // TODO: eliminare la playlist dal backend

            loadFragment(Utilities.profileFragmentTag);

            // Chiude il Dialog
            dialog.dismiss();
        });

        // Listener per il pulsante di annullamento
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });

        // Mostra il Dialog
        dialog.show();
    }

    private void showDialog_rinomina() {
        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_rinomina_playlist);

        // Modifica del layout del popup
        CardView cardViewToRemove = dialog.findViewById(R.id.playlistCardView);
        if (cardViewToRemove != null) {
            cardViewToRemove.setVisibility(View.GONE);
        }

        // Inizializzazione degli elementi di input e bottoni del Dialog
        EditText playlist_input = dialog.findViewById(R.id.email_input);
        CustomButton rinomina = dialog.findViewById(R.id.rinomina);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante di rinomina
        rinomina.setOnClickListener(view -> {
            // Ottiene il nuovo nome dalla casella di input
            String nuovo_nome_playlist = playlist_input.getText().toString();
            if (!nuovo_nome_playlist.isEmpty()) {

                // TODO: cambia il nome della playlist nel backend

                // Aggiorna il nome della playlist
                playlist.setName(nuovo_nome_playlist);
                nomePlaylist.setText(nuovo_nome_playlist);

                dialog.dismiss();
            } else {
                // Visualizza un messaggio Toast se il nome è vuoto
                Toast toast = Toast.makeText(requireContext(), "Inserisci un nome valido", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Listener per il pulsante di annullamento
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });

        // Mostra il Dialog
        dialog.show();
    }

    private void showDialog_cambiaGenere() {
        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_rinomina_playlist);

        // Modifica del layout del popup
        CardView cardViewToRemove = dialog.findViewById(R.id.playlistCardView);
        if (cardViewToRemove != null) {
            cardViewToRemove.setVisibility(View.GONE);
        }

        // Inizializzazione e modifica degli elementi di input e bottoni del Dialog
        EditText playlist_input = dialog.findViewById(R.id.email_input);
        playlist_input.setHint("Inserisci il nuovo genere qui.");
        CustomButton rinomina = dialog.findViewById(R.id.rinomina);
        rinomina.setText("Cambia");
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante rinomina
        rinomina.setOnClickListener(view -> {
            // Ottiene il nuovo genere dalla casella di input
            String nuovo_genere = playlist_input.getText().toString();
            if (!nuovo_genere.isEmpty()) {

                // TODO: cambia il genere della playlist nel backend

                // Aggiorna il genere
                playlist.setGenere(nuovo_genere);
                genere.setText(nuovo_genere);
                dialog.dismiss();
            } else {
                // Visualizza un messaggio Toast se il genere è vuoto
                Toast toast = Toast.makeText(requireContext(), "Inserisci un genere valido", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // Listener per il pulsante di annullamento
        annulla.setOnClickListener(view -> {
            // Chiude il Dialog senza effettuare alcuna azione
            dialog.dismiss();
        });

        // Mostra il Dialog
        dialog.show();
    }

    // Mostra la bottomNavigationView e rimpiazza il fragmet attuale con ProfileFragment
    private void loadFragment(String fragmentTag){
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigationView();
            ((MainActivity) getActivity()).selectRightItemBottomNavView(fragmentTag);
        }
    }

    public void loadArtistFragment(Artist artist) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("artist", artist);

        Fragment artistFragment = new ArtistFragment();
        artistFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(artistFragment, Utilities.artistFragmentTag);
        }
    }

    public void loadAddToPlaylistFragment(Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);

        Fragment addToPlaylistFragment = new AddToPlaylistFragment();
        addToPlaylistFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(addToPlaylistFragment, Utilities.addToPlaylistFragmentTag);
        }
    }


}