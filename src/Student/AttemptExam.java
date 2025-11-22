package Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.animation.*;
import javafx.util.Duration;
import java.sql.*;
import java.util.*;

import Database.DatabaseConnector;

public class AttemptExam {

    @FXML private Label examTitleLabel;
    @FXML private Label questionLabel;
    @FXML private Label timerLabel;

    @FXML private RadioButton optA, optB, optC, optD;
    @FXML private ToggleGroup optionsGroup;

    private int studentId;
    private int examId;

    private List<Question> questions = new ArrayList<>();
    private int current = 0;
    private Timeline timer;
    private Timestamp endTime;

    public static void show(int studentId, int examId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AttemptExam.class.getResource("fxml/AttemptExam.fxml")
            );
            Scene sc = new Scene(loader.load());

            AttemptExam ctrl = loader.getController();
            ctrl.studentId = studentId;
            ctrl.examId = examId;

            ctrl.loadExamMeta();
            ctrl.loadQuestions();

            if (ctrl.questions.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "No questions found!");
                a.show();
                return;
            }

            ctrl.startTimer();
            ctrl.showQuestion(0);

            Stage st = new Stage();
            st.setTitle("Attempt Exam");
            st.setScene(sc);
            st.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadExamMeta() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT exam_title, end_time FROM EXAM WHERE exam_id=?"
            );
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                examTitleLabel.setText(rs.getString(1));
                endTime = rs.getTimestamp(2);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadQuestions() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM EXAM_QUESTION WHERE exam_id=? ORDER BY eq_id"
            );
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                questions.add(new Question(
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_option")
                ));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startTimer() {
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> updateTimer())
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimer() {
        long now = System.currentTimeMillis();
        long diff = endTime.getTime() - now;

        if (diff <= 0) {
            timer.stop();
            submitExam();
            return;
        }

        long s = diff / 1000;
        timerLabel.setText(String.format("%02d:%02d:%02d", s/3600,(s%3600)/60,s%60));
    }

    private void showQuestion(int idx) {
        current = idx;

        Question q = questions.get(idx);
        questionLabel.setText((idx + 1) + ". " + q.text);

        optA.setText(q.A);
        optB.setText(q.B);
        optC.setText(q.C);
        optD.setText(q.D);

        optionsGroup.selectToggle(null);
    }

    @FXML
    private void nextQ() {
        if (current < questions.size() - 1)
            showQuestion(current + 1);
    }

    @FXML
    private void prevQ() {
        if (current > 0)
            showQuestion(current - 1);
    }

    @FXML
    private void submitExam() {
        if (timer != null) timer.stop();

        int correct = 0;

        for (Question q : questions) {
            Toggle t = optionsGroup.getSelectedToggle();
            if (t == null) continue;
            RadioButton rb = (RadioButton)t;
            String ans = rb == optA ? "A" :
                         rb == optB ? "B" :
                         rb == optC ? "C" : "D";

            if (ans.equals(q.correct))
                correct++;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO RESULT VALUES (RESULT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)"
            );

            double percent = (correct * 100.0) / questions.size();
            String grade = percent>=80?"A":percent>=60?"B":"C";

            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, questions.size());
            ps.setInt(4, correct);
            ps.setDouble(5, correct);
            ps.setDouble(6, percent);
            ps.setString(7, grade);

            ps.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION, "Exam Submitted!");
        a.show();

        ((Stage) examTitleLabel.getScene().getWindow()).close();
    }

    class Question {
        String text, A, B, C, D, correct;

        Question(String t,String a,String b,String c,String d,String corr){
            text=t; A=a; B=b; C=c; D=d; correct=corr;
        }
    }
}
