package com.soundlab.app.presenter.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;

import java.util.ArrayList;

import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.view.fragment.AddToPlaylistFragment;

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder> {

    private final ArrayList<Playlist> playlistArrayList;
    private final Song song;
    private final Context context;

    // Costruttore per inizializzare l'adapter con la lista di playlist
    public AddToPlaylistAdapter(AddToPlaylistFragment addToPlaylistFragment, ArrayList<Playlist> playlistArrayList, Song song) {
        this.playlistArrayList = playlistArrayList;
        this.song = song;
        context = addToPlaylistFragment.getContext();
        updatePlaylistOrder();
    }

    // Metodo per aggiornare l'ordine delle playlist in base alle preferenze
    public void updatePlaylistOrder() {
        playlistArrayList.sort((playlist1, playlist2) -> {
            // Ordina per preferenza (favorite prima), poi per nome
            if (playlist1.isFavourite() == playlist2.isFavourite()) {
                // Se hanno lo stesso stato di preferenza, ordina per nome
                return playlist1.getName().compareToIgnoreCase(playlist2.getName());
            } else {
                // Altrimenti, ordina per preferenza
                return Boolean.compare(playlist2.isFavourite(), playlist1.isFavourite());
            }
        });
    }

    // Metodo chiamato quando RecyclerView ha bisogno di un nuovo ViewHolder
    @NonNull
    @Override
    public AddToPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla il layout per ciascun elemento della RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_cardview, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {
            Playlist selectedPlaylist = playlistArrayList.get(adapterPosition);

            // Popola il ViewHolder con i dati della playlist
            holder.playlistImage.setImageResource(selectedPlaylist.getImage());
            holder.playlistName.setText(selectedPlaylist.getName());
            holder.playlistGenere.setText(selectedPlaylist.getGenre());
            holder.checkButton.setChecked(selectedPlaylist.isFavourite());


            holder.itemView.setOnClickListener(v -> {
                // Gestisci il clic dell'elemento se necessario
                boolean isChecked = holder.checkButton.isChecked();
                holder.checkButton.setChecked(!isChecked);

                // TODO: Aggiungi/Elimina l'associazione Playlist<->Traccia dal backend, aggiorna il numero di canzoni della playlist
            });


            // Gestisci il cambiamento di preferenza quando il pulsante Preferito viene selezionato/deselezionato
            holder.checkButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

                // TODO: Aggiungi/Elimina l'associazione Playlist<->Traccia dal backend, aggiorna il numero di canzoni della playlist

            });

        }

    }

    public void addPlaylist(Playlist newPlaylist) {
        // Aggiungi la nuova playlist alla lista e notifica alla UI
        int index = addPlaylistInOrder(newPlaylist);
        new Handler().post(() -> notifyItemInserted(index));
    }

    private int addPlaylistInOrder(Playlist playlist) {
        // Inizializza l'indice in cui verrà inserita la nuova playlist
        int index = 0;

        // Trova l'indice in cui inserire la playlist non favorita
        for (Playlist p : playlistArrayList) {
            if (!p.isFavourite() && playlist.getName().compareToIgnoreCase(p.getName()) < 0) {
                // Se la playlist corrente non è favorita e l'ordine alfabetico della playlist da inserire è maggiore o uguale,
                // interrompi il loop
                break;
            } else {
                index++;
            }
        }

        // Aggiungi la playlist non favorita nella posizione trovata
        playlistArrayList.add(index, playlist);

        // Restituisci l'indice della nuova playlist inserita
        return index;
    }


    // Restituisci il numero totale di elementi nella RecyclerView
    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    // ViewHolder che contiene i riferimenti agli elementi della playlist
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView playlistImage;
        private final TextView playlistName;
        private final TextView playlistGenere;
        private final ToggleButton checkButton;

        // Costruttore che inizializza i riferimenti agli elementi della playlist
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image);
            playlistName = itemView.findViewById(R.id.playlist_name);
            playlistGenere = itemView.findViewById(R.id.artist);
            checkButton = itemView.findViewById(R.id.favourite_button);
            Drawable drawable_checkButton = ContextCompat.getDrawable(context, R.drawable.custom_button_check);
            checkButton.setBackground(drawable_checkButton);
        }
    }
}
