package com.soundlab.app.model;

public class User {
    private final int id;
    private final String name;
    private final boolean isAdmin;

    public User(int id, String name, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
