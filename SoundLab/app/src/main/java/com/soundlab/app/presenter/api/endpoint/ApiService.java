package com.soundlab.app.presenter.api.endpoint;

import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.request.RegisterRequest;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.response.UserFromTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("authentication/authenticate")
    Call<Payload> loginUser(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @POST("authentication/register")
    Call<Payload> registerUser(@Body RegisterRequest registerRequest);

    @Headers("Content-Type: application/json")
    @GET("data/users/retrieve/{token}")
    Call<UserFromTokenResponse> userToken(@Header("Authorization") String authToken, @Path("token") String token);

}
