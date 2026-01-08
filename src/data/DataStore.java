package data;

import model.Athlete;

public class DataStore {

    private static Athlete athlete;

    public static void setAthlete(Athlete a) {
        athlete = a;
    }

    public static Athlete getAthlete() {
        return athlete;
    }
}
