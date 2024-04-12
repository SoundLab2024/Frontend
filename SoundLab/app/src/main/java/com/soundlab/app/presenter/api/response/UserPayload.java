package com.soundlab.app.presenter.api.response;

public class UserPayload {

    private String email;
    private String username;
    private String role;
    private Long libraryId;
    private String token;

    public UserPayload(String email, String username, String role, Long libraryId, String token) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.libraryId = libraryId;
        this.token = token;
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
}
