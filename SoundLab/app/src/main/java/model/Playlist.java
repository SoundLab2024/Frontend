package model;

import android.graphics.drawable.Drawable;


public class Playlist {

    private String name;
    private String genere;
    private int image;
    private boolean favorite;
    private int numberOfSongs;

    public Playlist(String name, String genere, int image, boolean favorite) {
        this.name = name;
        this.genere = genere;
        this.image = image;
        this.favorite = favorite;
        this.numberOfSongs = 0;
    }

    // Getter and Setter for 'name'
    public String getName() {
        return name;
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


