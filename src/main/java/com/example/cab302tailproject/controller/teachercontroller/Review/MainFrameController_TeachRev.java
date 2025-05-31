package com.example.cab302tailproject.controller.teachercontroller.Review;

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

public class MainFrameController_TeachRev {

    //<editor-fold desc="FXML UI Element References - Dynamic content">
    /**
     * A VBox container dynamically populated with content for managing and displaying
     * lesson plans or associated materials in the LessonPlanController.
     */
    @FXML
    private VBox dynamicContentBox;

    /**
     * This Label represents the UI element that displays the currently logged-in teacher's name.
     */
    @FXML private Label loggedInTeacherLabel;

    /**
     * Represents the JavaFX Label used to display the current time.
     */
    @FXML
    private Label timeLabel;

    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Navigation & Layout">
    /**
     * Button for navigating to a "Students" section.
     */
    @FXML private Button studentsButton;

    // Sidebar buttons - @FXML fields are optional if only onAction is used.
    /**
     * Button in the sidebar to navigate to the Library view.
     */
    @FXML private Button sidebarLibraryButton;
    /**
     * Button in the sidebar for generating content.
     */
    @FXML private Button sidebarGenerateButton;
    /**
     * Button in the sidebar for reviewing content.
     */
    @FXML private Button sidebarReviewButton;
    /**
     * Button in the sidebar for analysis.
     */
    @FXML private Button sidebarAnalysisButton;
    /**
     * Button in the sidebar for AI assistance.
     */
    @FXML private Button sidebarAiAssistanceButton;

    //</editor-fold>

    //<editor-fold desc="Initialisation">
    public void initialize() {
        loggedInTeacherLabel.setText(UserSession.getInstance().getFullName());
        showOverviewView();
        bindTimeToLabel(timeLabel, "hh:mm a");

    }

    private void showOverviewView() {
        try {
            // Moving to new view
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher-overview.fxml"));
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

    //<editor-fold desc="Navigation - Direct Scene Switching">
    /**
     * Handles clicks on the "Generate" button in the sidebar.
     * Reloads the lesson generation view on the current stage.
     */
    @FXML
    private void onSidebarGenerateClicked() throws IOException {
        loadScene("lesson_generator-teacher.fxml", sidebarGenerateButton, false);
    }

    /**
     * Handles clicks on the "Review" button in the sidebar.
     * Loads the teacher review view on the current stage.
     */
    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-teacher.fxml", sidebarReviewButton, false);
    }

    /**
     * Handles clicks on the "Analysis" button in the sidebar.
     * Loads the teacher analysis view on the current stage.
     */
    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-teacher.fxml", sidebarAnalysisButton, true);
    }

    /**
     * Handles clicks on the "A.I. Assistance" button in the sidebar.
     * Loads the teacher AI assistance view on the current stage.
     */
    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-teacher.fxml", sidebarAiAssistanceButton, false);
    }

    /**
     * Handles clicks on the "Library" button in the sidebar.
     * Loads the teacher library view on the current stage.
     */
    @FXML
    private void onSidebarLibraryClicked() throws IOException {
        loadScene("library-teacher.fxml", sidebarLibraryButton, true);
    }

    /**
     * Handles clicks on the "Students" button in the top navigation.
     * Loads a students view on the current stage.
     */
    @FXML
    private void onStudentsClicked() throws IOException {
        loadScene("classroom-teacher-view.fxml", studentsButton, true);
    }

    /**
     * Handles actions performed when the logout button is clicked.
     * This method logs out the currently logged-in user, switches the view
     * to the login page, and displays a confirmation message.
     */
    @FXML private void logoutButtonClicked() throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", studentsButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
    //</editor-fold>

}
