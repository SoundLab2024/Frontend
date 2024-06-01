package com.soundlab.app.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.model.Album;
import com.soundlab.app.model.Artist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.AlbumAdapter;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.Date;

public class AlbumFragment extends Fragment {

    private Album album;
    private ArrayList<Song> songArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        Log.d("AlbumFragment", "onCreateView called");

        Bundle bundle = getArguments();

        if (bundle != null) {
            album = (Album) bundle.getSerializable("album");
        }

        // Ottiene la RecyclerView dal layout
        RecyclerView recyclerView = view.findViewById(R.id.songs_recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        // Crea una nuova lista di canzoni
        songArrayList = new ArrayList<>();

        //TODO: Carica le tracce dell'album e relativi artisti dal backend

        // Aggiunge le tracce alla lista ed aggiunge alla traccia i relativi artisti
        Song song1 = new Song(1, "Canzone1","Rock", R.raw.canzone);
        song1.addArtist(album.getArtist());
        songArrayList.add(song1);

        Song song2 = new Song(2, "Canzone2","Rock", R.raw.canzone);
        song2.addArtist(album.getArtist());
        song2.addArtist(new Artist(31, "Ren", new Date(3 / 1998), "Italia"));
        songArrayList.add(song2);

        // Inizializza l'adapter e passa la lista di tracce
        AlbumAdapter albumAdapter = new AlbumAdapter(songArrayList, this);
        // Imposta un layout manager per la RecyclerView (lista verticale)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        // Imposta il layout manager e l'adapter per la RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(albumAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utilities.changeStatusBarColorFragment(this, R.color.alternative_purple);

        if (album != null) {
            TextView albumName = view.findViewById(R.id.nomeAlbum);
            albumName.setText(album.getNome());
            TextView albumYear = view.findViewById(R.id.anno);
            albumYear.setText(String.valueOf(album.getAnno()));
            TextView albumArtist = view.findViewById(R.id.nomeArtista);
            albumArtist.setText(album.getArtist().getName());
            TextView numeroBrani = view.findViewById(R.id.numeroBrani);
            numeroBrani.setText(songArrayList.size() + " brani");
        }
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

    public void loadPlayer(int songPosition, ArrayList<Song> songArrayList) {
        Utilities.loadPlayer(getActivity(), songPosition, songArrayList);
    }

}