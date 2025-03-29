package ExtraSources;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/enrollmentsystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection kon = null;

    public static Connection getConnection() {
        try {
            if (kon == null || kon.isClosed()) {
                kon = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database Connected Successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database Connection Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            kon = null;
        }
        return kon;
    }

    public static void keepConnectionAlive() {
        try {
            if (kon != null && !kon.isClosed()) {
                kon.createStatement().executeQuery("SELECT 1"); // Simple query to keep it alive
            }
        } catch (SQLException e) {
            System.err.println("Connection lost, attempting to reconnect...");
            kon = getConnection(); // Reconnect if needed
        }
    }

    public static void preventClosing() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Preventing database connection from closing...");
        }));
    }
}
