package com.soundlab.app.view.fragment;

import android.opengl.Visibility;
import android.os.Bundle;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.soundlab.R;
import com.soundlab.app.model.Library;
import com.soundlab.app.model.Playlist;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.LibraryFromIdResponse;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.utils.Utilities;
import com.soundlab.app.view.CustomCardView;
import com.soundlab.app.view.activity.MainActivity;

import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_LIB;
import static com.soundlab.app.utils.Constants.USER_NAME;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {

    private String userName;
    private Long libId;
    private String token;
    private int playlistsNumber;
    private TextView welcomeView;
    private List<Playlist> playlists = new ArrayList<>();
    private Library lib;
    private String TAG = "HOME_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflater per trovare gli elementi
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HomeFragment", "onCreateView called");

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_NAME, null);
        libId = sharedPreferences.getLong(USER_LIB, -1);
        token = sharedPreferences.getString(USER_TOKEN, null);

        returnLib();

        welcomeView = view.findViewById(R.id.welcome);
        welcomeView.setText("Ehy " + userName + ", cosa vuoi ascoltare oggi?");

        return view;
    }


    private void returnLib() { // Richiesta per il retrieve della libreria contenente le playlist

        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        String authToken = "Bearer " + token;

        Call<LibraryFromIdResponse> call = apiService.userLib(authToken, libId);
        call.enqueue(new Callback<LibraryFromIdResponse>() {
            @Override
            public void onResponse(Call<LibraryFromIdResponse> call, Response<LibraryFromIdResponse> response) {
                if (response.isSuccessful()) {
                    // Riuscito, prendiamo il body dalla risposta
                    LibraryFromIdResponse payload = response.body();

                    // Gestiamo le risposte del body
                    libId = payload.getId();
                    playlistsNumber = payload.getPlaylistsNumber();
                    playlists = payload.getPlaylists();

                    // ritorno la libreria
                    lib = new Library(playlists, playlistsNumber);

                    // prova
                    Log.d(TAG, "Id libreria: " + libId);
                    Log.d(TAG, "Numero playlists: " + playlistsNumber);
                    Log.d(TAG, "Nome: " + lib.getPlaylists().get(0).getName());

                    updateFavouritePlaylistUI(findFavoritePlaylists(playlists));

                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(getActivity(), "Libreria non recuperata.", Toast.LENGTH_SHORT).show();
                    removePlaylistUI();
                }
            }

            @Override
            public void onFailure(Call<LibraryFromIdResponse> call, Throwable t) {
                removePlaylistUI();
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d(TAG, "Richiesta fallita.");
            }
        });

    }

    private void removePlaylistUI(){
        if (getView()!=null) {
            CustomCardView[] playlistCardViews = new CustomCardView[3];
            playlistCardViews[0] = requireView().findViewById(R.id.playlist_cardView1);
            playlistCardViews[1] = requireView().findViewById(R.id.playlist_cardView2);
            playlistCardViews[2] = requireView().findViewById(R.id.playlist_cardView3);


            for (int i = 0; i < 3; i++) {
                playlistCardViews[i].setVisibility(View.INVISIBLE);
            }
        }

    }

    private void updateFavouritePlaylistUI(List<Playlist> playlists) {
        TextView[] playlistTextViews = new TextView[3];
        playlistTextViews[0] = requireView().findViewById(R.id.playlist_text1);
        playlistTextViews[1] = requireView().findViewById(R.id.playlist_text2);
        playlistTextViews[2] = requireView().findViewById(R.id.playlist_text3);

        CustomCardView[] playlistCardViews = new CustomCardView[3];
        playlistCardViews[0] = requireView().findViewById(R.id.playlist_cardView1);
        playlistCardViews[1] = requireView().findViewById(R.id.playlist_cardView2);
        playlistCardViews[2] = requireView().findViewById(R.id.playlist_cardView3);



        for (int i = 0; i<playlists.size(); i++) {
            playlistCardViews[i].setVisibility(View.VISIBLE);
            playlistTextViews[i].setText(playlists.get(i).getName());
            int index = i;
            playlistCardViews[i].setOnClickListener(v -> loadPlaylistFragment(playlists.get(index)));
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Imposta il colore della barra di stato quando la vista è creata
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);

        removePlaylistUI();

    }
}