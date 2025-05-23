package com.example.cab302tailproject.controller.teachercontroller.Review;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.DAO.SqliteTeacherDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.navigateToContent;

public class OverviewController_TeachRev {

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

    private SqliteTeacherDAO sqliteTeacherDAO;

    /**
     * A VBox container dynamically populated with content for managing and displaying
     * lesson plans or associated materials in the LessonPlanController.
     */
    @FXML
    private VBox dynamicContentBox;

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
        List<Button> weekButtons = List.of(week1Button, week2Button, week3Button, week4Button, week5Button,
                week6Button, week7Button, week8Button, week9Button, week10Button, week11Button, week12Button, week13Button);
        this.contentDAO = new ContentDAO();
        sqliteTeacherDAO = new SqliteTeacherDAO();
        for (Button button : weekButtons) {
            if (button == null) {
                System.err.println("Button is null: Check FXML file binding for this button!");
            }
        }

        for (Button weekButton : weekButtons) {
            weekButton.setOnAction(event -> handleWeekButtonSelection(weekButton, weekButtons));
        }
        setUpClassCheckBox();
        handleWeekButtonSelection(week1Button, weekButtons);        // Select week 1 by default
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
        viewContent("lesson");
    }

    /**
     * Handles the event triggered when the "View Cards" button is clicked.
     * This method is responsible for initiating the display of card-based
     * learning materials associated with the current selection. It retrieves
     * the necessary data such as material ID and week number and uses this
     * data to navigate to the appropriate card content page.
     */
    public void onViewCardsClicked() {
        viewContent("learningCard");   //
    }

    /**
     * Handles the event triggered when the "View Worksheet" button is clicked.
     * This method is responsible for initializing the worksheet content based on the
     * selected week and navigating to the corresponding content page.
     */
    public void onViewWorksheetClicked() {
        viewContent("worksheet");
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
            AllContentController_TeachRev controller = fxmlLoader.getController();
            controller.initData(previousView);  // pass the current page's layout over

            // Replace content in the dynamic container
            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().add(layout);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load generated content view.\n" + e.getMessage());
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
        String weekText = clickedButton.getText();
        String weekNumberString = weekText.replaceAll("\\D+", ""); // Remove all non-numbers
        int weekNumber = Integer.parseInt(weekNumberString);
        this.weekNumber = weekNumber;
        System.out.println("Week button clicked: " + weekNumber);
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">
    /**
     * Retrieves and displays the specified content type for the selected classroom
     * and current week.
     *
     * @param materialType The type of the material to be retrieved and displayed
     *                     (e.g., "Lesson", "Cards", or "Worksheet").
     */
    private void viewContent(String materialType) {
        if (classCheckBox.getValue() != null) {
            try {
                int classroomID = classCheckBox.getValue();

                int materialIdOfLesson = contentDAO.getMaterialByWeekAndClassroom(weekNumber, materialType, classroomID);
                currentMaterial = contentDAO.getMaterialContent(materialIdOfLesson, materialType);

                if (currentMaterial.getContent() != null) {
                    navigateToContentPage();
                }
                else {
                    showAlert(Alert.AlertType.ERROR, "No content found", "A " + materialType + " for classroom " + classCheckBox.getValue() + " in week " + weekNumber + " does not exist. \n Check 'All content' to review available content.");
                }
            }
            catch (Exception e) {
                System.err.println("Error retrieving " + materialType + " for classroom " + classCheckBox.getValue() + " in week " + weekNumber + " (materialID: " + currentMaterial.getMaterialID() + ").");
                showAlert(Alert.AlertType.ERROR, "Retrieval error", "Error retrieving " + materialType + " for classroom " + classCheckBox.getValue() + " in week " + weekNumber + ". \n Check 'All content' to review available content.");
            }
        }
        else {
            System.out.println("No classroom selected");
            showAlert(Alert.AlertType.WARNING, "No classroom selected", "No classroom selected. Please select a classroom to view the content, or navigate to 'Students' to create a classroom.");
        }
    }

    /**
     * Navigates to a new content page based on the currentMaterial.
     * Preserves the current view to facilitate return navigation and replaces
     * the content within the dynamic container with the new view.
     */
    private void navigateToContentPage(){
        previousView = new VBox();
        navigateToContent("review-teacher-lesson_view.fxml",
                dynamicContentBox,
                previousView,
                currentMaterial,
                (ContentViewController__TeachRev controller) ->
                        controller.initData(currentMaterial, dynamicContentBox, previousView
                ));
    }
    //</editor-fold>

    //<editor-fold desc="Class selection">
    /**
     * Configures and initializes the `classCheckBox` component to allow the selection of
     * classrooms for the current material. Retrieves classrooms associated with a teacher
     * and populates the `classCheckBox` with these available classroom options.
     * The initial value is set based on the classroom associated with the material.
     */
    public void setUpClassCheckBox() {
        try{
            // Retrieve teacher's associated classrooms
            UserSession userSession = UserSession.getInstance();
            String teacherEmail = userSession.getEmail();
            ObservableList<Integer> availableClasses = FXCollections.observableArrayList(sqliteTeacherDAO.getClassroomList(teacherEmail));

            int initialClassroomID = availableClasses.getLast();
            classCheckBox.setValue(initialClassroomID);

            classCheckBox.setItems(availableClasses);
        } catch (Exception e) {
            System.err.println("Error getting classroom id: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update classroom. Error: " + e.getMessage());
        }

    }
    //</editor-fold>

}
