package com.soundlab.app.controller;

import static com.soundlab.app.utils.Constants.BASE_URL;
import static com.soundlab.app.utils.Utilities.setTrackAndImage;

import androidx.annotation.NonNull;

import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.DeleteSongRequest;
import com.soundlab.app.presenter.api.request.InsertPlaylistRequest;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.response.RetriveSongResponse;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlaylistController {
    private final ApiService apiService;

    public PlaylistController() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        this.apiService = retrofit.create(ApiService.class);
    }

    public void createPlaylist(String token, String name, String genre, Long libraryId, ControllerCallback<Long> callback) {
        InsertPlaylistRequest request = new InsertPlaylistRequest(name, genre, libraryId);
        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.createPlaylist(authToken, request);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    Payload payload = response.body();
                    Long playlistId = Long.parseLong(payload.getMsg());
                    callback.onSuccess(playlistId);
                } else {
                    callback.onFailed("Impossibile aggiungere la playlist. " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile aggiungere la playlist. " + t.getMessage());
            }
        });
    }

    public void updateFavouritePlaylist(String token, Long playlistId, ControllerCallback<Payload> callback) {
        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.favPlaylist(authToken, playlistId);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Impossibile aggiornare lo stato di preferenza della playlist. " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile aggiornare lo stato di preferenza della playlist. Errore: " + t.getMessage());
            }
        });
    }

    public void deletePlaylist(String token, Long playlistId, ControllerCallback<Payload> callback) {
        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.deletePlaylist(authToken, playlistId);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Impossibile rimuovere la playlist. Errore: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile rimuovere la playlist. Errore: " + t.getMessage());
            }
        });
    }

    public void modifyPlaylist(String token, String newName, String newGenre, Long playlistId, ControllerCallback<Payload> callback) {
        InsertPlaylistRequest renamePlaylistRequest = new InsertPlaylistRequest(newName, newGenre, playlistId);
        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.modifyPlaylist(authToken, renamePlaylistRequest);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Impossibile modificare la playlist. Errore: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile modificare la playlist. Errore: " + t.getMessage());
            }
        });
    }

    public void retriveSongs(String token, Long playlistId, ControllerCallback<List<Song>> callback) {
        String authToken = "Bearer " + token;

        Call<RetriveSongResponse> call = apiService.retriveSong(authToken, playlistId);
        call.enqueue(new Callback<RetriveSongResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetriveSongResponse> call, @NonNull Response<RetriveSongResponse> response) {
                if (response.isSuccessful()) {
                    RetriveSongResponse retriveSongResponse = response.body();
                    List<Song> songs = retriveSongResponse.getSongs();
                    setTrackAndImage(songs);
                    callback.onSuccess(songs);
                } else {
                    callback.onFailed("Impossibile recuperare le canzoni: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetriveSongResponse> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile recuperare le canzoni: " + t.getMessage());
            }
        });
    }

    public void insertSong(String token, Long songId, Long playlistId, ControllerCallback<Payload> callback) {
        Map<String, Long> body = new HashMap<>();
        body.put("idPlaylist", playlistId);
        body.put("idSong", songId);

        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.insertSong(authToken, body);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Impossibile aggiungere la canzone alla playlist. Errore: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile aggiungere la canzone alla playlist. Errore: " + t.getMessage());
            }
        });
    }

    public void deleteSong(String token, Long songId, Long playlistId, ControllerCallback<Payload> callback) {
        DeleteSongRequest deleteSongRequest = new DeleteSongRequest(songId, playlistId);

        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.deleteSong(authToken, deleteSongRequest);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Impossibile rimuovere la canzone alla playlist. Errore: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile rimuovere la canzone alla playlist. Errore: " + t.getMessage());
            }
        });
    }

    public void playlistsFromAddedSong(String token, Long songId, Long libraryId, ControllerCallback<List<Playlist>> callback) {
        String authToken = "Bearer " + token;

        Call<List<Playlist>> call = apiService.playlistsFromAddedSong(authToken, libraryId, songId);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Playlist>> call, @NonNull Response<List<Playlist>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Impossibile recuperare le playlist. Errore: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Playlist>> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile recuperare le playlist. Errore: " + t.getMessage());
            }
        });
    }
}
