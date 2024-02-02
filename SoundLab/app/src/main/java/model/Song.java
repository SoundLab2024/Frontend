package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Song implements Serializable {

    private static final long serialVersionUID = 1L; // Numero di versione per la serializzazione

    private final int id;
    private final String name;
    private final Date year;
    private final String genre;
    private final Type type;  // Enum Type
    private final int numberOfSingers;
    private  int image;
    private final List<Artist> artists;  // Lista di Artist associati alla canzone

    // Costruttore
    public Song(int id, String name, Date year, String genre, Type type, int numberOfSingers, int image) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.genre = genre;
        this.type = type;
        this.numberOfSingers = numberOfSingers;
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

    // Getter per year
    public Date getYear() {
        return year;
    }

    // Getter per genre
    public String getGenre() {
        return genre;
    }

    // Getter per type
    public Type getType() {
        return type;
    }

    // Getter per numberOfSingers
    public int getNumberOfSingers() {
        return numberOfSingers;
    }

    // Enum Type
    public enum Type {
        ORIGINAL, COVER, REMASTER
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

    // Aggiunge un artista alla lista degli artisti associati alla canzone
    public void addArtist(Artist artist) {
        artists.add(artist);
    }
}
