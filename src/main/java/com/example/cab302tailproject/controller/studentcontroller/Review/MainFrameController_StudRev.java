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

    /**
     * Initializes the main frame controller with default values and settings.
     * - Updates the logged-in user display with the full name from the UserSession singleton instance.
     * - Binds the current time to the `timeLabel` using a specified time format.
     * - Loads and displays the default "overview" view in the dynamic content container.
     */
    public void initialize() {
        LoggedInName.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");
        showOverviewView();
    }

    /**
     * Loads and displays the "review-student-overview.fxml" view within the dynamic content container.
     * Clears the existing content of the `dynamicContentBox` and replaces it with the content
     * loaded from the specified FXML file. Handles potential errors during the loading process by
     * displaying an error alert.
     */
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

    /**
     * Handles the click event of the "Lesson Cards" button in the sidebar.
     * This method transitions the application view to the "learning_cards.fxml" screen
     * by calling the `loadScene` utility method. The associated stage of the
     * `sidebarCardsButton` is updated, and the application's view is entirely replaced.
     */
    @FXML
    public void onSidebarCardsClicked() throws IOException {
        loadScene("learning_cards.fxml", sidebarCardsButton, true);
    }

    /**
     * Handles the click event of the "Review" button in the sidebar.
     * This method transitions the application view to the "review-student.fxml" screen
     * by invoking the `loadScene` utility method. The associated stage of the
     * `sidebarReviewButton` is used, with the view being updated without replacing the
     * entire scene.
     */
    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-student.fxml", sidebarReviewButton, false);
    }

    /**
     * Handles the click event of the "Analysis" button in the sidebar.
     * This method transitions the application view to the "analytics-student.fxml" screen
     * by invoking the `loadScene` method. The associated stage of the `sidebarAnalysisButton`
     * is used, and the entire scene is replaced.
     */
    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-student.fxml", sidebarAnalysisButton, true);
    }

    /**
     * Handles the click event of the "AI Assistance" button in the sidebar.
     * Transitions the application view to the "ai_assistant-student.fxml" screen by invoking
     * the `loadScene` method. The associated stage of the `sidebarAiAssistanceButton`
     * is utilized, and the view is updated without replacing the entire scene.
     */
    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-student.fxml", sidebarAiAssistanceButton, false);
    }

    /**
     * Handles actions performed when the logout button is clicked.
     * This method logs out the currently logged-in user, switches the view
     * to the login page, and displays a confirmation message.
     */
    @FXML private void logoutButtonClicked() throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", logoutButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
    //</editor-fold>
}
