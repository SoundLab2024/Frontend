package view.fragment;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundlab.R;

import java.util.ArrayList;

import model.Playlist;
import presenter.adapter.PlaylistAdapter;
import view.CustomButton;

public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    private TextView zeroPlaylistTextView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Imposta il colore della barra di stato quando la vista è creata
        changeStatusBarColor(view, R.color.blue);

        // Ottieni il riferimento al pulsante addButton e crea un listener
        CustomButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addNewPlaylist());
    }

    // Metodo per aggiungere una nuova playlist
    private void addNewPlaylist() {

        // Creazione e personalizzazione del Dialog
        Dialog dialog = new Dialog(this.requireContext(), R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.popup_add_playlist);

        // Inizializzazione degli elementi di input e bottoni del Dialog
        EditText nome_input = dialog.findViewById(R.id.nome);
        EditText genere_input = dialog.findViewById(R.id.genere);
        CustomButton aggiungi = dialog.findViewById(R.id.aggiungi);
        CustomButton annulla = dialog.findViewById(R.id.annulla);

        // Listener per il pulsante di aggiunta
        aggiungi.setOnClickListener(view -> {
            // Ottiene nome e genere dalle caselle di input
            String nome_playlist = nome_input.getText().toString();
            String genere_playlist = genere_input.getText().toString();

            if (!nome_playlist.isEmpty() && !genere_playlist.isEmpty()) {
                // Crea una nuova playlist con i dati inseriti dall'utente
                Playlist newPlaylist = new Playlist(nome_playlist, genere_playlist, R.drawable.playlist_default, false);
                // Ottiene l'adapter dalla RecyclerView
                PlaylistAdapter playlistAdapter = (PlaylistAdapter) recyclerView.getAdapter();
                // Aggiungi la nuova playlist all'adapter
                if (playlistAdapter != null) {
                    playlistAdapter.addPlaylist(newPlaylist);
                }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout del fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ottiene la RecyclerView dal layout
        recyclerView = view.findViewById(R.id.playlists_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di playlist
        ArrayList<Playlist> playlistArrayList = new ArrayList<>();
        // Aggiungi i dati dal back-end...

        // Inizializza l'adapter e passa la lista di playlist
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, playlistArrayList);

        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(playlistAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Ripristina colore della barra di stato quando la vista viene distrutta
        if (getView() != null) {
            changeStatusBarColor(getView(), R.color.dark_purple);
        }
    }

    private void changeStatusBarColor(View view, int color) {
        int statusBarColor = ContextCompat.getColor(view.getContext(), color);

        // Imposta il colore della barra di stato
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setStatusBarColor(statusBarColor);
        }
    }

//    public void createZeroPlaylistTextView() {
//        ConstraintLayout constraintLayout = requireView().findViewById(R.id.constraintLayout);
//
//        // Crea la TextView solo se zeroPlaylist è true
//        zeroPlaylistTextView = new TextView(requireContext());
//        zeroPlaylistTextView.setId(R.id.zeroPlaylistTextView);
//        zeroPlaylistTextView.setText("Non hai aggiunto ancora nessuna playlist");
//        zeroPlaylistTextView.setTextSize(14);
//        zeroPlaylistTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
//        zeroPlaylistTextView.setGravity(Gravity.CENTER);
//
//        // Aggiungi la TextView al ConstraintLayout
//        constraintLayout.addView(zeroPlaylistTextView);
//
//        // Imposta "wrap_content"
//        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) zeroPlaylistTextView.getLayoutParams();
//        layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
//        layoutParams.height = 300;
//        zeroPlaylistTextView.setLayoutParams(layoutParams);
//
//        // Creazione di un oggetto ConstraintSet per impostare i constraint
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//
//        // Impostazione dei constraint per la TextView
//        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
//        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.TOP, recyclerView.getId(), ConstraintSet.BOTTOM, 0);
//        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
//        constraintSet.connect(zeroPlaylistTextView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
//
//        // Applicazione dei vincoli al ConstraintLayout
//        constraintSet.applyTo(constraintLayout);
//
//        // Rimuovi la TextView solo se zeroPlaylist è false e la TextView esiste
//
//    }
//
//    public void destroyZeroPlaylistTextView() {
//        ConstraintLayout constraintLayout = requireView().findViewById(R.id.constraintLayout);
//
//        if (zeroPlaylistTextView != null) {
//            constraintLayout.removeView(zeroPlaylistTextView);
//            zeroPlaylistTextView = null;  // Imposta a null dopo la rimozione
//        }
//    }

}