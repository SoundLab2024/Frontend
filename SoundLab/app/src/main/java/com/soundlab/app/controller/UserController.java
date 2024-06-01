package com.soundlab.app.controller;

import static com.soundlab.app.utils.Constants.BASE_URL;

import androidx.annotation.NonNull;

import com.soundlab.app.presenter.api.endpoint.ApiService;
import com.soundlab.app.presenter.api.request.ChangePasswordRequest;
import com.soundlab.app.presenter.api.request.LoginRequest;
import com.soundlab.app.presenter.api.request.RegisterRequest;
import com.soundlab.app.presenter.api.response.Payload;
import com.soundlab.app.presenter.api.response.UserPayload;
import com.soundlab.app.presenter.api.retrofit.RetrofitClient;

import java.sql.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserController {

    private final ApiService apiService;

    public UserController() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        this.apiService = retrofit.create(ApiService.class);
    }

    public void register(String email, String password, String username, String scelta, Date data, ControllerCallback<UserPayload> callback) {
        RegisterRequest registerRequest = new RegisterRequest(email, password, username, scelta, data);
        Call<UserPayload> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<UserPayload>() {
            @Override
            public void onResponse(@NonNull Call<UserPayload> call, @NonNull Response<UserPayload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Registrazione fallita, riprova. " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPayload> call, @NonNull Throwable t) {
                callback.onFailed("Regiastrazione fallita, riprova. " + t.getMessage());
            }
        });

    }

    public void login(String email, String password, ControllerCallback<UserPayload> callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<UserPayload> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<UserPayload>() {
            @Override
            public void onResponse(@NonNull Call<UserPayload> call, @NonNull Response<UserPayload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed("Login fallito, riprova. " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPayload> call, @NonNull Throwable t) {
                callback.onFailed("Login fallito, riprova. " + t.getMessage());
            }
        });

    }

    public void changePassword(String token, String email, String oldPassword, String newPassword, ControllerCallback<Payload> callback) {
        String authToken = "Bearer " + token;

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(email, oldPassword, newPassword);
        Call<Payload> call = apiService.changePw(authToken, changePasswordRequest);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    Payload payload = response.body();
                    if (payload != null) {
                        if (payload.getStatusCode() == 200) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onFailed(payload.getMsg());
                        }
                    }
                    else {
                        callback.onFailed("Impossibile cambiare la password");
                    }
                } else {
                    callback.onFailed(response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed(t.getMessage());
            }
        });
    }


    public void deleteUser(String email, String token, ControllerCallback<Payload> callback) {
        String authToken = "Bearer " + token;

        Call<Payload> call = apiService.deleteUser(authToken, email);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(@NonNull Call<Payload> call, @NonNull Response<Payload> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payload> call, @NonNull Throwable t) {
                callback.onFailed(t.getMessage());
            }
        });

    }


}
