package com.example.cab302tailproject.controller.studentcontroller.Review;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.List;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.fetchContent;
import static com.example.cab302tailproject.utils.TextFormatting.formatTextWithBold;

public class OverviewController_StudRev {
    //<editor-fold desc="Field declarations">
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

    @FXML TextField topicTextField;
    @FXML VBox generatedTextArea;
    @FXML private ChoiceBox<Integer> classCheckBox;


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

    private String materialType;

    private int weekNumber;

    private SqlStudentDAO sqlStudentDAO;
    //</editor-fold>

    //<editor-fold desc="Initialisation">
    /**
     * Initializes the controller and sets up the necessary data and event bindings for UI components.
     */
    @FXML public void initialize() {
        List<Button> weekButtons = List.of(week1Button, week2Button, week3Button, week4Button, week5Button,
                week6Button, week7Button, week8Button, week9Button, week10Button, week11Button, week12Button, week13Button);
        this.contentDAO = new ContentDAO();
        for (Button button : weekButtons) {
            if (button == null) {
                System.err.println("Button is null: Check FXML file binding for this button!");
            }
        }
        materialType = "worksheet";
        sqlStudentDAO = new SqlStudentDAO();

        for (Button weekButton : weekButtons) {
            weekButton.setOnAction(event -> handleWeekButtonSelection(weekButton, weekButtons));
        }
        setUpClassCheckBox();
        handleWeekButtonSelection(week1Button, weekButtons);        // Select week 1 by default
    }
    //</editor-fold>

    //<editor-fold desc="Button handling">
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
        this.weekNumber = Integer.parseInt(weekNumberString);
        viewContent();
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">
    /**
     * Retrieves and displays the specified content type for the selected classroom
     * and current week.
     */
    private void viewContent() {
        currentMaterial = fetchContent(weekNumber, classCheckBox, materialType, contentDAO);
        if (currentMaterial != null){
            updateContentView();
        }
    }

    /**
     * Helper for viewContent. Updates the view by refreshing the displayed content and
     * corresponding topic.
     * Clears the existing content in the `generatedTextArea` and formats the new content from
     * `currentMaterial` using the `formatTextWithBold` method. Additionally,
     * it updates the `topicTextField` with the topic associated with the
     * `currentMaterial`.
     */
    private void updateContentView(){
        generatedTextArea.getChildren().clear();
        TextFlow formattedContent = formatTextWithBold(currentMaterial.getContent());
        generatedTextArea.getChildren().add(formattedContent);
        topicTextField.setText(currentMaterial.getTopic());
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
            // Retrieve student's associated classrooms
            String userEmail = UserSession.getInstance().getEmail();
            int userID = sqlStudentDAO.getStudentIDByEmail(userEmail);
            ObservableList<Integer> availableClasses = FXCollections.observableArrayList(sqlStudentDAO.getClassroomList(userID));

            int initialClassroomID = availableClasses.getLast();
            if (initialClassroomID == -1) {
                System.err.println("No classroom found for user: " + userEmail);
                showAlert(Alert.AlertType.WARNING, "No classroom found", "No classroom found for the current user. \n Please contact your teacher to request access to a classroom.");
                return;
            }
            classCheckBox.setValue(initialClassroomID);

            classCheckBox.setItems(availableClasses);

            // Listener to call viewContent when the value changes
            classCheckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                // Check if the new value is not null
                if (newValue != null) {
                    viewContent();
                }
            });


        } catch (Exception e) {
            System.err.println("Error getting classroom id: " + e.getMessage());
        }
    }

    //</editor-fold>
}
