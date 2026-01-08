package model;

public class Athlete {
    private int id;
    private String name;
    private int age;
    private double height;
    private double weight;
    private String sport;

    public Athlete(int id, String name, int age, double height, double weight, String sport) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sport = sport;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public String getSport() { return sport; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setHeight(double height) { this.height = height; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setSport(String sport) { this.sport = sport; }
}
