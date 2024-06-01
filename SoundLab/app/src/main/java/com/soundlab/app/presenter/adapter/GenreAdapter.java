package com.soundlab.app.presenter.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.model.Song;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.fragment.GenreFragment;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private final List<Song> songs;
    private final GenreFragment genreFragment;

    public GenreAdapter(List<Song> songs, GenreFragment genreFragment) {
        this.songs = songs;
        this.genreFragment = genreFragment;
    }

    @NonNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_cardview, parent, false);
        return new GenreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(Utilities.ottieniArtistiDellaTracciaInStringa(song));

        holder.itemView.setOnClickListener(view -> genreFragment.loadPlayer(songs.indexOf(song), songs));
        holder.addToPlaylist.setOnClickListener(view -> genreFragment.loadAddToPlaylistFragment(song));
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView artistTextView;
        public CustomButton addToPlaylist;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.song_title);
            artistTextView = view.findViewById(R.id.song_artist);
            addToPlaylist = view.findViewById(R.id.remove_button);

            Drawable drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.add_song);
            addToPlaylist.setBackground(drawable);
        }
    }
}
