package model;

public class Aggiungi {

    private int id_playlist;
    private int id_song;

    public Aggiungi(int id_playlist, int id_traccia) {
        this.id_playlist = id_playlist;
        this.id_song = id_traccia;
    }

    public int getId_playlist() {
        return id_playlist;
    }

    public void  setId_playlist(int id_playlist) {
        this.id_playlist = id_playlist;
    }

    public int getId_song() {
        return id_song;
    }

    public void setId_song(int id_song) {
        this.id_song = id_song;
    }
}
