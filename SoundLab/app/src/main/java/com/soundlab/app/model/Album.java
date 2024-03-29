package com.soundlab.app.model;

public class Album {

    private long id;
    private String nome;
    private int anno;
    private Artist artist;

    public Album(long id, String nome, int anno, Artist artist) {
        this.id = id;
        this.nome = nome;
        this.anno = anno;
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public Artist getArtist() {
        return artist;
    }

}
