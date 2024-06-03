package com.soundlab.app.presenter.api.request;

public class ListenRequest {
    private String userId;
    private Long songId;

    public ListenRequest(String userId, Long songId) {
        this.userId = userId;
        this.songId = songId;
    }

    // Getter e setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }
}
