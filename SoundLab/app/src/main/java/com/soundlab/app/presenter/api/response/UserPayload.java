package com.soundlab.app.presenter.api.response;

public class UserPayload {

    private String email;
    private String username;
    private String role;
    private Long libraryId;
    private String token;
    private int statusCode;

    public UserPayload(String email, String username, String role, Long libraryId, String token, int statusCode) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.libraryId = libraryId;
        this.token = token;
        this.statusCode = statusCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
