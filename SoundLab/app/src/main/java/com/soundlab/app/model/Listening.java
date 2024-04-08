package com.soundlab.app.model;

import java.util.List;

public class Listening {
    private String utente;
    private String canzone;
    private String songType;
    private List<Artist> singers;
    private int totalListens;
    private String timeSlot;

    public Listening(String utente,String canzone, String songType, List<Artist> singers, int totalListens, String timeSlot) {
        this.utente = utente;
        this.canzone = canzone;
        this.songType = songType;
        this.singers = singers;
        this.totalListens = totalListens;
        this.timeSlot = timeSlot;
    }

    public String getUtente() {return utente;}
    public String getCanzone() {return canzone;}

    public String getSongType() {return songType;}

    public List<Artist> getSingers() {return singers;}

    public int getTotalListens() {return totalListens;}

    public String getTimeSlot() {return timeSlot;}
}
