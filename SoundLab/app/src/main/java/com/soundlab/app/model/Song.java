package com.soundlab.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song implements Serializable {

    private static final long serialVersionUID = 1L; // Numero di versione per la serializzazione

    private final int id;
    private final String name;
    private final String genre;
    private  int image;
    private final List<Artist> artists;  // Lista di Artist associati alla canzone

    // Costruttore
    public Song(int id, String name, String genre, Integer image) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.image = image;
        this.artists = new ArrayList<>();
    }

    // Getter per id
    public int getId() {
        return id;
    }

    // Getter per name
    public String getName() {
        return name;
    }

    // Getter per genre
    public String getGenre() {
        return genre;
    }

    // Getter and Setter for 'image'
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public Artist getMainArtist() {
        return artists.get(0);
    }

    // Aggiunge un artista alla lista degli artisti associati alla canzone
    public void addArtist(Artist artist) {
        artists.add(artist);
    }

}