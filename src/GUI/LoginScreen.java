package GUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Database.DatabaseConnector;
import java.sql.*;
import Student.StudentDashboard;
import Teacher.TeacherDashboard;

public class LoginScreen extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Online Exam Portal - Login");

        Label title = new Label("Online Exam Portal");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button loginBtn = new Button("Login");
        Label status = new Label();

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();

            String[] auth = authenticateUser(user, pass);

            if (auth != null) {
                String role = auth[0];
                int id = Integer.parseInt(auth[1]);

                status.setText("Login Successful as " + role);
                status.setStyle("-fx-text-fill: green;");

                stage.close();
                openDashboard(role, id);

            } else {
                status.setText("Invalid credentials!");
                status.setStyle("-fx-text-fill: red;");
            }
    });


        VBox root = new VBox(15, title, usernameField, passwordField, loginBtn, status);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: #f4f4f4;");

        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    private String[] authenticateUser(String username, String password) {
    String[] result = new String[2]; // [role, id]

    try (Connection conn = DatabaseConnector.getConnection()) {

        // STUDENT
        PreparedStatement ps = conn.prepareStatement("SELECT student_id FROM STUDENT WHERE username=? AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            result[0] = "Student";
            result[1] = rs.getString(1);
            return result;
        }

        // TEACHER
        ps = conn.prepareStatement("SELECT teacher_id FROM TEACHER WHERE username=? AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);
        rs = ps.executeQuery();
        if (rs.next()) {
            result[0] = "Teacher";
            result[1] = rs.getString(1);
            return result;
        }

        // ADMIN
        ps = conn.prepareStatement("SELECT admin_id FROM ADMINS WHERE username=? AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);
        rs = ps.executeQuery();
        if (rs.next()) {
            result[0] = "Admin";
            result[1] = rs.getString(1);
            return result;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null; // login failed
}


    private void openDashboard(String role, int id) {
    switch (role) {
        case "Student":
            StudentDashboard.show(id);
            break;
        case "Teacher":
            TeacherDashboard.show(id);
            break;
        case "Admin":
            AdminDashboard.show(id);
            break;
    }
}


    public static void main(String[] args) {
        launch(args);
    }
}
