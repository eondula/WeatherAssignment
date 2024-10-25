import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/weather_conditions";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "Root1234"; // Replace with your MySQL password

    public static Connection initializeDatabase() throws SQLException, ClassNotFoundException {
        // Load MySQL JDBC Driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish Connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
