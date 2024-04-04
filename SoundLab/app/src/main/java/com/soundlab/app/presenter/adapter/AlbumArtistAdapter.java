package com.soundlab.app.presenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.model.Album;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.fragment.AlbumArtistFragment;

import java.util.ArrayList;

public class AlbumArtistAdapter extends RecyclerView.Adapter<AlbumArtistAdapter.ViewHolder> {

    private final AlbumArtistFragment albumArtistFragment;
    private final ArrayList<Album> albumArrayList;

    public AlbumArtistAdapter(AlbumArtistFragment albumArtistFragment, ArrayList<Album> albumArrayList){
        this.albumArtistFragment = albumArtistFragment;
        this.albumArrayList = albumArrayList;
    }

    @NonNull
    @Override
    public AlbumArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_cardview, parent, false);
        return new AlbumArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumArtistAdapter.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {

            Album selectedAlbum = albumArrayList.get(adapterPosition);

            //Popola il ViewHolder con i dati degli album
            holder.albumImage.setImageResource(selectedAlbum.getImage());
            holder.albumName.setText(selectedAlbum.getNome());
            String anno = String.valueOf(selectedAlbum.getAnno());
            holder.albumYear.setText(anno);

            holder.itemView.setOnClickListener(view -> albumArtistFragment.loadAlbumFragment(selectedAlbum));
        }

    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView albumImage;
        private final TextView albumName;
        private final TextView albumYear;

        // Costruttore che inizializza i riferimenti agli elementi degli album
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.song_image);
            albumName = itemView.findViewById(R.id.song_title);
            albumYear = itemView.findViewById(R.id.song_artist);

            CustomButton favourite = itemView.findViewById(R.id.remove_button);
            favourite.setVisibility(View.GONE);

        }
    }
}
