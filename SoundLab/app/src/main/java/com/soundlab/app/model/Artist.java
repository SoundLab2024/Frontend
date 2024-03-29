package com.soundlab.app.model;

import java.io.Serializable;
import java.util.Date;

public class Artist implements Serializable {

    //private static final long serialVersionUID = 1L; // Numero di versione per la serializzazione
    private long id;
    private String name;
    private Date dataDiNascita;
    private String nazionalita;

    public Artist(long id, String name, Date dataDiNascita, String nazionalita) {
        this.id = id;
        this.name = name;
        this.dataDiNascita = dataDiNascita;
        this.nazionalita = nazionalita;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDataDiNascita() {
        return dataDiNascita;
    }

    public void setDataDiNascita(Date dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public String getNazionalita() {
        return nazionalita;
    }

    public void setNazionalita(String nazionalita) {
        this.nazionalita = nazionalita;
    }
}
