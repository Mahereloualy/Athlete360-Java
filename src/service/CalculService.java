package service;

import model.Athlete;

public class CalculService {

    public static double calculerIMC(Athlete athlete) {
        return athlete.getPoids() /
                (athlete.getTaille() * athlete.getTaille());
    }
}
