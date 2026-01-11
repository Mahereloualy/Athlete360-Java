package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "athlete360_db";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS)) {
                System.out.println("[OK] Connected to database: " + DB_NAME);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Database connection failed: " + e.getMessage());
            System.err.println("Make sure XAMPP MySQL is running!");
        }
    }
}
