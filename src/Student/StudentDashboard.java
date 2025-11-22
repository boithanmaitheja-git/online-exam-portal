package Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import Database.DatabaseConnector;

public class StudentDashboard {

    @FXML private Label welcomeLabel;
    @FXML private VBox examListBox;

    private int studentId;

    public static void show(int studentId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    StudentDashboard.class.getResource("fxml/StudentDashboard.fxml")
            );
            Scene sc = new Scene(loader.load());

            StudentDashboard ctrl = loader.getController();
            ctrl.studentId = studentId;
            ctrl.loadStudentInfo();
            ctrl.loadExams();

            Stage st = new Stage();
            st.setTitle("Student Dashboard");
            st.setScene(sc);
            st.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadStudentInfo() {
        try (Connection conn = DatabaseConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                "SELECT student_name FROM STUDENT WHERE student_id=?"
            );
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                welcomeLabel.setText("Welcome, " + rs.getString(1));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadExams() {
        examListBox.getChildren().clear();

        try (Connection conn = DatabaseConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                "SELECT exam_id, exam_title, start_time, end_time FROM EXAM"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int examId = rs.getInt("exam_id");
                String title = rs.getString("exam_title");

                Button b = new Button(title);
                b.setStyle("-fx-background-color:#f1f1f1; -fx-padding:8px;");

                b.setOnAction(e -> AttemptExam.show(studentId, examId));

                examListBox.getChildren().add(b);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
