package com.soundlab.app.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soundlab.R;

import java.util.ArrayList;
import java.util.Date;

import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.SongArtistAdapter;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;

public class SongArtistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        // Crea una nuova lista di canzoni
        ArrayList<Song> songArrayList = new ArrayList<>();

        //TODO: Carica le tracce e relativi artisti dal backend

        // Aggiunge le tracce alla lista ed aggiunge alla traccia i relativi artisti
        Song song1 = new Song(1, "Canzone1","Rock", R.drawable.cover_default);
        song1.addArtist(new Artist(7, "Gio", new Date(5 / 1985), "Inghilterra"));
        songArrayList.add(song1);

        Song song2 = new Song(2, "Canzone2","Rock", R.drawable.cover_default);
        song2.addArtist(new Artist(36, "Ale", new Date(7 / 1995), "Italia"));
        song2.addArtist(new Artist(31, "Ren", new Date(3 / 1998), "Italia"));
        songArrayList.add(song2);

        RecyclerView recyclerView = view.findViewById(R.id.s_recyclerView);

        // Inizializza l'adapter e passa la lista di tracce
        SongArtistAdapter songArtistAdapter = new SongArtistAdapter(this, songArrayList);
        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songArtistAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadAddToPlaylistFragment(Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);

        Fragment addToPlaylistFragment = new AddToPlaylistFragment();
        addToPlaylistFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(addToPlaylistFragment, Utilities.addToPlaylistFragmentTag);
        }
    }

}