package com.soundlab.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Library implements Serializable {
    private static final Library instance = new Library();
    private Long id;
    private List<Playlist> playlists;
    private int playlistNumber;
    private boolean initialized;

    private Library() {
        this.playlists = new ArrayList<>();
        this.playlistNumber = 0;
        this.initialized = false;
    }

    public static Library getInstance() {
        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int getPlaylistNumber() {
        return playlistNumber;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public void setPlaylistNumber(int playlistNumber) {
        this.playlistNumber = playlistNumber;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
