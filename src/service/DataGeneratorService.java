package service;

import dao.AthleteDAO;
import dao.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Random;

public class DataGeneratorService {

    private final Random random = new Random();
    private final AthleteDAO athleteDAO = new AthleteDAO();

    public void generate(int count) throws Exception {
        for (int i = 0; i < count; i++) {

            int athleteId = athleteDAO.insertAndReturnId(
                    randomName(),
                    18 + random.nextInt(20),
                    1.60 + random.nextDouble() * 0.30,
                    60 + random.nextDouble() * 30,
                    "Fitness"
            );

            generateWeightHistory(athleteId);
        }
    }

    private void generateWeightHistory(int athleteId) throws Exception {
        String sql = "INSERT INTO weight_history (athlete_id, weight, date) VALUES (?, ?, ?)";
        Connection c = DBConnection.getConnection();

        double baseWeight = 60 + random.nextDouble() * 30;

        for (int i = 0; i < 30; i++) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, athleteId);
            ps.setDouble(2, baseWeight + random.nextGaussian());
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now().minusDays(i)));
            ps.executeUpdate();
        }
    }

    private String randomName() {
        String[] names = {"Ziyad", "Adam", "Yassine", "Omar", "Anas", "Ilyas"};
        return names[random.nextInt(names.length)] + random.nextInt(100);
    }
}
