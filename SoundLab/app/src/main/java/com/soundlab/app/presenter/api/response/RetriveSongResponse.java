package com.soundlab.app.presenter.api.response;

import com.soundlab.app.model.Song;

import java.util.List;

public class RetriveSongResponse {
    List<Song> songs;

    public RetriveSongResponse(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
