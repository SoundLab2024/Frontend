package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
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
import androidx.fragment.app.Fragment;

import com.example.soundlab.R;
import com.soundlab.app.controller.ControllerCallback;
import com.soundlab.app.controller.LibraryController;
import com.soundlab.app.controller.ListeningController;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.CustomCardView;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class HomeFragment extends Fragment {

    private String userName;
    private Long libId;
    private String token;
    private String userEmail;
    private LibraryController libraryController;
    private ListeningController listeningController;
    private final Fragment homeFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflater per trovare gli elementi
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HomeFragment", "onCreateView called");

        libraryController = new LibraryController();
        listeningController = new ListeningController();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_NAME, null);
        userEmail = sharedPreferences.getString(USER_EMAIL, null);
        libId = sharedPreferences.getLong(USER_LIB, -1);
        token = sharedPreferences.getString(USER_TOKEN, null);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);

        updateUI();

        TextView welcomeView = view.findViewById(R.id.welcome);
        welcomeView.setText("Ehy " + userName + ", cosa vuoi ascoltare oggi?");

    }

    private void updateUI() {
        removePlaylistUI();
        removeRecentlyListenedUI();

        updateGenreUI();
        returnRecentlyListened();

        if (!Library.getInstance().isInitialized()) {
            returnLib();
        } else {
            List<Playlist> favouritePlaylists = findFavoritePlaylists(Library.getInstance().getPlaylists());
            updateFavouritePlaylistUI(favouritePlaylists);
        }
    }

    private void updateGenreUI() {
        View view = getView();
        if (view != null) {

            String[] musicGenres = {
                    "Rock",
                    "Pop",
                    "Jazz",
                    "Classical",
                    "Hip Hop",
                    "Electronic"
            };

            List<CustomButton> genreButtons = new ArrayList<>();
            genreButtons.add(view.findViewById(R.id.genre1));
            genreButtons.add(view.findViewById(R.id.genre2));
            genreButtons.add(view.findViewById(R.id.genre3));

            Random random = new Random();
            Set<String> selectedGenres = new HashSet<>();

            for (CustomButton button : genreButtons) {
                String randomGenre;
                do {
                    int randomIndex = random.nextInt(musicGenres.length);
                    randomGenre = musicGenres[randomIndex];
                } while (selectedGenres.contains(randomGenre));  // Controlla se il genere è già stato selezionato
                selectedGenres.add(randomGenre);  // Aggiunge il genere selezionato al set

                button.setText(randomGenre);
            }
        }
    }


    private void returnRecentlyListened() {
        listeningController.retrieveRecentlyListened(token, userEmail, new ControllerCallback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs) {
                updateRecentlyListenedUI(songs);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(homeFragment, errorMessage);
            }
        });
    }


    private void updateRecentlyListenedUI(List<Song> songs) {
        View view = getView();
        if (view != null) {
            List<TextView> songsTextViews = new ArrayList<>();
            songsTextViews.add(view.findViewById(R.id.title1));
            songsTextViews.add(view.findViewById(R.id.title2));
            songsTextViews.add(view.findViewById(R.id.title3));

            List<CustomCardView> songsCardViews = new ArrayList<>();
            songsCardViews.add(view.findViewById(R.id.music_cardView1));
            songsCardViews.add(view.findViewById(R.id.music_cardView2));
            songsCardViews.add(view.findViewById(R.id.music_cardView3));


            for (int i = 0; i < songs.size(); i++) {
                CustomCardView cardView = songsCardViews.get(i);
                TextView textView = songsTextViews.get(i);
                Song song = songs.get(i);

                cardView.setVisibility(View.VISIBLE);
                textView.setText(song.getTitle());

                //TODO: Caricare il player con le canzoni
            }
            if (songs.size() == 3) {
                HorizontalScrollView scrollView = view.findViewById(R.id.scrollView_recentListen);
                scrollView.setOnTouchListener(null); //Rendi la scroll View scrollabile
            }
        }
    }

    private void removeRecentlyListenedUI() {
        View view = getView();
        if (view != null) {
            HorizontalScrollView scrollView = view.findViewById(R.id.scrollView_recentListen);
            scrollView.setOnTouchListener((v, event) -> true); //Rendi la scroll View non scrollabile

            List<CustomCardView> recenlyListenCardViews = new ArrayList<>();
            recenlyListenCardViews.add(view.findViewById(R.id.music_cardView1));
            recenlyListenCardViews.add(view.findViewById(R.id.music_cardView2));
            recenlyListenCardViews.add(view.findViewById(R.id.music_cardView3));

            for (CustomCardView cardView : recenlyListenCardViews) {
                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void returnLib() {
        libraryController.retrieveLibrary(token, libId, new ControllerCallback<Library>() {
            @Override
            public void onSuccess(Library library) {
                List<Playlist> playlists = library.getPlaylists();
                List<Playlist> favouritePlaylists = findFavoritePlaylists(playlists);
                updateFavouritePlaylistUI(favouritePlaylists);
            }

            @Override
            public void onFailed(String errorMessage) {
                showErrorMessage(homeFragment, errorMessage);
            }
        });
    }

    private void removePlaylistUI() {
        View view = getView();
        if (view != null) {
            HorizontalScrollView scrollView = view.findViewById(R.id.scrollView_favPlaylist);
            scrollView.setOnTouchListener((v, event) -> true); //Rendi la scroll View non scrollabile

            List<CustomCardView> playlistCardViews = new ArrayList<>();
            playlistCardViews.add(view.findViewById(R.id.playlist_cardView1));
            playlistCardViews.add(view.findViewById(R.id.playlist_cardView2));
            playlistCardViews.add(view.findViewById(R.id.playlist_cardView3));

            for (CustomCardView cardView : playlistCardViews) {
                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateFavouritePlaylistUI(List<Playlist> playlists) {
        View view = getView();
        if (view != null) {
            List<TextView> playlistTextViews = new ArrayList<>();
            playlistTextViews.add(view.findViewById(R.id.playlist_text1));
            playlistTextViews.add(view.findViewById(R.id.playlist_text2));
            playlistTextViews.add(view.findViewById(R.id.playlist_text3));

            List<CustomCardView> playlistCardViews = new ArrayList<>();
            playlistCardViews.add(view.findViewById(R.id.playlist_cardView1));
            playlistCardViews.add(view.findViewById(R.id.playlist_cardView2));
            playlistCardViews.add(view.findViewById(R.id.playlist_cardView3));


            for (int i = 0; i < playlists.size(); i++) {
                CustomCardView cardView = playlistCardViews.get(i);
                TextView textView = playlistTextViews.get(i);
                Playlist playlist = playlists.get(i);

                cardView.setVisibility(View.VISIBLE);
                textView.setText(playlist.getName());

                int index = i;
                cardView.setOnClickListener(v -> loadPlaylistFragment(playlists.get(index)));
            }
            if (playlists.size() == 3) {
                HorizontalScrollView scrollView = view.findViewById(R.id.scrollView_favPlaylist);
                scrollView.setOnTouchListener(null); //Rendi la scroll View scrollabile
            }
        }
    }

    private List<Playlist> findFavoritePlaylists(List<Playlist> playlists) {
        List<Playlist> favoritePlaylists = new ArrayList<>();

        for (Playlist playlist : playlists) {
            if (playlist.isFavourite()) {
                favoritePlaylists.add(playlist);

                if (favoritePlaylists.size() >= 3) {
                    break;
                }
            }
        }

        return favoritePlaylists;
    }


    public void loadPlaylistFragment(Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlist", playlist);

        Fragment playlistFragment = new PlaylistFragment();
        playlistFragment.setArguments(bundle);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomNavigationView();
            ((MainActivity) getActivity()).replaceFragmentWithoutPopStack(playlistFragment, Utilities.playlistFragmentTag);
        }
    }

}


