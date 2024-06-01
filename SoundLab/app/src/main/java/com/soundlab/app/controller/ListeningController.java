package com.soundlab.app.controller;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Utilities.setTrackAndImage;

import androidx.annotation.NonNull;

import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.RecentlyListenedResponse;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListeningController {

    private final ApiService apiService;

    public ListeningController() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        this.apiService = retrofit.create(ApiService.class);
    }

    public void retrieveRecentlyListened(String token, String userEmail, ControllerCallback<List<Song>> callback) {
        String authToken = "Bearer " + token;

        Call<List<RecentlyListenedResponse>> call = apiService.recentlyListened(authToken, userEmail);

        call.enqueue(new Callback<List<RecentlyListenedResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecentlyListenedResponse>> call, @NonNull Response<List<RecentlyListenedResponse>> response) {
                if (response.isSuccessful()) {
                    List<RecentlyListenedResponse> payload = response.body();

                    List<Song> songs = extractSongs(payload);
                    setTrackAndImage(songs);

                    callback.onSuccess(songs);
                } else {
                    callback.onFailed("Impossibile mostrare gli ascolti recenti. " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecentlyListenedResponse>> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile mostrare gli ascolti recenti. " + t.getMessage());
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


}

