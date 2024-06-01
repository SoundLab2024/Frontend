package com.soundlab.app.model;

import com.example.soundlab.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Playlist implements Serializable {

    //private static final long serialVersionUID = 1L; // Numero di versione per la serializzazione
    private final Long id;
    private String name;
    private String genre;
    private int image;
    private boolean favourite;
    private List<Song> songs;
    private int songsNumber;

    public Playlist(Long id, String name, String genre, boolean favourite, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.image = R.drawable.playlist_default;
        this.favourite = favourite;
        this.songs = new ArrayList<>();
        this.songsNumber = 0;
    }

    public Playlist(Long id, String name, String genre) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.image = R.drawable.playlist_default;
        this.favourite = false;
        this.songs = new ArrayList<>();
        this.songsNumber = 0;
    }

    // Getter and Setter for 'id'
    public long getId() {
        return id;
    }

    // Getter and Setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for 'genere'
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // Getter and Setter for 'image'
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() { return songs; }

    // Getter and Setter for 'numberOfSongs'
    public int getSongsNumber() {
        return songsNumber;
    }

    public void setSongsNumber(int songsNumber) {
        this.songsNumber = songsNumber;
    }

    // Getter and Setter for 'favorite'
    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
}
