package Teacher;

import java.sql.*;
import java.util.Scanner;
import Database.DatabaseConnector;

public class TeacherLogin {

    public static int login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        int teacherId = -1;
        String query = "SELECT teacher_id FROM TEACHER WHERE username=? AND password=?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                teacherId = rs.getInt("teacher_id");
                System.out.println("✅ Login successful! Welcome, " + username + "!");
            } else {
                System.out.println("❌ Invalid username or password!");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Database error: " + e.getMessage());
        }

        return teacherId;
    }

    public static void main(String[] args) {
        int id = login();
        if (id != -1) {
            System.out.println("You are logged in as Teacher ID: " + id);
            // You can call TeacherDashboard or CreateExam next
        }
    }
}
