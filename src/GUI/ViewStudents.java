package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import Database.DatabaseConnector;
import java.sql.*;
import javafx.collections.*;

public class ViewStudents {

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, Integer> idCol;
    @FXML
    private TableColumn<Student, String> nameCol;
    @FXML
    private TableColumn<Student, String> emailCol;
    @FXML
    private TableColumn<Student, String> usernameCol;
    @FXML
    private TableColumn<Student, String> branchCol;
    @FXML
    private TableColumn<Student, Integer> yearCol;

    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(ViewStudents.class.getResource("fxml/ViewStudents.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Students List");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("student_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("student_name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        studentTable.setItems(loadStudentData());
    }

    private ObservableList<Student> loadStudentData() {
        ObservableList<Student> students = FXCollections.observableArrayList();
        String query = "SELECT student_id, student_name, email, username, branch, year FROM STUDENT";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("branch"),
                        rs.getInt("year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
