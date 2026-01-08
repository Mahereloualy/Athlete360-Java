package service;

import model.Athlete;

public class AthleteService {

    public void afficherInfosAthlete(Athlete athlete) {
        System.out.println("=== Infos Athlète ===");
        System.out.println(athlete);
        System.out.println("IMC : " + CalculService.calculerIMC(athlete));
    }
}
