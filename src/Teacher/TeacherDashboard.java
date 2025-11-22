package Teacher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import Database.DatabaseConnector;

public class TeacherDashboard {

    @FXML private Label welcomeLabel;
    @FXML private VBox examListBox;

    private int teacherId;

    public static void show(int teacherId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    TeacherDashboard.class.getResource("fxml/TeacherDashboard.fxml")
            );
            Scene sc = new Scene(loader.load());

            TeacherDashboard ctrl = loader.getController();
            ctrl.teacherId = teacherId;
            ctrl.loadTeacherInfo();
            ctrl.loadExams();

            Stage st = new Stage();
            st.setTitle("Teacher Dashboard");
            st.setScene(sc);
            st.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadTeacherInfo() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT teacher_name FROM TEACHER WHERE teacher_id=?"
            );
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                welcomeLabel.setText("Welcome, " + rs.getString(1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadExams() {
        examListBox.getChildren().clear();

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT exam_id, exam_title, status FROM EXAM WHERE teacher_id=? ORDER BY exam_id DESC"
            );
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("exam_id");
                String title = rs.getString("exam_title");
                String status = rs.getString("status");

                Button b = new Button(id + " - " + title + " (" + status + ")");
                b.setStyle("-fx-padding: 6px; -fx-font-size: 14px; -fx-background-color:#ecf0f1;");

                examListBox.getChildren().add(b);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openCreateExam() {
        CreateExam.show(teacherId);
    }

    @FXML
    private void openUploadPDF() {
        UploadPDF.show(teacherId);
    }
}
