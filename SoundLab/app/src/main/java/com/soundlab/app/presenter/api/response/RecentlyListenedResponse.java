package com.soundlab.app.presenter.api.response;

import com.soundlab.app.model.Song;
import java.time.LocalDateTime;
import java.util.Date;

public class RecentlyListenedResponse {
    private int id;
    private Date data;
    private String timeSlot;
    private String user;
    private Song song;

    // Costruttore
    public RecentlyListenedResponse(int id, Date data, String timeSlot, String user, Song song) {
        this.id = id;
        this.data = data;
        this.timeSlot = timeSlot;
        this.user = user;
        this.song = song;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public String toString() {
        return "RecentlyListenedResponse{" +
                "id=" + id +
                ", data=" + data +
                ", timeSlot='" + timeSlot + '\'' +
                ", user='" + user + '\'' +
                ", song=" + song +
                '}';
    }
}
