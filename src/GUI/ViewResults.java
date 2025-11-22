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

public class ViewResults {

    @FXML
    private TableView<Result> resultTable;
    @FXML
    private TableColumn<Result, String> studentNameCol;
    @FXML
    private TableColumn<Result, String> examTitleCol;
    @FXML
    private TableColumn<Result, Double> scoreCol;
    @FXML
    private TableColumn<Result, Double> percentageCol;
    @FXML
    private TableColumn<Result, String> gradeCol;

    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(ViewResults.class.getResource("fxml/ViewResults.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Exam Results");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("student_name"));
        examTitleCol.setCellValueFactory(new PropertyValueFactory<>("exam_title"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        percentageCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        resultTable.setItems(loadResults());
    }

    private ObservableList<Result> loadResults() {
        ObservableList<Result> results = FXCollections.observableArrayList();
        String query = """
            SELECT s.student_name, e.exam_title, r.score, r.percentage, r.grade
            FROM RESULT r
            JOIN STUDENT s ON r.student_id = s.student_id
            JOIN EXAM e ON r.exam_id = e.exam_id
            """;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                results.add(new Result(
                        rs.getString("student_name"),
                        rs.getString("exam_title"),
                        rs.getDouble("score"),
                        rs.getDouble("percentage"),
                        rs.getString("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
