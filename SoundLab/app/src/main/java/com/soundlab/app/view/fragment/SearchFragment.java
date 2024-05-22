package com.soundlab.app.view.fragment;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Constants.USER_TOKEN;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundlab.R;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.adapter.CercaAdapter;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;
import com.soundlab.app.utils.Debouncer;
import com.soundlab.app.utils.Utilities;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SearchFragment extends Fragment {
    private final ArrayList<Song> displayedSongs = new ArrayList<>();
    private CercaAdapter cercaAdapter;
    private String token;
    private final Debouncer debouncer = new Debouncer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d("SearchFragment", "onCreateView called");
        Utilities.changeStatusBarColorFragment(this, R.color.dark_purple);

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
            callSearchSong(newText);
        }
    }

    private void callSearchSong(String prefix) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);
        String authToken = "Bearer " + token;

        Call<ArrayList<Song>> call = apiService.searchSong(authToken, prefix);

        call.enqueue(new Callback<ArrayList<Song>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Song>> call, @NonNull Response<ArrayList<Song>> response) {
                if (response.isSuccessful()) {
                    // Riuscito, prendiamo il body dalla risposta
                    displayedSongs.clear();
                    displayedSongs.addAll(response.body());
                    cercaAdapter.notifyDataSetChanged();
                } else {
                    // Gestisci la risposta di errore, es. credenziali non valide
                    Toast.makeText(getActivity(), "Canzoni non recuperate.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Song>> call, @NonNull Throwable t) {
                // Gestisci l'errore di rete o la conversione della risposta qui
                Log.d("SEARCH_FRAGMENT", "Richiesta fallita.");
            }
        });
    }

    public void loadPlayer(int songPosition, ArrayList<Song> songArrayList) {
        Utilities.loadPlayer(getActivity(), songPosition, songArrayList);
    }
}