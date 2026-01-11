package service;

import model.Athlete;

public class RecommendationService {

    public static String recommend(Athlete a) {
        double imc = a.imc();

        if (imc < 18.5) return a.name + " → Gain weight program";
        if (imc < 25) return a.name + " → Maintain performance";
        return a.name + " → Fat loss & cardio program";
    }
}
