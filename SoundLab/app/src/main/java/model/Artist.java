package model;

import java.util.Date;

public class Artist {
    private final int id;
    private final String name;
    private final Date dataDiNascita;
    private final String nazionalita;

    public Artist(int id, String name, Date dataDiNascita, String nazionalita) {
        this.id = id;
        this.name = name;
        this.dataDiNascita = dataDiNascita;
        this.nazionalita = nazionalita;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDataDiNascita() {
        return dataDiNascita;
    }

    public String getNazionalita() {
        return nazionalita;
    }
}

