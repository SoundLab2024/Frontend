package com.soundlab.app.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.CercaAdapter;
import com.soundlab.app.utils.Utilities;


public class SearchFragment extends Fragment {
    //aggiunta
    private List<Song> allSongs;
    private List<Song> displayedSongs;
    private CercaAdapter cercaAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d("SearchFragment", "onCreateView called");
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);

        // Inizializza la lista delle canzoni
        allSongs = new ArrayList<>();

        // Aggiunge le tracce alla lista ed aggiunge alla traccia i relativi artisti
        Song song1 = new Song(1, "Canzone1","Rock",  R.drawable.cover_default);
        song1.addArtist(new Artist(7, "Gio", new Date(5 / 1985), "Inghilterra"));
        allSongs.add(song1);

        Song song2 = new Song(2, "Canzone2", "Rock", R.drawable.cover_default);
        song2.addArtist(new Artist(36, "Ale", new Date(7 / 1995), "Italia"));
        song2.addArtist(new Artist(31, "Ren", new Date(3 / 1998), "Italia"));
        allSongs.add(song2);


        // Inizializza la RecyclerView e l'Adapter
        RecyclerView recyclerView = view.findViewById(R.id.songs_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        displayedSongs = new ArrayList<>();
        cercaAdapter = new CercaAdapter(displayedSongs);
        recyclerView.setAdapter(cercaAdapter);

        // Inizializza la SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Chiamato quando l'utente preme il pulsante di invio
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Chiamato ogni volta che l'utente modifica il testo nella SearchView
                filterResults(newText);
                return true;
            }
        });
        return view;
    }

    private void filterResults(String query) {
        displayedSongs.clear();
        if (!TextUtils.isEmpty(query)) {
            for (Song song : allSongs) {
                // Controlla se il nome della canzone contiene la query
                if (song.getName().toLowerCase().contains(query.toLowerCase())) {
                    displayedSongs.add(song);
                } else {
                    // Controlla ogni artista associato alla canzone
                    for (Artist artist : song.getArtists()) {
                        if (artist.getName().toLowerCase().contains(query.toLowerCase())) {
                            displayedSongs.add(song);
                            break; // Trovato un artista corrispondente, non è necessario cercare altri artisti per questa canzone
                        }
                    }
                }
            }
        }
        cercaAdapter.notifyDataSetChanged();
    }
}