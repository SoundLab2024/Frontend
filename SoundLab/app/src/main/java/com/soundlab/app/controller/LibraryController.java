package com.soundlab.app.controller;

import static com.soundlab.app.utils.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.soundlab.app.model.Library;
import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.response.LibraryFromIdResponse;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LibraryController {

    private final ApiService apiService;

    public LibraryController() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        this.apiService = retrofit.create(ApiService.class);
    }

    public void retrieveLibrary(String token, Long libId, ControllerCallback<Library> callback) {
        String authToken = "Bearer " + token;

        Call<LibraryFromIdResponse> call = apiService.userLib(authToken, libId);

        call.enqueue(new Callback<LibraryFromIdResponse>() {
            @Override
            public void onResponse(@NonNull Call<LibraryFromIdResponse> call, @NonNull Response<LibraryFromIdResponse> response) {
                if (response.isSuccessful()) {
                    LibraryFromIdResponse libraryFromIdResponse = response.body();

                    // Creiamo e impostiamo la libreria
                    Library library = Library.getInstance();
                    library.setId(libraryFromIdResponse.getId());
                    library.setPlaylistNumber(libraryFromIdResponse.getPlaylistsNumber());
                    library.setPlaylists(libraryFromIdResponse.getPlaylists());
                    library.setInitialized(true);


                    callback.onSuccess(library);
                } else {
                    callback.onFailed("Impossibile recuperare la libreria." + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibraryFromIdResponse> call, @NonNull Throwable t) {
                callback.onFailed("Impossibile recuperare la libreria. " + t.getMessage());
            }
        });
    }
}
