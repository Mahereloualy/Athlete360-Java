package dao;

import model.Athlete;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class AthleteDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/athlete360_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public List<Athlete> findAll() {
        List<Athlete> list = new ArrayList<>();
        String sql = "SELECT * FROM athlete";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Athlete(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getString("sport")));
            }
        } catch (SQLException e) {
            System.err.println("findAll error: " + e.getMessage());
        }
        return list;
    }

    public void insert(Athlete a) {
        String sql = "INSERT INTO athlete (name, age, height, weight, sport) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.name);
            pstmt.setInt(2, a.age);
            pstmt.setDouble(3, a.height);
            pstmt.setDouble(4, a.weight);
            pstmt.setString(5, a.sport);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("insert error: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM athlete WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("delete error: " + e.getMessage());
        }
    }

    public int count() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM athlete")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("count error: " + e.getMessage());
        }
        return 0;
    }

    public double avgWeight() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT AVG(weight) FROM athlete")) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("avgWeight error: " + e.getMessage());
        }
        return 0.0;
    }

    public double avgImc() {
        String sql = "SELECT AVG(weight / (height * height)) FROM athlete";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("avgImc error: " + e.getMessage());
        }
        return 0.0;
    }

    public Map<String, Integer> getSportsDistribution() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT sport, COUNT(*) FROM athlete GROUP BY sport";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("getSportsDistribution error: " + e.getMessage());
        }
        return map;
    }

    public List<Map<String, Object>> weightSeries() {
        List<Map<String, Object>> list = new ArrayList<>();
        LocalDate d = LocalDate.now().minusDays(5);
        double avg = avgWeight();
        if (avg == 0)
            avg = 70.0;
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("date", d.plusDays(i).toString());
            m.put("weight", avg + (r.nextDouble() * 4 - 2));
            list.add(m);
        }
        return list;
    }

    public void seed(int count) {
        Random r = new Random();
        String[] sports = { "Football", "Tennis", "Basket", "Running", "Fitness" };
        for (int i = 0; i < count; i++) {
            insert(new Athlete(0, "Athlete " + r.nextInt(1000), 18 + r.nextInt(20),
                    1.60 + r.nextDouble() * 0.40, 60 + r.nextDouble() * 40,
                    sports[r.nextInt(sports.length)]));
        }
    }
}
