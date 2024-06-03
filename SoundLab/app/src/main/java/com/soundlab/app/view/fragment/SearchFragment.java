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
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.soundlab.app.view.CustomCardView;
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

        songController = new SongController();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPreferences.getString(USER_TOKEN, null);



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);
        hideNavigationView();

        initSearch();
        initAdapter();
        removeMostPlayedUI();
        getMostPlayedSongs();
    }

    private void hideNavigationView() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigationView();
        }
    }

    private void getMostPlayedSongs() {
        songController.getMostPlayed(token, new ControllerCallback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs) {
                updateMostPlayedUI(songs);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(searchFragment, errorMessage);
            }
        });
    }

    private void updateMostPlayedUI(List<Song> songs) {
        View view = getView();
        if (view != null) {
            List<TextView> songsTextViews = new ArrayList<>();
            songsTextViews.add(view.findViewById(R.id.title1));
            songsTextViews.add(view.findViewById(R.id.title2));
            songsTextViews.add(view.findViewById(R.id.title3));
            songsTextViews.add(view.findViewById(R.id.title4));

            List<CustomCardView> songsCardViews = new ArrayList<>();
            songsCardViews.add(view.findViewById(R.id.music_cardView1));
            songsCardViews.add(view.findViewById(R.id.music_cardView2));
            songsCardViews.add(view.findViewById(R.id.music_cardView3));
            songsCardViews.add(view.findViewById(R.id.music_cardView4));


            for (int i = 0; i < songs.size(); i++) {
                CustomCardView cardView = songsCardViews.get(i);
                TextView textView = songsTextViews.get(i);
                Song song = songs.get(i);

                cardView.setVisibility(View.VISIBLE);
                textView.setText(song.getTitle());

                int index = i;
                cardView.setOnClickListener((view1 -> Utilities.loadPlayer(requireActivity(), index, (ArrayList<Song>) songs)));
            }
            if (songs.size() == 4) {
                HorizontalScrollView scrollView = view.findViewById(R.id.scroll_mostPlay);
                scrollView.setOnTouchListener(null); //Rendi la scroll View scrollabile
            }
        }
    }

    private void removeMostPlayedUI() {
        View view = getView();

        if (view != null) {

            HorizontalScrollView scrollView = view.findViewById(R.id.scroll_mostPlay);
            scrollView.setOnTouchListener((v, event) -> true); //Rendi la scroll View non scrollabile

            List<CustomCardView> mostPlayedCardViews = new ArrayList<>();
            mostPlayedCardViews.add(view.findViewById(R.id.music_cardView1));
            mostPlayedCardViews.add(view.findViewById(R.id.music_cardView2));
            mostPlayedCardViews.add(view.findViewById(R.id.music_cardView3));
            mostPlayedCardViews.add(view.findViewById(R.id.music_cardView4));

            for (CustomCardView cardView : mostPlayedCardViews) {
                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initAdapter() {
        View view = getView();

        if (view != null) {
            // Inizializza la RecyclerView e l'Adapter
            RecyclerView recyclerView = view.findViewById(R.id.songs_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            cercaAdapter = new CercaAdapter(displayedSongs, this);
            recyclerView.setAdapter(cercaAdapter);
        }
    }


    private void initSearch() {
        View view = getView();
        // view è null, perchè???
        if (view != null) {
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
                    debouncer.debounce(() -> checkText(newText), 500);
                    return true;
                }
            });
        }
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