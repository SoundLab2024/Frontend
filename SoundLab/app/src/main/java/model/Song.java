package model;

import java.util.Date;

public class Song {

    private final int id;
    private final String name;
    private final Date year;
    private final String genre;
    private final Type type;  // Enum Type
    private final int numberOfSingers;

    // Costruttore
    public Song(int id, String name, Date year, String genre, Type type, int numberOfSingers) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.genre = genre;
        this.type = type;
        this.numberOfSingers = numberOfSingers;
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
}
