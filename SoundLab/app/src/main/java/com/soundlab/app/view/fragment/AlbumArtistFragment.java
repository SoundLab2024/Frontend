package com.soundlab.app.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soundlab.R;
import com.soundlab.app.model.Album;
import com.soundlab.app.model.Artist;
import com.soundlab.app.presenter.adapter.AlbumArtistAdapter;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;

public class AlbumArtistFragment extends Fragment {

    private final Artist artist;

    AlbumArtistFragment(Artist artist) {
        this.artist = artist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("AlbumArtistFragment", "onCreateView called");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_inartist, container, false);

        ArrayList<Album> albumArrayList = new ArrayList<>();

        // TODO: Caricare gli album dell'artista

        Album album1 = new Album(1, "Album1", 2020, artist);
        albumArrayList.add(album1);
        Album album2 = new Album(2, "Album2", 2018, artist);
        albumArrayList.add(album2);

        RecyclerView recyclerView = view.findViewById(R.id.a_recyclerView);

        // Inizializza l'adapter e passa la lista di album
        AlbumArtistAdapter albumArtistAdapter = new AlbumArtistAdapter(this, albumArrayList);
        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(albumArtistAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadAlbumFragment(Album album){
        Bundle bundle = new Bundle();
        bundle.putSerializable("album", album);

        Fragment albumFragment = new AlbumFragment();
        albumFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(albumFragment, Utilities.albumFragmentTag);
        }
    }

}