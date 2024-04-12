package com.soundlab.app.presenter.api.endpoint;

import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.request.RegisterRequest;
import com.soundlab.app.presenter.api.response.UserPayload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("authentication/authenticate")
    Call<UserPayload> loginUser(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @POST("authentication/register")
    Call<UserPayload> registerUser(@Body RegisterRequest registerRequest);

}
