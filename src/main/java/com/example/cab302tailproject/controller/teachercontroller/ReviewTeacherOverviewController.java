package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    @FXML Button allContentButton;
    private Button selectedButton = null;

    @FXML private ChoiceBox<Integer> classCheckBox;


    /**
     * Holds a reference to the previously displayed view within the application.
     * Used to facilitate navigation between views by tracking the last active view.
     * This is typically updated when transitioning between different scenes in the interface.
     */
    private VBox previousView;


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

    //<editor-fold desc="Initialisation">
    /**
     * Initializes the controller and sets up the necessary data and event bindings for UI components.
     */
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
        setUpClassCheckBox();
    }
    //</editor-fold>

    //<editor-fold desc="Button handling">

    /**
     * Handles the event triggered when the "View Lesson" button is clicked.
     * This method retrieves the material ID corresponding to the selected week
     * and initializes a new lesson material. The lesson is then displayed by
     * navigating to the appropriate content page.
     */
    public void onViewLessonClicked() {
        int materialIdOfWeek = contentDAO.getMaterialByWeek(weekNumber, "lesson");
        currentMaterial = new Material(materialIdOfWeek, "lesson");     // Prepare new view with the given output
        navigateToContentPage(currentMaterial.getMaterialID());
    }

    /**
     * Handles the event triggered when the "View Cards" button is clicked.
     * This method is responsible for initiating the display of card-based
     * learning materials associated with the current selection. It retrieves
     * the necessary data such as material ID and week number and uses this
     * data to navigate to the appropriate card content page.
     */
    public void onViewCardsClicked() {
    ; // TODO: implement a way for teachers to view cards
    }

    /**
     * Handles the event triggered when the "View Worksheet" button is clicked.
     * This method is responsible for initializing the worksheet content based on the
     * selected week and navigating to the corresponding content page.
     */
    public void onViewWorksheetClicked() {
        int materialIdOfWeek = contentDAO.getMaterialByWeek(weekNumber, "worksheet");
        currentMaterial = new Material(materialIdOfWeek, "worksheet");     // Prepare new view with the given output
        navigateToContentPage(currentMaterial.getMaterialID());
    }

    /**
     * Handles the event when the "All Content" button is clicked.
     * This method facilitates navigation to the "All Content" view
     * while preserving the current view for potential return navigation.
     */
    @FXML
    private void onAllContentClicked() {
        try {
            // Save current view logic to return back to
            previousView = new VBox();
            previousView.getChildren().setAll(dynamicContentBox.getChildren()); // clone the current view

            // Moving to new view
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher-all_content.fxml"));
            VBox layout = fxmlLoader.load();
            ReviewTeacherAllContentController controller = fxmlLoader.getController();
            controller.initData(previousView);  // pass the current page's layout over

            // Replace content in the dynamic container
            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().add(layout);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load generated content view.\n" + e.getMessage());
            //e.printStackTrace();
        }
    }

    /**
     * Handles the selection of a week button from a list of week buttons.
     * This method updates the button styles by highlighting the selected button
     * and resetting the styles of the other buttons. It also retrieves and processes
     * the selected week's number for further use in the application.
     *
     * @param clickedButton The button that was clicked and selected by the user.
     * @param weekButtons   A list of all week buttons that can be selected, including
     *                      the clicked button.
     */
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
    //</editor-fold>

    //<editor-fold desc="Navigation">
    /**
     * Navigates to a new content page based on the provided material ID.
     * Preserves the current view to facilitate return navigation and replaces
     * the content within the dynamic container with the new view.
     *
     * @param materialID The ID of the material to be loaded for the new content page.
     */
    private void navigateToContentPage(int materialID) {
        try {
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
            //e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Class selection">

    /**
     * Configures and initializes the `classCheckBox` component to allow the selection of
     * classrooms for the current material. Retrieves classrooms associated with a teacher
     * and populates the `classCheckBox` with these available classroom options.
     * The initial value is set based on the classroom associated with the material.
     * Also, adds a property change listener to handle selection events.
     */
    public void setUpClassCheckBox() {
        try{
            // Retrieve teacher's associated classrooms
            int initialClassroomID = contentDAO.getClassroomID(materialID);
            classCheckBox.setValue(initialClassroomID);
            UserSession userSession = UserSession.getInstance();
            String teacherEmail = userSession.getEmail();
            ObservableList<Integer> availableClasses = FXCollections.observableArrayList(contentDAO.getClassroomList(teacherEmail));
            classCheckBox.setItems(availableClasses);

            // Add a listener to handle the selection of a week
            classCheckBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue != null) {
                    handleClassSelection(newValue);
                }
            });
        } catch (Exception e) {
            System.err.println("Error getting classroom id: " + e.getMessage());
            //e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update classroom. Error: " + e.getMessage());
        }

    }

    /**
     * Handles the selection of a specific classroom for a material and updates the database
     * with the provided classroomID. Displays confirmation or error messages based on the
     * success or failure of the update operation.
     *
     * @param classroomID the identifier of the classroom to be associated with the material
     *                    and updated in the database
     */
    private void handleClassSelection(int classroomID) {
        try {
            boolean updated = contentDAO.updateClassroomID(classroomID, materialID);

            if (updated) {
                System.out.println("ClassroomID updated: " + classroomID + " for " + materialID);
            }
            else {
                System.out.println("ClassroomID update failed: " + classroomID);
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update ClassroomID.");
            }
        } catch (Exception e) {
            System.err.println("Error updating ClassroomID: " + e.getMessage());
            //e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update ClassroomID. Error: " + e.getMessage());
        }
    }
    //</editor-fold>

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
