package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import Database.DatabaseConnector;
import Teacher.TeacherLogin;
import GUI.Teacher;
import java.sql.*;
import javafx.collections.*;

public class ViewTeachers {

    @FXML
    private TableView<Teacher> teacherTable;
    @FXML
    private TableColumn<Teacher, Integer> idCol;
    @FXML
    private TableColumn<Teacher, String> nameCol;
    @FXML
    private TableColumn<Teacher, String> emailCol;
    @FXML
    private TableColumn<Teacher, String> usernameCol;
    @FXML
    private TableColumn<Teacher, String> deptCol;

    // 💫 Load the window
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(ViewTeachers.class.getResource("fxml/ViewTeachers.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Teachers List");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 💖 When the FXML loads, this runs automatically
    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("teacher_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("teacher_name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        teacherTable.setItems(loadTeacherData());
    }

    // 🧠 Fetch data from Oracle DB
    private ObservableList<Teacher> loadTeacherData() {
        ObservableList<Teacher> teachers = FXCollections.observableArrayList();
        String query = "SELECT teacher_id, teacher_name, email, username, department FROM TEACHER";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                teachers.add(new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getString("teacher_name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("department")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }
}
