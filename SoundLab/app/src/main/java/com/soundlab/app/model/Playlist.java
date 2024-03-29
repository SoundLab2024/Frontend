package com.soundlab.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {

    //private static final long serialVersionUID = 1L; // Numero di versione per la serializzazione
    private final long id;
    private String name;
    private String genere;
    private int image;
    private boolean favorite;
    private final List<Song> songs;
    private int numberOfSongs;

    public Playlist(int id, String name, String genere, int image, boolean favorite, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.genere = genere;
        this.image = image;
        this.favorite = favorite;
        this.songs = new ArrayList<>();
        this.numberOfSongs = 0;
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
    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    // Getter and Setter for 'image'
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    // Getter and Setter for 'numberOfSongs'
    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    // Getter and Setter for 'favorite'
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
