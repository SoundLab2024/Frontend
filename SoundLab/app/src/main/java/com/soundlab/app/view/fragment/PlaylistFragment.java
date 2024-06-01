package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.PlaylistController;
import com.soundlab.app.model.Album;
import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.PlaylistAdapter;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.utils.Debouncer;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private Playlist playlist;
    private TextView nomePlaylist;
    private TextView genere;
    private TextView numeroBrani;
    private String token;
    private final Debouncer debouncer = new Debouncer();
    private PlaylistController playlistController;
    private List<Song> songs;
    private RecyclerView recyclerView;
    private final Fragment playlistFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        Log.d("PlaylistFragment", "onCreateView called");

        playlistController = new PlaylistController();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);

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
        favouriteButton.setChecked(playlist.isFavourite());

        favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> updatePlaylistPreferences(isChecked, favouriteButton));

        // Ottiene la RecyclerView dal layout
        recyclerView = view.findViewById(R.id.songs_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di canzoni
        songs = new ArrayList<>();

        retriveSongs();

        return view;
    }

    private void retriveSongs() {
        playlistController.retriveSongs(token, playlist.getId(), new ControllerCallback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> res) {
                songs = res;
                initAdapter();
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(playlistFragment, errorMessage);
            }
        });
    }

    private void initAdapter() {
        // Inizializza l'adapter e passa la lista di tracce
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, songs, playlist, token);
        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(playlistAdapter);
    }

    private void updatePlaylistPreferences(boolean isChecked, ToggleButton favouriteButton) {
        favouriteButton.setEnabled(false);
        debouncer.debounce(() -> callFavPlaylist(isChecked, favouriteButton), 500);
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
            genere.setText(playlist.getGenre());
            numeroBrani = view.findViewById(R.id.numeroBrani);
            aggiornaTextViewNumeroBraniPlaylist(playlist);
        }

        CardView add_songCardView = view.findViewById(R.id.add_songcardview);
        add_songCardView.setOnClickListener(v -> loadFragment(Utilities.searchFragmentTag));
    }

    private void callFavPlaylist(boolean isChecked, ToggleButton favouriteButton) {
        playlistController.updateFavouritePlaylist(token, playlist.getId(), new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                favouriteButton.setEnabled(true);
                playlist.setFavourite(isChecked);
            }

            @Override
            public void onFailed(String errorMessage) {
                favouriteButton.setEnabled(true);
                showErrorMessage(playlistFragment, errorMessage);
            }
        });
    }

    public void aggiornaTextViewNumeroBraniPlaylist(Playlist playlist){
        String numBrani = playlist.getSongsNumber() + " brani";
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

            callDeletePlaylist(playlist);

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

    private void callDeletePlaylist(Playlist playlist) {
        playlistController.deletePlaylist(token, playlist.getId(), new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                List<Playlist> playlists = Library.getInstance().getPlaylists();
                playlists.remove(playlist);
                loadFragment(Utilities.profileFragmentTag);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(playlistFragment, errorMessage);
            }
        });
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
            String newName = playlist_input.getText().toString();
            if (!newName.isEmpty()) {
                modifyPlaylist(newName, playlist.getGenre(), playlist, true);
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
            String newGenre = playlist_input.getText().toString();
            if (!newGenre.isEmpty()) {
                modifyPlaylist(playlist.getName(), newGenre, playlist, false);
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


    private void modifyPlaylist(String newName, String newGenre, Playlist playlist, boolean rename) {
        playlistController.modifyPlaylist(token, newName, newGenre, playlist.getId(), new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                if (rename) {
                    // Aggiorna il nome della playlist
                    playlist.setName(newName);
                    nomePlaylist.setText(newName);
                } else {
                    // Aggiorna il genere
                    playlist.setGenre(newGenre);
                    genere.setText(newGenre);
                }
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(playlistFragment, errorMessage);
            }
        });
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

    public void loadAlbumFragment(Album album){
        Bundle bundle = new Bundle();
        bundle.putSerializable("album", album);

        Fragment albumFragment = new AlbumFragment();
        albumFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(albumFragment, Utilities.albumFragmentTag);
        }
    }

    public void loadPlayer(int songPosition, ArrayList<Song> songArrayList){
        Utilities.loadPlayer(getActivity(), songPosition, songArrayList);
    }


}