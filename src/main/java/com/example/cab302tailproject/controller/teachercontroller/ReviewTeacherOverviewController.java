package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Material;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ReviewTeacherOverviewController {

    //<editor-fold desc="Field declarations">
    @FXML Button viewLesson;
    @FXML Button viewCards;
    @FXML Button viewWorksheet;
    @FXML Button week1Button;
    @FXML Button week2Button;
    @FXML Button week3Button;
    @FXML Button week4Button;
    @FXML Button week5Button;
    @FXML Button week6Button;
    @FXML Button week7Button;
    @FXML Button week8Button;
    @FXML Button week9Button;
    @FXML Button week10Button;
    @FXML Button week11Button;
    @FXML Button week12Button;
    @FXML Button week13Button;
    private Button selectedButton = null;


    /**
     * Holds a reference to the previously displayed view within the application.
     * Used to facilitate navigation between views by tracking the last active view.
     * Typically updated when transitioning between different scenes in the interface.
     */
    private static VBox previousView;


    /**
     * Represents the material currently being edited or managed by the LessonPlanController.
     * This field holds a reference to the Material object containing relevant metadata
     */
    private Material currentMaterial;

    /**
     * The `contentDAO` variable is a private instance of the `IContentDAO` interface used to interact
     * with the content database.
     */
    private IContentDAO contentDAO;

    /**
     * A VBox container dynamically populated with content for managing and displaying
     * lesson plans or associated materials in the LessonPlanController.
     */
    @FXML
    private VBox dynamicContentBox;

    /**
     * Represents the type of material currently being processed or displayed within the
     * LessonPlanController.
     */
    private String materialType;

    /**
     * Represents the unique identifier for the material currently being processed or displayed within LessonPlanController.
     */
    private int materialID;

    private int weekNumber;
    //</editor-fold>

    @FXML public void initialize() {
        this.contentDAO = new ContentDAO();
        List<Button> weekButtons = List.of(week1Button, week2Button, week3Button, week4Button, week5Button,
                week6Button, week7Button, week8Button, week9Button, week10Button, week11Button, week12Button, week13Button);
        for (Button button : weekButtons) {
            if (button == null) {
                System.err.println("Button is null: Check FXML file binding for this button!");
            }
        }

        for (Button weekButton : weekButtons) {
            weekButton.setOnAction(event -> handleWeekButtonSelection(weekButton, weekButtons));
        }
    }

    public void onViewLessonClicked(ActionEvent event) {
        int materialIdOfWeek = contentDAO.getMaterialByWeek(weekNumber, "lesson");
        currentMaterial = new Material(materialIdOfWeek, "lesson");     // Prepare new view with the given output
        navigateToContentPage(currentMaterial.getMaterialID());
    }

    public void onViewCardsClicked(ActionEvent event) {
    ;
    }

    public void onViewWorksheetClicked(ActionEvent event) {
        int materialIdOfWeek = contentDAO.getMaterialByWeek(weekNumber, "worksheet");
        currentMaterial = new Material(materialIdOfWeek, "worksheet");     // Prepare new view with the given output
        navigateToContentPage(currentMaterial.getMaterialID());
    }

    private void navigateToContentPage(int materialID) {
        try {   // TODO: make this a util method, using fxml file name as second parameter
            this.materialID = materialID;
            if (this.currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Material Not Found", "No material found with the given ID: " + materialID + ".");
                return;
            }

            // Save current view logic to return back to
            previousView = new VBox();
            previousView.getChildren().setAll(dynamicContentBox.getChildren()); // clone the current view

            // Moving to new view
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher-lesson_view.fxml"));
            VBox layout = fxmlLoader.load();
            ReviewTeacherLessonViewController controller = fxmlLoader.getController();
            controller.initData(currentMaterial, dynamicContentBox, previousView);  // pass the data

            // Replace content in the dynamic container
            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().add(layout);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load generated content view.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleWeekButtonSelection(Button clickedButton, List<Button> weekButtons) {
        for (Button button : weekButtons) {
            button.setStyle(""); // Clear styling
        }
        clickedButton.setStyle("-fx-background-color: #0073e6; -fx-text-fill: white;");

        selectedButton = clickedButton;

        String weekText = clickedButton.getText();
        String weekNumberString = weekText.replaceAll("\\D+", ""); // Remove all non-numbers
        int weekNumber = Integer.parseInt(weekNumberString);
        this.weekNumber = weekNumber;
        System.out.println("Week button clicked: " + weekNumber);
    }

    //<editor-fold desc="Utility methods">
    /**
     * Helper method to display a standard JavaFX Alert dialog.
     * Ensures the alert is shown on the JavaFX Application Thread.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert window.
     * @param message   The main message content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    /**
     * Internal helper method to create and show an alert.
     * This method should only be called from the JavaFX Application Thread.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert.
     * @param message   The content message of the alert.
     */
    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //</editor-fold>
}
