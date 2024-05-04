package com.soundlab.app.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String email;
    private final String username;
    private final String role;
    private Library lib;

    public User(String email, String username, String role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Library getLibreria() {
        return lib;
    }

    public void setLibreria(Library libreria) {
        this.lib = libreria;
    }
}
