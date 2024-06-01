package com.soundlab.app.model;

import com.example.soundlab.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song implements Serializable {

    //private static final long serialVersionUID = 1L; // Numero di versione per la serializzazione
    private final Long id;
    private final String title;
    private final String genre;
    private  int image;
    private final List<Artist> artists;  // Lista di Artist associati alla canzone
    private int track;

    // Costruttore
    public Song(long id, String title, String genre, Integer image, int track) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.image = image;
        this.artists = new ArrayList<>();
        this.track = track;
    }

    public Song(long id, String title, String genre, int track) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.image = R.drawable.cover_default;
        this.artists = new ArrayList<>();
        this.track = track;
    }

    // Getter per id
    public long getId() {
        return id;
    }

    // Getter per name
    public String getTitle() {
        return title;
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

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }


}