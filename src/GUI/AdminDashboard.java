package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminDashboard {

    @FXML private Button viewStudentsBtn;
    @FXML private Button viewTeachersBtn;
    @FXML private Button viewResultsBtn;
    @FXML private Button logoutBtn;

    private int adminId; // Store which admin logged in

    // Show admin dashboard with adminId
    public static void show(int adminId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AdminDashboard.class.getResource("/GUI/fxml/AdminDashboard.fxml")
            );

            Scene scene = new Scene(loader.load());

            AdminDashboard controller = loader.getController();
            controller.adminId = adminId;

            Stage stage = new Stage();
            stage.setTitle("Admin Dashboard");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {

        viewStudentsBtn.setOnAction(e -> {
            ViewStudents.show();
        });

        viewTeachersBtn.setOnAction(e -> {
            ViewTeachers.show();
        });

        viewResultsBtn.setOnAction(e -> {
            ViewResults.show();
        });

        logoutBtn.setOnAction(e -> {
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.close();
            LoginScreen.main(new String[]{});
        });
    }
}
