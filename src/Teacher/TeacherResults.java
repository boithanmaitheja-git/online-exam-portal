package Teacher;

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

public class TeacherResults {

    @FXML private TableView<ResultRow> resultTable;
    @FXML private TableColumn<ResultRow, String> studentNameCol;
    @FXML private TableColumn<ResultRow, String> examTitleCol;
    @FXML private TableColumn<ResultRow, Double> percentageCol;
    @FXML private TableColumn<ResultRow, String> gradeCol;

    private int teacherId = -1;

    public static void show(int teacherId) {
        try {
            FXMLLoader loader = new FXMLLoader(TeacherResults.class.getResource("/Teacher/fxml/TeacherResults.fxml"));
            Scene scene = new Scene(loader.load());
            TeacherResults ctrl = loader.getController();
            ctrl.teacherId = teacherId;
            ctrl.loadData();
            Stage stage = new Stage();
            stage.setTitle("Teacher - Results");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        examTitleCol.setCellValueFactory(new PropertyValueFactory<>("examTitle"));
        percentageCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        resultTable.setItems(loadFromDB());
    }

    private ObservableList<ResultRow> loadFromDB() {
        ObservableList<ResultRow> rows = FXCollections.observableArrayList();
        String sql = """
            SELECT s.student_name, e.exam_title, r.percentage, r.grade
            FROM RESULT r
            JOIN STUDENT s ON r.student_id = s.student_id
            JOIN EXAM e ON r.exam_id = e.exam_id
            WHERE e.teacher_id = ?
            ORDER BY e.exam_id DESC
            """;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new ResultRow(rs.getString(1), rs.getString(2),
                        rs.getDouble(3), rs.getString(4)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    public static class ResultRow {
        private final String studentName, examTitle, grade;
        private final double percentage;
        public ResultRow(String studentName, String examTitle, double percentage, String grade) {
            this.studentName = studentName; this.examTitle = examTitle; this.percentage = percentage; this.grade = grade;
        }
        public String getStudentName() { return studentName; }
        public String getExamTitle() { return examTitle; }
        public double getPercentage() { return percentage; }
        public String getGrade() { return grade; }
    }
}
