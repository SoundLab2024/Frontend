package com.soundlab.app.presenter.api.request;

public class InsertPlaylistRequest {
    private String name;
    private String genre;
    private Long libId;

    public InsertPlaylistRequest(String name, String genre, Long libId) {
        this.name = name;
        this.genre = genre;
        this.libId = libId;
    }

    // Getter and Setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for 'genre'
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // Getter and Setter for 'libraryId'
    public Long getLibId() {
        return libId;
    }

    public void setLibId(Long libId) {
        this.libId = libId;
    }
}
