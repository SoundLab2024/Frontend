package presenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;

import java.util.ArrayList;
import java.util.List;

import model.Artist;
import model.Song;
import utils.Utilities;

public class CercaAdapter extends RecyclerView.Adapter<CercaAdapter.ViewHolder> {
    private List<Song> songList;

    // Costruttore
    public CercaAdapter(List<Song> songs) {
        this.songList = songs;
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
        holder.titleTextView.setText(song.getName());
        holder.artistTextView.setText(Utilities.ottieniArtistiDellaTracciaInStringa(song));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
