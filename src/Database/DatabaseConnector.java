package Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    
    // Database URL, username and password
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USERNAME = "system";
    private static final String DB_PASSWORD = "system123";

    // Method to establish a connection
    public static Connection getConnection() {

        Connection conn = null;
        try {

            // Load Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish and return the connection
            System.out.println("Database Connected Successfully!");
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

        } catch (ClassNotFoundException e) {

            System.out.println("Oracle JDBC Driver not found!");
            e.printStackTrace();     

        }catch (SQLException e) {

            System.out.println("Connection Failed! Check output console"); 
            e.printStackTrace();

        }
        return conn;
    }

    public static void main(String[] args) {
        getConnection();
    }

}
