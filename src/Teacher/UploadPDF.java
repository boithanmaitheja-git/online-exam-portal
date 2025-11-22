package Teacher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import Database.DatabaseConnector;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class UploadPDF {

    @FXML private TextField examTitleField;
    @FXML private Button selectPdfBtn;
    @FXML private Button uploadBtn;
    @FXML private Label pdfNameLabel;
    @FXML private Label statusLabel;

    private File selectedPDF;
    private int teacherId;

    // ------------------------- SHOW SCREEN ----------------------------- //

    public static void show(int teacherId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    UploadPDF.class.getResource("fxml/UploadPDF.fxml")
            );

            Parent root = loader.load();
            UploadPDF controller = loader.getController();
            controller.teacherId = teacherId;

            Stage stage = new Stage();
            stage.setTitle("Upload PDF - Create Exam");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------- INITIALIZE UI --------------------------- //

    @FXML
    private void initialize() {

        // SELECT PDF
        selectPdfBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            selectedPDF = chooser.showOpenDialog(null);

            if (selectedPDF != null) {
                pdfNameLabel.setText("Selected: " + selectedPDF.getName());
                statusLabel.setText("");
            }
        });

        // UPLOAD PDF
        uploadBtn.setOnAction(e -> {
            if (selectedPDF == null) {
                statusLabel.setText("⚠ Please select a PDF");
                return;
            }
            if (examTitleField.getText().trim().isEmpty()) {
                statusLabel.setText("⚠ Please enter exam title");
                return;
            }

            processPDF();
        });
    }

    // ---------------------------- PROCESS PDF ------------------------------- //

    private void processPDF() {
        try {
            PDDocument document = Loader.loadPDF(selectedPDF);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            List<QuestionModel> questions = parseQuestions(text);

            if (questions.isEmpty()) {
                statusLabel.setText("❌ No questions detected. Check PDF format.");
                return;
            }

            int examId = insertExam(examTitleField.getText(), selectedPDF.getName(), questions.size());
            insertQuestions(examId, questions);

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("✔ Successfully uploaded " + questions.size() + " questions!");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Error: " + e.getMessage());
        }
    }

    // ---------------------------- ADVANCED QUESTION PARSER ------------------------------- //

    private List<QuestionModel> parseQuestions(String text) {

        List<QuestionModel> list = new ArrayList<>();

        // CLEAN TEXT — remove PDF weirdness
        text = text.replace("\r", "")
                   .replace("\u00A0", " ")   // non-breaking space
                   .replaceAll("\\s+", " ")  // collapse multiple spaces
                   .trim();

        // Split based on "1." "2." style
        String[] raw = text.split("\\d+\\.");

        for (String block : raw) {

            block = block.trim();
            if (block.isEmpty()) continue;

            QuestionModel q = new QuestionModel();

            // Find A)
            int aIndex = block.indexOf("A)");
            if (aIndex == -1) continue;

            // Question text
            q.question = block.substring(0, aIndex).trim();

            // Extract options
            q.optionA = extractOption(block, "A\\)");
            q.optionB = extractOption(block, "B\\)");
            q.optionC = extractOption(block, "C\\)");
            q.optionD = extractOption(block, "D\\)");

            // Extract Answer
            q.correct = extractAnswer(block);

            list.add(q);
        }

        return list;
    }

    private String extractOption(String block, String letter) {

        String regex = letter;

        String[] parts = block.split(regex);
        if (parts.length < 2) return "";

        String after = parts[1].trim();

        // Stop at next option letter
        after = after.split("A\\)|B\\)|C\\)|D\\)|Answer")[0].trim();

        return after;
    }

    private String extractAnswer(String block) {
        block = block.toLowerCase();

        if (!block.contains("answer")) return "";

        String[] parts = block.split("answer");
        if (parts.length < 2) return "";

        String ans = parts[1].replace(":", "").trim();

        if (ans.length() == 0) return "";

        return ans.substring(0,1).toUpperCase(); // A/B/C/D
    }

    // ---------------------------- INSERT EXAM -------------------------------- //

    private int insertExam(String title, String pdfName, int totalQuestions) throws SQLException {

        String sql = """
            INSERT INTO EXAM (exam_id, teacher_id, exam_title, pdf_name, total_questions, status)
            VALUES (EXAM_SEQ.NEXTVAL, ?, ?, ?, ?, 'Scheduled')
            """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"exam_id"})) {

            ps.setInt(1, teacherId);
            ps.setString(2, title);
            ps.setString(3, pdfName);
            ps.setInt(4, totalQuestions);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

            return -1;
        }
    }

    // ---------------------------- INSERT QUESTIONS -------------------------------- //

    private void insertQuestions(int examId, List<QuestionModel> questions) throws SQLException {

        String sql = """
            INSERT INTO EXAM_QUESTION 
            (eq_id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option)
            VALUES (EXAM_Q_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (QuestionModel q : questions) {

                ps.setInt(1, examId);
                ps.setString(2, q.question);
                ps.setString(3, q.optionA);
                ps.setString(4, q.optionB);
                ps.setString(5, q.optionC);
                ps.setString(6, q.optionD);
                ps.setString(7, q.correct);

                ps.executeUpdate();
            }
        }
    }

    // ---------------------------- MODEL CLASS -------------------------------- //

    class QuestionModel {
        String question;
        String optionA;
        String optionB;
        String optionC;
        String optionD;
        String correct;
    }
}
