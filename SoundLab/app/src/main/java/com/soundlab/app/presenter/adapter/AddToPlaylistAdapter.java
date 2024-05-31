package com.soundlab.app.presenter.adapter;

import static com.soundlab.app.utils.Utilities.showErrorMessage;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.PlaylistController;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.utils.Debouncer;
import com.soundlab.app.view.fragment.AddToPlaylistFragment;

import java.util.List;

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder> {

    private final List<Playlist> playlists;
    private final Song song;
    private final PlaylistController playlistController;
    private final String token;
    private final Debouncer debouncer = new Debouncer();
    private final Fragment addToPlaylistFragment;

    // Costruttore per inizializzare l'adapter con la lista di playlist
    public AddToPlaylistAdapter(AddToPlaylistFragment addToPlaylistFragment, List<Playlist> playlists, Song song, String token) {
        this.playlists = playlists;
        this.song = song;
        this.token = token;
        this.addToPlaylistFragment = addToPlaylistFragment;
        playlistController = new PlaylistController();
        updatePlaylistOrder();
    }

    // Metodo per aggiornare l'ordine delle playlist in base alle preferenze
    public void updatePlaylistOrder() {
        playlists.sort((playlist1, playlist2) -> {
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
        return new ViewHolder(view, addToPlaylistFragment.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {
            Playlist selectedPlaylist = playlists.get(adapterPosition);

            // Popola il ViewHolder con i dati della playlist
            holder.playlistImage.setImageResource(selectedPlaylist.getImage());
            holder.playlistName.setText(selectedPlaylist.getName());
            holder.playlistGenere.setText(selectedPlaylist.getGenre());

            //holder.checkButton.setChecked(playlists.contains());


            holder.itemView.setOnClickListener(v -> {
                holder.itemView.setEnabled(false);
                holder.checkButton.setEnabled(false);

                boolean isChecked = holder.checkButton.isChecked();
                holder.checkButton.setChecked(!isChecked);

                debouncer.debounce(()-> {
                    // Gestisci il clic dell'elemento se necessario

                    addOrRemoveSongFromPlaylist(!isChecked, selectedPlaylist, holder);
                }, 1500);

            });


            // Gestisci il cambiamento di preferenza quando il pulsante Preferito viene selezionato/deselezionato
            holder.checkButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                holder.itemView.setEnabled(false);
                holder.checkButton.setEnabled(false);

                debouncer.debounce(()-> {
                    addOrRemoveSongFromPlaylist(isChecked, selectedPlaylist, holder);
                }, 1500);

            });
        }

    }

    private void addOrRemoveSongFromPlaylist(boolean isChecked, Playlist playlist, ViewHolder holder) {
        if (isChecked) {
            callInsertSong(playlist.getId(), song.getId(), holder);
        } else {
            callDeleteSong(playlist.getId(), song.getId(), holder);
        }
    }

    private void callDeleteSong(Long playlistId, Long songId, ViewHolder holder) {
        playlistController.deleteSong(token, songId, playlistId, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                holder.itemView.setEnabled(true);
                holder.checkButton.setEnabled(true);
            }

            @Override
            public void onFailed(String errorMessage) {
                holder.itemView.setEnabled(true);
                holder.checkButton.setEnabled(true);
            }
        });
    }

    private void callInsertSong(Long playlistId, long songId, ViewHolder holder) {
        playlistController.insertSong(token, songId, playlistId, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                holder.itemView.setEnabled(true);
                holder.checkButton.setEnabled(true);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(addToPlaylistFragment, errorMessage);
                holder.itemView.setEnabled(true);
                holder.checkButton.setEnabled(true);
            }
        });
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
        for (Playlist p : playlists) {
            if (!p.isFavourite() && playlist.getName().compareToIgnoreCase(p.getName()) < 0) {
                // Se la playlist corrente non è favorita e l'ordine alfabetico della playlist da inserire è maggiore o uguale,
                // interrompi il loop
                break;
            } else {
                index++;
            }
        }

        // Aggiungi la playlist non favorita nella posizione trovata
        playlists.add(index, playlist);

        // Restituisci l'indice della nuova playlist inserita
        return index;
    }


    // Restituisci il numero totale di elementi nella RecyclerView
    @Override
    public int getItemCount() {
        return playlists.size();
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
