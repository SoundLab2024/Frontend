package com.soundlab.app.model;
import java.util.List;

public class Library {

    private List<Playlist> playlists;
    private int playlistNumber;

    public Library(List<Playlist> playlists){
        this.playlists = playlists;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int getPlaylistNumber() {
        return playlistNumber;
    }

    public void setPlaylistNumber(int playlistNumber) {
        this.playlistNumber = playlistNumber;
    }

}
