package model;

public class Athlete {

    private int id;
    private String nom;
    private int age;
    private double taille; // en mètres
    private double poids;  // en kg
    private String sport;

    public Athlete(int id, String nom, int age, double taille, double poids, String sport) {
        this.id = id;
        this.nom = nom;
        this.age = age;
        this.taille = taille;
        this.poids = poids;
        this.sport = sport;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getAge() {
        return age;
    }

    public double getTaille() {
        return taille;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids = poids;
    }

    public String getSport() {
        return sport;
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", age=" + age +
                ", taille=" + taille +
                ", poids=" + poids +
                ", sport='" + sport + '\'' +
                '}';
    }
}
