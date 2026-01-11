package model;

public class Athlete {
    public int id;
    public String name;
    public int age;
    public double height;
    public double weight;
    public String sport;
    public double imc;

    public Athlete(int id, String name, int age, double height, double weight, String sport) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sport = sport;
        this.imc = (height > 0) ? weight / (height * height) : 0;
    }

    public double imc() {
        return this.imc;
    }
}
