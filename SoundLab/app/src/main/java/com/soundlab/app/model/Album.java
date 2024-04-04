package com.soundlab.app.model;

import com.example.soundlab.R;

import java.io.Serializable;

public class Album implements Serializable {

    private long id;
    private String nome;
    private int anno;
    private final Artist artist;
    private int image;

    public Album(long id, String nome, int anno, Artist artist) {
        this.id = id;
        this.nome = nome;
        this.anno = anno;
        this.artist = artist;
        this.image = R.drawable.album_cover;
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

    public int getImage() {
        return image;
    }

}
