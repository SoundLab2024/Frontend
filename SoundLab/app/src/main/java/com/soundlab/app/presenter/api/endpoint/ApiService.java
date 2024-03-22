package com.soundlab.app.presenter.api.endpoint;

import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.response.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("post/")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

}
