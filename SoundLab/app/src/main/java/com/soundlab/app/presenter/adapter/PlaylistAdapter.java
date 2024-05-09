package com.soundlab.app.presenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import com.soundlab.app.model.Album;
import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.CustomCardView;
import com.soundlab.app.view.fragment.PlaylistFragment;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final ArrayList<Song> songArrayList;
    private final PlaylistFragment playlistFragment;
    private final Playlist playlist;


    // Costruttore per inizializzare l'adapter con la lista di tracce
    public PlaylistAdapter(PlaylistFragment playlistFragment, ArrayList<Song> songArrayList, Playlist playlist) {
        this.songArrayList = songArrayList;
        this.playlistFragment = playlistFragment;
        this.playlist = playlist;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla il layout per ciascun elemento della RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_cardview, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {
            Song selectedSong = songArrayList.get(adapterPosition);

            String artistNames = Utilities.ottieniArtistiDellaTracciaInStringa(selectedSong);

            //Popola il ViewHolder con i dati delle tracce
            holder.songImage.setImageResource(selectedSong.getImage());
            holder.songName.setText(selectedSong.getName());
            holder.songArtist.setText(artistNames);

            holder.itemView.setOnClickListener(view -> playlistFragment.loadPlayer(songArrayList.indexOf(selectedSong), songArrayList));

            holder.removeButton.setOnClickListener(view -> {

                // TODO: Rimuovi l'associaizone Playlist<->Traccia dal backend, aggiorna il numero di canzoni della playlist

                playlist.setSongsNumber(playlist.getSongsNumber() - 1);

                playlistFragment.aggiornaTextViewNumeroBraniPlaylist(playlist);

                songArrayList.remove(selectedSong);
                notifyItemRemoved(adapterPosition);

                String toastText = "Hai rimosso " + selectedSong.getName() + " da questa playlist";
                Toast toast = Toast.makeText(view.getContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();

            });

            holder.itemView.setOnLongClickListener(v -> {

                // Crea il BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.itemView.getContext());
                View bottomSheetView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.bottom_popup_song, (ViewGroup) holder.itemView, false);

                // Inizializza gli elementi del layout del pannello inferiore
                initBottomSheetDialogElements(bottomSheetView, selectedSong);
                CustomCardView add_to_another_playlist = bottomSheetView.findViewById(R.id.add_to_playlist);
                CustomCardView goto_album = bottomSheetView.findViewById(R.id.goto_album);
                CustomCardView goto_artist = bottomSheetView.findViewById(R.id.goto_artist);

                // Mostra il BottomSheetDialog
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                // Imposta il listener del bottone Elimina
                add_to_another_playlist.setOnClickListener(v0 -> {
                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();

                    playlistFragment.loadAddToPlaylistFragment(selectedSong);
                });

                goto_artist.setOnClickListener(v0 -> {
                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();

                    Artist artist = selectedSong.getMainArtist();
                    playlistFragment.loadArtistFragment(artist);
                });

                goto_album.setOnClickListener(v0 -> {
                    // Chiude il BottomSheetDialog
                    bottomSheetDialog.dismiss();

                    Album album = new Album(7, "Album", 2015, selectedSong.getArtists().get(0));
                    playlistFragment.loadAlbumFragment(album);
                });

                return true;
            });

        }

    }

    private void initBottomSheetDialogElements(View bottomSheetView, Song selectedSong) {
        ImageView song_image = bottomSheetView.findViewById(R.id.song_image);
        song_image.setImageResource(selectedSong.getImage());
        TextView song_name = bottomSheetView.findViewById(R.id.title);
        song_name.setText(selectedSong.getName());
        TextView song_artist = bottomSheetView.findViewById(R.id.artist);
        String artists = Utilities.ottieniArtistiDellaTracciaInStringa(selectedSong);
        song_artist.setText(artists);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView songImage;
        private final TextView songName;
        private final TextView songArtist;
        private final CustomButton removeButton;

        // Costruttore che inizializza i riferimenti agli elementi della playlist
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.song_image);
            songName = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
