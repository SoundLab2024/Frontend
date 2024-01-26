package view.fragment;

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

import model.Playlist;
import model.Song;
import presenter.adapter.SongAdapter;
import view.CustomButton;
import view.Utilities;
import view.activity.MainActivity;

public class PlaylistFragment extends Fragment {

    private Playlist playlist;
    private TextView nomePlaylist;
    private TextView genere;
    private RecyclerView recyclerView;
    private ArrayList<Song> songArrayList;
    private SongAdapter songAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        // Necessario per il funzionamento del menuItem
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            playlist = (Playlist) bundle.getSerializable("playlist");

            if (playlist != null) {
                nomePlaylist = view.findViewById(R.id.nomePlaylist);
                genere = view.findViewById(R.id.genere);
                nomePlaylist.setText(playlist.getName());
                genere.setText(playlist.getGenere());

                TextView numeroBrani = view.findViewById(R.id.numeroBrani);
                String numBrani = playlist.getNumberOfSongs() + " brani";
                numeroBrani.setText(numBrani);
            }
        }

        ToggleButton favouriteButton = view.findViewById(R.id.favourite_button);
        favouriteButton.setChecked(playlist.isFavorite());

        favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // TODO: Aggiorna la preferenza della playlist nel backend

            playlist.setFavorite(isChecked);
        });

        // Ottiene la RecyclerView dal layout
        recyclerView = view.findViewById(R.id.songs_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di playlist
        songArrayList = new ArrayList<>();

        // TODO: Carica le tracce dal backend

        //songsArrayList.add(new Song(...));

        // Inizializza l'adapter e passa la lista di tracce
        songAdapter = new SongAdapter(this, songArrayList);
        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.alternative_purple);
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

            // Mostra la bottomNavigationView e rimpiazza il fragmet attuale con ProfileFragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showBottomNavigationView();
                ((MainActivity) getActivity()).replaceFragment(new ProfileFragment(), Utilities.profileFragmentTag);
            }
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
        EditText playlist_input = dialog.findViewById(R.id.playlist_input);
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
        EditText playlist_input = dialog.findViewById(R.id.playlist_input);
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


}