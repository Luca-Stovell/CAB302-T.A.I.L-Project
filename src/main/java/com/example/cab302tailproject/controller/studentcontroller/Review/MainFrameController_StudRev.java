package com.example.cab302tailproject.controller.studentcontroller.Review;

import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;
import static com.example.cab302tailproject.utils.TextFormatting.bindTimeToLabel;


public class MainFrameController_StudRev {

    //<editor-fold desc="FXML UI Element References - Dynamic content">
    /**
     * A VBox container dynamically populated with content for managing and displaying
     * lesson plans or associated materials in the LessonPlanController.
     */
    @FXML
    private VBox dynamicContentBox;

    /**
     * This Label represents the UI element that displays the currently logged-in user's name.
     */
    @FXML Label LoggedInName;

    /**
     * Represents the JavaFX Label used to display the current time.
     */
    @FXML
    private Label timeLabel;
    //</editor-fold>

    @FXML private Button sidebarCardsButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAiAssistanceButton;
    @FXML private Button logoutButton;

    //<editor-fold desc="Initialisation">
    public void initialize() {
        LoggedInName.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");
        showOverviewView();
    }

    private void showOverviewView() {
        try {
            // Moving to new view
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-student-overview.fxml"));
            VBox layout = fxmlLoader.load();

            // Replace content in the dynamic container
            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().add(layout);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load generated content view.\n" +e.getMessage());
            System.err.println("Error: " + e.getMessage());
        }
    }
    //</editor-fold>


    //<editor-fold desc="Sidebar Buttons">
    @FXML
    public void onSidebarCardsClicked() throws IOException {
        loadScene("learning_cards.fxml", sidebarCardsButton, true);
    }

    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-student.fxml", sidebarReviewButton, false);
    }

    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-student.fxml", sidebarAnalysisButton, true);
    }

    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-student.fxml", sidebarAiAssistanceButton, true);
    }

    @FXML private void logoutButtonClicked() throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", logoutButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
    //</editor-fold>
}
