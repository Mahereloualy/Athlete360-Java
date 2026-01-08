package model;

import java.time.LocalDate;

public class Entrainement {

    private LocalDate date;
    private String type;
    private int duree; // minutes
    private String intensite;

    public Entrainement(LocalDate date, String type, int duree, String intensite) {
        this.date = date;
        this.type = type;
        this.duree = duree;
        this.intensite = intensite;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getDuree() {
        return duree;
    }

    public String getIntensite() {
        return intensite;
    }

    @Override
    public String toString() {
        return "Entrainement{" +
                "date=" + date +
                ", type='" + type + '\'' +
                ", duree=" + duree +
                ", intensite='" + intensite + '\'' +
                '}';
    }
}
