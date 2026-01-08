package service;

import model.Entrainement;
import java.util.ArrayList;
import java.util.List;

public class EntrainementService {

    private List<Entrainement> entrainements = new ArrayList<>();

    public void ajouterEntrainement(Entrainement e) {
        entrainements.add(e);
    }

    public void afficherEntrainements() {
        System.out.println("=== Entraînements ===");
        for (Entrainement e : entrainements) {
            System.out.println(e);
        }
    }

    public int tempsTotal() {
        int total = 0;
        for (Entrainement e : entrainements) {
            total += e.getDuree();
        }
        return total;
    }
}
