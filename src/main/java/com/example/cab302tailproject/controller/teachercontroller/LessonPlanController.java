package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller class responsible for managing the lesson plan and worksheet functionalities
 * after generating. It provides logic to handle UI interactions, manage material data,
 * and facilitate the saving or navigation between views.
 */
public class LessonPlanController {

    //<editor-fold desc="Field declarations">
    /**
     * Represents a ChoiceBox component that allows the user to select a specific week.
     */
    @FXML private ChoiceBox<Integer> weekCheckBox;
    @FXML private ChoiceBox<Integer> classCheckBox;

    /**
     * A JavaFX TextField component used for displaying and editing the topic or title
     * associated with the material being managed in the `LessonPlanController`.
     */
    @FXML
    private TextField topicTextField;

    /**
     * A JavaFX TextArea UI component that is used to display generated text content
     * within the lesson generation view of the application. This component is
     * updated dynamically when new text is generated based on user inputs or actions
     * such as generating lessons or worksheets.
     */
    @FXML
    private TextArea generatedTextArea;

    /**
     * FileChooser for saving generated content.
     */
    private FileChooser fileChooser;

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

    //</editor-fold>

    //<editor-fold desc="Initialisation">

    /**
     * Initializes the controller after the root element has been completely loaded.
     */
    @FXML
    public void initialize() {}

    /**
     * Initializes the material data and sets up the user interface components based on the provided material type.
     * Handles different material types such as "lesson" and "worksheet", retrieves their content from the database,
     * and populates the text area and topic fields accordingly. Validates material type and displays appropriate
     * alerts for invalid cases or missing content.
     *
     * @param material the Material object containing the material ID and type to initialize
     * @param dynamicContentBox the VBox that dynamically displays the content for the initialized material
     * @param previousView the VBox representing the prior view to which the user can navigate back
     */
    public void initData(Material material, VBox dynamicContentBox, VBox previousView) {
        this.currentMaterial = material;
        this.contentDAO = new ContentDAO();
        materialID = currentMaterial.getMaterialID();
        materialType = currentMaterial.getMaterialType();
        currentMaterial = contentDAO.getMaterialContent(materialID, materialType);

        this.dynamicContentBox = dynamicContentBox;
        this.previousView = previousView;
        setUpWeekCheckBox();
        setUpClassCheckBox();
        setupLabelToggleBehavior();

        if (currentMaterial != null) {
            generatedTextArea.setText(currentMaterial.getContent());
            topicTextField.setText(currentMaterial.getTopic());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No generated content found",
                    "No content found for the given ID: " + materialID + ".");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Button functionality">
    /**
     * Handles the event triggered when the "Back" button is clicked.
     * This restores the previous view if one is available by clearing the current dynamic content and replacing it with the
     * children of the previous view. Also deletes the content associated with the current material ID from the database.
     * If no previous view exists, it displays a warning alert to the user.
     */
    @FXML
    private void onBackClicked() {
        if (previousView != null) {
            System.out.println("Restoring previous view with children: " + previousView.getChildren().size());
            contentDAO.deleteContent(materialID);

            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().addAll(previousView.getChildren());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No Previous View",
                    "No previous state to navigate back to.");
        }
    }

    /**
     * Saves the provided content to a file, allowing the user to choose the file's location and name.
     * The file name is automatically suggested based on the supplied type and topic. If the content is
     * null or the current stage cannot be retrieved, an error alert is displayed. If the file is successfully
     * saved, an informational alert confirms the save operation. If an error occurs during the save process,
     * an error alert is displayed with the relevant message.
     *
     * @param content the text content to be saved to the file; cannot be null.
     * @param type the category or type of the content used to suggest a file name.
     * @param topic the topic of the content used to suggest a safe and descriptive file name.
     */
    private void saveContentToFileFromContentView(String content, String type, String topic){
        if (content == null) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save null content.");
            return;
        }
        if (fileChooser == null) {
            fileChooser = new FileChooser();
        }

        String safeTopic = topic.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replace(" ", "_");
        if (safeTopic.length() > 50) safeTopic = safeTopic.substring(0, 50);
        String suggestedFileName = String.format("%s-%s.txt", type.replace(" ", "_"), safeTopic);
        fileChooser.setInitialFileName(suggestedFileName);

