package Teacher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

import Database.DatabaseConnector;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class CreateExam {

    @FXML private TextField titleField;
    @FXML private Button selectPdfBtn;
    @FXML private Label pdfLabel;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField durationField;
    @FXML private Label statusLabel;

    private File selectedPDF;
    private int teacherId;

    public static void show(int teacherId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                CreateExam.class.getResource("fxml/CreateExam.fxml")
            );
            Scene sc = new Scene(loader.load());

            CreateExam ctrl = loader.getController();
            ctrl.teacherId = teacherId;

            Stage st = new Stage();
            st.setTitle("Create Exam");
            st.setScene(sc);
            st.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        selectPdfBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            selectedPDF = fc.showOpenDialog(null);
            if (selectedPDF != null)
                pdfLabel.setText(selectedPDF.getName());
        });
    }

    @FXML
    private void createExam() {
        try {
            if (titleField.getText().trim().isEmpty()) {
                statusLabel.setText("Enter title"); return;
            }
            if (selectedPDF == null) {
                statusLabel.setText("Select PDF"); return;
            }
            if (datePicker.getValue() == null) {
                statusLabel.setText("Select exam date"); return;
            }

            LocalTime time = LocalTime.parse(timeField.getText());
            int duration = Integer.parseInt(durationField.getText());

            LocalDateTime start = LocalDateTime.of(datePicker.getValue(), time);
            LocalDateTime end = start.plusMinutes(duration);

            int examId = insertExam(start, end, duration);

            if (examId > 0) {
                extractAndInsertQuestions(examId);
                statusLabel.setText("Exam created!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private int insertExam(LocalDateTime start, LocalDateTime end, int duration) throws Exception {

        String sql = """
            INSERT INTO EXAM (exam_id, teacher_id, exam_title, pdf_name, total_questions, start_time, end_time, duration_minutes, status)
            VALUES (EXAM_SEQ.NEXTVAL, ?, ?, ?, 0, ?, ?, ?, 'Scheduled')
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[] {"exam_id"})) {

            ps.setInt(1, teacherId);
            ps.setString(2, titleField.getText());
            ps.setString(3, selectedPDF.getName());
            ps.setTimestamp(4, Timestamp.valueOf(start));
            ps.setTimestamp(5, Timestamp.valueOf(end));
            ps.setInt(6, duration);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private void extractAndInsertQuestions(int examId) throws Exception {

        PDDocument doc = Loader.loadPDF(selectedPDF);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();

        String[] qs = text.split("\\d+\\.");

        for (String block : qs) {
            block = block.trim();
            if (block.isEmpty()) continue;

            String question = block.split("A\\)")[0].trim();
            String A = block.split("A\\)")[1].split("B\\)")[0].trim();
            String B = block.split("B\\)")[1].split("C\\)")[0].trim();
            String C = block.split("C\\)")[1].split("D\\)")[0].trim();
            String D = block.split("D\\)")[1].split("Answer")[0].trim();
            String ans = block.toLowerCase().contains("answer")
                    ? block.substring(block.indexOf("Answer") + 7, block.indexOf("Answer") + 8).toUpperCase()
                    : "A";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO EXAM_QUESTION VALUES (EXAM_Q_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)"
                 )) {

                ps.setInt(1, examId);
                ps.setString(2, question);
                ps.setString(3, A);
                ps.setString(4, B);
                ps.setString(5, C);
                ps.setString(6, D);
                ps.setString(7, ans);
                ps.executeUpdate();
            }
        }
    }
}
