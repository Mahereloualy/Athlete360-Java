package dao;

import model.Athlete;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AthleteDAO {

    // ===============================
    // INSERT SIMPLE (UI / FORM)
    // ===============================
    public void insert(Athlete a) throws SQLException {
        String sql = "INSERT INTO athlete(name, age, height, weight, sport) VALUES(?,?,?,?,?)";
        Connection c = DBConnection.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);

        ps.setString(1, a.getName());
        ps.setInt(2, a.getAge());
        ps.setDouble(3, a.getHeight());
        ps.setDouble(4, a.getWeight());
        ps.setString(5, a.getSport());

        ps.executeUpdate();
    }

    // ===============================
    // INSERT + RETURN ID (GENERATOR)
    // ===============================
    public int insertAndReturnId(
            String name,
            int age,
            double height,
            double weight,
            String sport
    ) throws SQLException {

        String sql = "INSERT INTO athlete(name, age, height, weight, sport) VALUES(?,?,?,?,?)";
        Connection c = DBConnection.getConnection();

        PreparedStatement ps = c.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        );

        ps.setString(1, name);
        ps.setInt(2, age);
        ps.setDouble(3, height);
        ps.setDouble(4, weight);
        ps.setString(5, sport);

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    // ===============================
    // FIND ALL ATHLETES
    // ===============================
    public List<Athlete> findAll() throws SQLException {
        List<Athlete> list = new ArrayList<>();
        String sql = "SELECT * FROM athlete";

        Connection c = DBConnection.getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            list.add(new Athlete(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getDouble("height"),
                    rs.getDouble("weight"),
                    rs.getString("sport")
            ));
        }
        return list;
    }

    // ===============================
    // DASHBOARD STATS
    // ===============================
    public int countAthletes() throws SQLException {
        Connection c = DBConnection.getConnection();
        ResultSet rs = c.createStatement()
                .executeQuery("SELECT COUNT(*) FROM athlete");
        rs.next();
        return rs.getInt(1);
    }

    public double avgWeight() throws SQLException {
        Connection c = DBConnection.getConnection();
        ResultSet rs = c.createStatement()
                .executeQuery("SELECT AVG(weight) FROM athlete");
        rs.next();
        return rs.getDouble(1);
    }

    public double avgIMC() throws SQLException {
        Connection c = DBConnection.getConnection();
        ResultSet rs = c.createStatement()
                .executeQuery(
                        "SELECT AVG(weight / (height * height)) FROM athlete"
                );
        rs.next();
        return rs.getDouble(1);
    }
}
