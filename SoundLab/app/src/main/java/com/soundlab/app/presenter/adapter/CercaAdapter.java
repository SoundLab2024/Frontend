package com.soundlab.app.presenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;

import java.util.ArrayList;

import com.soundlab.app.model.Song;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.fragment.SearchFragment;

public class CercaAdapter extends RecyclerView.Adapter<CercaAdapter.ViewHolder> {
    private final ArrayList<Song> songList;
    private final SearchFragment searchFragment;

    // Costruttore
    public CercaAdapter(ArrayList<Song> songs, SearchFragment searchFragment) {
        this.songList = songs;
        this.searchFragment = searchFragment;
    }

    // Creare ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView artistTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.song_title);
            artistTextView = view.findViewById(R.id.song_artist);
        }
    }

    // Override i metodi necessari
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(Utilities.ottieniArtistiDellaTracciaInStringa(song));

        holder.itemView.setOnClickListener(view -> searchFragment.loadPlayer(songList.indexOf(song), songList));

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
