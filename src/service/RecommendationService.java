package service;

public class RecommendationService {

    public static String generate(double imc, int calories) {

        if (imc < 18.5 && calories < 2200)
            return "Increase calories + strength training";

        if (imc > 25 && calories > 2800)
            return "Reduce calories + more cardio";

        if (imc >= 18.5 && imc <= 25)
            return "Balanced training and nutrition";

        return "Personalized adjustment recommended";
    }
}
