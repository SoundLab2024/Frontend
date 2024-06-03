package com.soundlab.app.presenter.api.endpoint;

import com.soundlab.app.model.Playlist;
import com.soundlab.app.model.Song;
import com.soundlab.app.presenter.api.request.ChangePasswordRequest;
import com.soundlab.app.presenter.api.request.DeleteSongRequest;
import com.soundlab.app.presenter.api.request.InsertPlaylistRequest;
import com.soundlab.app.presenter.api.request.ListenRequest;
import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.request.RegisterRequest;
import com.soundlab.app.presenter.api.response.LibraryFromIdResponse;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.response.RecentlyListenedResponse;
import com.soundlab.app.presenter.api.response.RetriveSongResponse;
import com.soundlab.app.presenter.api.response.UserPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("authentication/authenticate")
    Call<UserPayload> loginUser(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @POST("authentication/register")
    Call<UserPayload> registerUser(@Body RegisterRequest registerRequest);

    @Headers("Content-Type: application/json")
    @GET("data/libs/{id}")
    Call<LibraryFromIdResponse> userLib(@Header("Authorization") String authToken, @Path("id") Long id);

    @Headers("Content-Type: application/json")
    @POST("data/playlist/createPl")
    Call<Payload> createPlaylist(@Header("Authorization") String authToken, @Body InsertPlaylistRequest insertPlaylistRequest);

    @Headers("Content-Type: application/json")
    @DELETE("data/playlist/{id}")
    Call<Payload> deletePlaylist(@Header("Authorization") String authToken, @Path("id") Long id);

    @Headers("Content-Type: application/json")
    @POST("data/playlist/renamePl")
    Call<Payload> modifyPlaylist(@Header("Authorization") String authToken, @Body InsertPlaylistRequest insertPlaylistRequest);

    @Headers("Content-Type: application/json")
    @POST("data/playlist/toggleFav/{id}")
    Call<Payload> favPlaylist(@Header("Authorization") String authToken, @Path("id") Long id);

    @Headers("Content-Type: application/json")
    @DELETE("data/users/{id}")
    Call<Payload> deleteUser(@Header("Authorization") String authToken, @Path("id") String id);

    @Headers("Content-Type: application/json")
    @GET("data/listenings/recently/{id}")
    Call<List<RecentlyListenedResponse>> recentlyListened(@Header("Authorization") String authToken, @Path("id") String id);

    @Headers("Content-Type: application/json")
    @GET("data/song/search/{prefix}")
    Call<ArrayList<Song>> searchSong(@Header("Authorization") String authToken, @Path("prefix") String prefix);

    @Headers("Content-Type: application/json")
    @POST("authentication/changepw")
    Call<Payload> changePw(@Header("Authorization") String authToken, @Body ChangePasswordRequest changePasswordRequest);

    @Headers("Content-Type: application/json")
    @POST("data/playlist/addToPl")
    Call<Payload> insertSong(@Header("Authorization") String authToken, @Body Map<String, Long> body);

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "data/playlist/delFrPl", hasBody = true)
    Call<Payload> deleteSong(@Header("Authorization") String authToken, @Body DeleteSongRequest deleteSongRequest);

    @Headers("Content-Type: application/json")
    @GET("data/playlist/{id}")
    Call<RetriveSongResponse> retriveSong(@Header("Authorization") String authToken, @Path("id") Long id);

    @Headers("Content-Type: application/json")
    @GET("data/playlist/isAdded/{idLib}/{idSn}")
    Call<List<Playlist>> playlistsFromAddedSong(@Header("Authorization") String authToken, @Path("idLib") Long idLib, @Path("idSn") Long idSn);

    @Headers("Content-Type: application/json")
    @GET("data/song/search/genre/{prefix}")
    Call<List<Song>> getSongsFromGenre(@Header("Authorization") String authToken, @Path("prefix") String genre);

    @Headers("Content-Type: application/json")
    @GET("data/song/recentlyFour/")
    Call<List<Song>> getMostPlayed(@Header("Authorization") String authToken);

    @Headers("Content-Type: application/json")
    @POST("data/listenings/new")
    Call<Payload> postListen(@Header("Authorization") String authToken, @Body ListenRequest listenRequest);
}
