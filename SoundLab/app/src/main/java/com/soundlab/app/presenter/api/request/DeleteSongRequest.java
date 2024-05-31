package com.soundlab.app.presenter.api.request;

public class DeleteSongRequest {
    private Long idSong;
    private Long idPlaylist;

    public DeleteSongRequest(Long idSong, Long idPlaylist) {
        this.idSong = idSong;
        this.idPlaylist = idPlaylist;
    }

    public Long getIdSong() {
        return idSong;
    }

    public void setIdSong(Long idSong) {
        this.idSong = idSong;
    }

    public Long getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(Long idPlaylist) {
        this.idPlaylist = idPlaylist;
    }
}
