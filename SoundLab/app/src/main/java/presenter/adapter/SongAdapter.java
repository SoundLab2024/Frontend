package presenter.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;

import java.util.ArrayList;

import model.Song;
import view.fragment.PlaylistFragment;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private final ArrayList<Song> songArrayList;
    private final PlaylistFragment playlistFragment;

    // Costruttore per inizializzare l'adapter con la lista di playlist
    public SongAdapter(PlaylistFragment playlistFragment, ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
        this.playlistFragment = playlistFragment;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
