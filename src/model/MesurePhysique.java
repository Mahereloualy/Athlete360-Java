package model;

import java.time.LocalDate;

public class MesurePhysique {

    private LocalDate date;
    private double poids;

    public MesurePhysique(LocalDate date, double poids) {
        this.date = date;
        this.poids = poids;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getPoids() {
        return poids;
    }
}
