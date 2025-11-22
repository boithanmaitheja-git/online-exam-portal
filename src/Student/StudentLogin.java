package Student;

import java.sql.*;
import java.util.Scanner;
import Database.DatabaseConnector;

public class StudentLogin {

    public static int login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String user = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        int id = -1;
        String sql = "SELECT student_id FROM STUDENT WHERE username=? AND password=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
                System.out.println("✅ Login successful! Your ID = " + id);
            } else {
                System.out.println("❌ Invalid credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}
