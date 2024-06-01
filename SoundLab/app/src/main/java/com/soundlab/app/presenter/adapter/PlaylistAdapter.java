package com.soundlab.app.presenter.adapter;

import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.util.Log;
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
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.PlaylistController;
import com.soundlab.app.model.Album;
import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.CustomCardView;
import com.soundlab.app.view.fragment.PlaylistFragment;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final List<Song> songs;
    private final PlaylistFragment playlistFragment;
    private final Playlist playlist;
    private final PlaylistController playlistController;
    private final String token;

    // Costruttore per inizializzare l'adapter con la lista di tracce
    public PlaylistAdapter(PlaylistFragment playlistFragment, List<Song> songs, Playlist playlist, String token) {
        this.songs = songs;
        this.playlistFragment = playlistFragment;
        this.playlist = playlist;
        this.token = token;
        playlistController = new PlaylistController();
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
            Song selectedSong = songs.get(adapterPosition);

            String artistNames = Utilities.ottieniArtistiDellaTracciaInStringa(selectedSong);

            //Popola il ViewHolder con i dati delle tracce
            holder.songImage.setImageResource(selectedSong.getImage());
            holder.songName.setText(selectedSong.getTitle());
            holder.songArtist.setText(artistNames);

            holder.itemView.setOnClickListener(view -> playlistFragment.loadPlayer(songs.indexOf(selectedSong), (ArrayList<Song>) songs));

            holder.removeButton.setOnClickListener(view -> callDeleteSong(playlist.getId(), adapterPosition, selectedSong));

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

                    Log.d("---SELECTED SONG---", selectedSong.getTitle() + " , " + selectedSong.getId());

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

    private void callDeleteSong(Long playlistId, int adapterPosition, Song selectedSong) {
        playlistController.deleteSong(token, selectedSong.getId(), playlistId, new ControllerCallback<Payload>() {
            @Override
            public void onSuccess(Payload result) {
                removeSong(adapterPosition, selectedSong);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(playlistFragment, errorMessage);
            }
        });
    }

    private void removeSong(int adapterPosition, Song selectedSong) {
        playlist.setSongsNumber(playlist.getSongsNumber() - 1);

        playlistFragment.aggiornaTextViewNumeroBraniPlaylist(playlist);

        songs.remove(selectedSong);
        notifyItemRemoved(adapterPosition);

        String toastText = "Hai rimosso " + selectedSong.getTitle() + " da questa playlist";
        Toast toast = Toast.makeText(playlistFragment.requireContext(), toastText, Toast.LENGTH_SHORT);
        toast.show();

    }

    private void initBottomSheetDialogElements(View bottomSheetView, Song selectedSong) {
        ImageView song_image = bottomSheetView.findViewById(R.id.song_image);
        song_image.setImageResource(selectedSong.getImage());
        TextView song_name = bottomSheetView.findViewById(R.id.title);
        song_name.setText(selectedSong.getTitle());
        TextView song_artist = bottomSheetView.findViewById(R.id.artist);
        String artists = Utilities.ottieniArtistiDellaTracciaInStringa(selectedSong);
        song_artist.setText(artists);
    }

    @Override
    public int getItemCount() {
        return songs.size();
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
