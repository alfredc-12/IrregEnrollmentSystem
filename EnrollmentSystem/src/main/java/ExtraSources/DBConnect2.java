package ExtraSources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect2 {
    // Hardcoded connection details
    private static final String URL = "jdbc:mysql://044im.h.filess.io:61002/enrollmentsystem_strengthby";
    private static final String USERNAME = "enrollmentsystem_strengthby";
    private static final String PASSWORD = "84e9d7296c7975d93f738c74fefcda9d1481c712";

    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database Connected Successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            conn = null;
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("Connection is active!");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}
