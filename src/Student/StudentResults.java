package Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Database.DatabaseConnector;
import javafx.collections.*;

import java.sql.*;

public class StudentResults {

    @FXML private TableView<StudentResultRow> resultTable;
    @FXML private TableColumn<StudentResultRow, String> examTitleCol;
    @FXML private TableColumn<StudentResultRow, Double> scoreCol;
    @FXML private TableColumn<StudentResultRow, Double> percentageCol;
    @FXML private TableColumn<StudentResultRow, String> gradeCol;

    private int studentId;

    public static void show(int studentId) {
        try {
            FXMLLoader loader = new FXMLLoader(StudentResults.class.getResource("/Student/fxml/StudentResults.fxml"));
            Scene scene = new Scene(loader.load());
            StudentResults ctrl = loader.getController();
            ctrl.studentId = studentId;
            ctrl.loadResults();
            Stage stage = new Stage();
            stage.setTitle("My Results");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadResults() {
        examTitleCol.setCellValueFactory(new PropertyValueFactory<>("examTitle"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        percentageCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        resultTable.setItems(loadFromDB());
    }

    private ObservableList<StudentResultRow> loadFromDB() {
        ObservableList<StudentResultRow> list = FXCollections.observableArrayList();
        String sql = "SELECT e.exam_title, r.score, r.percentage, r.grade FROM RESULT r JOIN EXAM e ON r.exam_id = e.exam_id WHERE r.student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new StudentResultRow(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static class StudentResultRow {
        private final String examTitle;
        private final double score, percentage;
        private final String grade;
        public StudentResultRow(String examTitle, double score, double percentage, String grade) {
            this.examTitle=examTitle; this.score=score; this.percentage=percentage; this.grade=grade;
        }
        public String getExamTitle(){return examTitle;}
        public double getScore(){return score;}
        public double getPercentage(){return percentage;}
        public String getGrade(){return grade;}
    }
}
