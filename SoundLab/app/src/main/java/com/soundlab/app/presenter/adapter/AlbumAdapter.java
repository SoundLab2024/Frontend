package com.soundlab.app.presenter.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.model.Song;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.fragment.AlbumFragment;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private final ArrayList<Song> songArrayList;
    private final AlbumFragment albumFragment;

    public AlbumAdapter(ArrayList<Song> songArrayList, AlbumFragment albumFragment) {
        this.songArrayList = songArrayList;
        this.albumFragment = albumFragment;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_cardview, parent, false);
        return new AlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {
            Song selectedSong = songArrayList.get(adapterPosition);

            String artistNames = Utilities.ottieniArtistiDellaTracciaInStringa(selectedSong);

            //Popola il ViewHolder con i dati delle tracce
            holder.songImage.setImageResource(selectedSong.getImage());
            holder.songName.setText(selectedSong.getName());
            holder.songArtist.setText(artistNames);

            holder.itemView.setOnClickListener(view -> albumFragment.loadPlayer(songArrayList.indexOf(selectedSong), songArrayList));

            holder.addToPlaylist.setOnClickListener(view -> albumFragment.loadAddToPlaylistFragment(selectedSong));
        }
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView songImage;
        private final TextView songName;
        private final TextView songArtist;
        private final CustomButton addToPlaylist;

        // Costruttore che inizializza i riferimenti agli elementi della playlist
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.song_image);
            songName = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            addToPlaylist = itemView.findViewById(R.id.remove_button);
            Drawable addSong = ContextCompat.getDrawable(itemView.getContext(), R.drawable.add_song);
            addToPlaylist.setBackground(addSong);
        }
    }

}