        Stage stage = Stage.getWindows().stream()
                .filter(window -> window instanceof Stage && window.isShowing())
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);
        if (stage == null) {
            showAlert(Alert.AlertType.ERROR, "UI Error", "Could not display save dialog (cannot get current stage).");
            return;
        }
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(content);
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Content saved to " + file.getName());
            } catch (IOException e) {
                System.err.println("Error saving file '" + file.getAbsolutePath() + "': " + e.getMessage());
                //e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save file. Error: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the save operation when the "Save" button is clicked.
     * If the content is found, it triggers any necessary modifications, retrieves the updated content, and saves
     * it to a file. The file save operation includes suggesting a file name based on the
     * material type and topic. If no content or an invalid material type is provided,
     * an appropriate alert is displayed to notify the user.
     */
    @FXML
    private void onSaveClicked(){
        if (currentMaterial != null) {
            System.out.println("Content saving...");
            onModifyClicked(false);
            Material updated_material = contentDAO.getMaterialContent(materialID, materialType);
            saveContentToFileFromContentView(updated_material.getContent(), materialType, updated_material.getTopic());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No material found", "Provided MaterialID is null");
        }
    }

    /**
     * Handles the event triggered when the "Modify" button is clicked.
     * This method validates the current state and modifies the content and topic
     * of a material if valid inputs are provided. It verifies that both "generatedTextArea"
     * and "currentMaterial" are not null before proceeding. On successful modification,
     * the updated content and topic name are sent to the `contentDAO` for updating the database.
     */
    @FXML
    private void onModifyClicked() {
        onModifyClicked(true);
    }

    private void onModifyClicked(boolean showAlert) {
        if (generatedTextArea == null || currentMaterial == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "no content to modify");
            return;
        }
        // Retrieve updated content
        String updatedContent = generatedTextArea.getText();
        String newTopicName = topicTextField.getText();

        try {
            boolean isSaved = contentDAO.setContent(materialID, updatedContent, newTopicName, materialType);
            if (isSaved && showAlert) {
                showAlert(Alert.AlertType.CONFIRMATION, "Content Updated", "Content updated successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Content Update Failed", "Could not update content. Error: " + e.getMessage());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Week selection logic">

    /**
     * Configures and initializes the behavior and contents of the `weekCheckBox` component.
     * Populates the `weekCheckBox` with week numbers ranging from 1 to 13
     * and sets its default value to the week associated with the current material,
     * as retrieved from the `contentDAO`. It also attaches a listener to detect and
     * handle changes in the selected week value.
     */
    public void setUpWeekCheckBox() {
        // Populate weekCheckBox with week numbers (1-13)
        ObservableList<Integer> weeks = FXCollections.observableArrayList();
        for (int i = 1; i <= 13; i++) {
            weeks.add(i);
        }
        weekCheckBox.setItems(weeks);

        // Add a listener to handle the selection of a week
        weekCheckBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                handleWeekSelection(newValue);
            }
        });
    }

    /**
     * Handles the selection of a specific week for a material and updates the
     * corresponding week value in the database. Displays appropriate messages
     * and alerts based on the success or failure of the update operation.
     *
     * @param weekNumber the week number to be updated for the current material
     */
    private void handleWeekSelection(int weekNumber) {
        try {
            boolean updated = contentDAO.updateWeek(weekNumber, materialID);

            if (updated) {
                System.out.println("Week updated: " + weekNumber + " for " + materialID);
            }
            else {
                System.out.println("Week update failed: " + weekNumber);
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update week.");
            }
        } catch (Exception e) {
            System.err.println("Error updating week: " + e.getMessage());
            //e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update week. Error: " + e.getMessage());
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
        // Retrieve teacher's associated classrooms
        try{
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
     * Configures the behavior of the `topicTextField` to allow editing when clicked and
     * disable editing upon losing focus or pressing Enter. Validates input to ensure the
     * topic name is not empty, displaying an error alert if the validation fails.
     */
    private void setupLabelToggleBehavior() {
        topicTextField.setEditable(false); // Disable editing initially

        // Allow edits when clicked
        topicTextField.setOnMouseClicked(event -> topicTextField.setEditable(true));

        // Save changes on focus loss
        topicTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                topicTextField.setEditable(false); // Disable editing again
                String newTopicName = topicTextField.getText();
                if (newTopicName.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Topic name cannot be empty");
                }
            }
        });

        // Save changes on pressing Enter
        topicTextField.setOnAction(event -> {
            topicTextField.setEditable(false); // Disable editing
            String newTopicName = topicTextField.getText();
            if (newTopicName.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Topic name cannot be empty");
            }
        });

    }

    /**
     * Displays an alert dialog to the user with the specified alert type, title, and content.
     *
     * @param alertType the type of alert to be displayed (e.g., CONFIRMATION, ERROR, INFORMATION, WARNING)
     * @param title the title of the alert dialog
     * @param content the text content to be displayed within the alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //</editor-fold>

}
