package ui;

import model.Athlete;
import model.Entrainement;
import service.AthleteService;
import service.EntrainementService;
import data.DataStore;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AthleteService athleteService = new AthleteService();
        EntrainementService entrainementService = new EntrainementService();

        System.out.println("=== Création de l'athlète ===");

        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Âge : ");
        int age = scanner.nextInt();

        System.out.print("Taille (en mètres) : ");
        double taille = scanner.nextDouble();

        System.out.print("Poids (kg) : ");
        double poids = scanner.nextDouble();
        scanner.nextLine(); // vider le buffer

        System.out.print("Sport : ");
        String sport = scanner.nextLine();

        Athlete athlete = new Athlete(1, nom, age, taille, poids, sport);
        DataStore.setAthlete(athlete);

        int choix;

        do {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Ajouter un entraînement");
            System.out.println("2. Afficher infos athlète");
            System.out.println("3. Afficher entraînements");
            System.out.println("4. Temps total d'entraînement");
            System.out.println("0. Quitter");

            System.out.print("Choix : ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("Type : ");
                    String type = scanner.nextLine();

                    System.out.print("Durée (minutes) : ");
                    int duree = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Intensité : ");
                    String intensite = scanner.nextLine();

                    entrainementService.ajouterEntrainement(
                            new Entrainement(LocalDate.now(), type, duree, intensite)
                    );
                    break;

                case 2:
                    athleteService.afficherInfosAthlete(DataStore.getAthlete());
                    break;

                case 3:
                    entrainementService.afficherEntrainements();
                    break;

                case 4:
                    System.out.println("Temps total : "
                            + entrainementService.tempsTotal() + " minutes");
                    break;

                case 0:
                    System.out.println("Au revoir !");
                    break;

                default:
                    System.out.println("Choix invalide");
            }

        } while (choix != 0);

        scanner.close();
    }
}
