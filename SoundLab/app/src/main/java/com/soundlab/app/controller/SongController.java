package com.soundlab.app.controller;

import static com.soundlab.app.utils.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SongController {
    private final ApiService apiService;

    public SongController() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        this.apiService = retrofit.create(ApiService.class);
    }

    public void searchSong(String token, String prefix, ControllerCallback<List<Song>> callback) {
        String authToken = "Bearer " + token;

        Call<ArrayList<Song>> call = apiService.searchSong(authToken, prefix);

        call.enqueue(new Callback<ArrayList<Song>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Song>> call, @NonNull Response<ArrayList<Song>> response) {
                if (response.isSuccessful()) {
                    List<Song> songs = response.body();
                    callback.onSuccess(songs);
                } else {
                    callback.onFailed("Impossible mostrare le canzoni. " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Song>> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile mostrare le canzoni" + t.getMessage());
            }
        });
    }


}
