package com.soundlab.app.presenter.api.response;

import com.soundlab.app.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class LibraryFromIdResponse {

    private Long id;
    private int playlistsNumber;
    List<Playlist> playlists;

    public LibraryFromIdResponse(Long id, int playlistsNumber, List<Playlist> playlists) {
        this.id = id;
        this.playlistsNumber = playlistsNumber;
        this.playlists = playlists;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPlaylistsNumber() {
        return playlistsNumber;
    }

    public void setPlaylistsNumber(int playlistsNumber) {
        this.playlistsNumber = playlistsNumber;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}
