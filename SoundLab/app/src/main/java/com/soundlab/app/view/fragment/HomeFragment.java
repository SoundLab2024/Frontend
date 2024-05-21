package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_EMAIL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soundlab.R;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.LibraryFromIdResponse;
import com.soundlab.app.presenter.api.response.RecentlyListenedResponse;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.CustomCardView;
import com.soundlab.app.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {

    private String userName;
    private Long libId;
    private String token;
    private int playlistsNumber;
    private List<Playlist> playlists = new ArrayList<>();
    private Library lib;
    private final String TAG = "HOME_FRAGMENT";
    private String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflater per trovare gli elementi
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HomeFragment", "onCreateView called");

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
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        String authToken = "Bearer " + token;

        Call<List<RecentlyListenedResponse>> call = apiService.recentlyListened(authToken, userEmail);
        call.enqueue(new Callback<List<RecentlyListenedResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecentlyListenedResponse>> call, @NonNull Response<List<RecentlyListenedResponse>> response) {
                if (response.isSuccessful()) {
                    // Riuscito, prendiamo il body dalla risposta
                    List<RecentlyListenedResponse> payload = response.body();

                    Log.d(TAG, "RecentlyListenedResponse:  " + payload.toString());

                    List<Song> songs = extractSongs(payload);

                    updateRecentlyListenedUI(songs);
                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(getActivity(), "Recently Listened non recuperate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecentlyListenedResponse>> call, @NonNull Throwable t) {
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d(TAG, "-recentlyListened- Richiesta fallita.");
            }
        });
    }

    private List<Song> extractSongs(List<RecentlyListenedResponse> recentlyListenedResponses) {
        List<Song> songs = new ArrayList<>();
        if (recentlyListenedResponses != null) {
            for (RecentlyListenedResponse recentlyListenedResponse : recentlyListenedResponses) {
                if (recentlyListenedResponse.getSong() != null) {
                    Song song = recentlyListenedResponse.getSong();
                    songs.add(song);
                }
            }
        }
        return songs;
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

    private void returnLib() { // Richiesta per il retrieve della libreria contenente le playlist

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        String authToken = "Bearer " + token;

        Call<LibraryFromIdResponse> call = apiService.userLib(authToken, libId);

        call.enqueue(new Callback<LibraryFromIdResponse>() {
            @Override
            public void onResponse(@NonNull Call<LibraryFromIdResponse> call, @NonNull Response<LibraryFromIdResponse> response) {
                if (response.isSuccessful()) {
                    // Riuscito, prendiamo il body dalla risposta
                    LibraryFromIdResponse payload = response.body();

                    // Gestiamo le risposte del body
                    libId = payload.getId();
                    playlistsNumber = payload.getPlaylistsNumber();
                    playlists = payload.getPlaylists();

                    // ritorno la libreria
                    lib = Library.getInstance();
                    lib.setId(libId);
                    lib.setPlaylistNumber(playlistsNumber);
                    lib.setPlaylists(playlists);
                    Library.getInstance().setInitialized(true);

                    // prova
                    Log.d(TAG, "Id libreria: " + libId);
                    Log.d(TAG, "Numero playlists: " + playlistsNumber);


                    List<Playlist> favouritePlaylists = findFavoritePlaylists(playlists);
                    updateFavouritePlaylistUI(findFavoritePlaylists(favouritePlaylists));
                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(getActivity(), "Libreria non recuperata.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibraryFromIdResponse> call, @NonNull Throwable t) {
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d(TAG, "Richiesta fallita.");
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


