package com.soundlab.app.controller;

public interface ControllerCallback<T> {
    void onSuccess(T result);
    void onFailed(String errorMessage);
}
