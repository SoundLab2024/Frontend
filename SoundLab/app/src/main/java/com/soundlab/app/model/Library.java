package com.soundlab.app.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Library implements Serializable {
    private List<Playlist> playlists = new ArrayList<>();
    private int playlistNumber;

    public Library(List<Playlist> playlists, int playlistNumber){
        this.playlists = playlists;
        this.playlistNumber = playlistNumber;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int getPlaylistNumber() {
        return playlistNumber;
    }

}
