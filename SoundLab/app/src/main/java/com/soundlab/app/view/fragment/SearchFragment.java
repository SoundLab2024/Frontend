package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.USER_TOKEN;
import static com.soundlab.app.utils.Utilities.showErrorMessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.SongController;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.CercaAdapter;
import com.soundlab.app.utils.Debouncer;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private final ArrayList<Song> displayedSongs = new ArrayList<>();
    private CercaAdapter cercaAdapter;
    private String token;
    private final Debouncer debouncer = new Debouncer();
    private SongController songController;
    private final Fragment searchFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d("SearchFragment", "onCreateView called");
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigationView();
        }

        songController = new SongController();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);

        // Inizializza la RecyclerView e l'Adapter
        RecyclerView recyclerView = view.findViewById(R.id.songs_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cercaAdapter = new CercaAdapter(displayedSongs, this);
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
                debouncer.debounce(() -> checkText(newText), 800);
                return true;
            }
        });
        return view;
    }

    private void checkText(String newText) {
        if (newText.isEmpty()) {
            displayedSongs.clear();
            cercaAdapter.notifyDataSetChanged();
        } else {
            returnSearchedSong(newText);
        }
    }

    private void returnSearchedSong(String prefix) {
        songController.searchSong(token, prefix, new ControllerCallback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs) {
                displayedSongs.clear();
                displayedSongs.addAll(songs);
                cercaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(searchFragment, errorMessage);
            }
        });
    }

    public void loadPlayer(int songPosition, ArrayList<Song> songArrayList) {
        Utilities.loadPlayer(getActivity(), songPosition, songArrayList);
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