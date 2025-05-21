package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.UserSession;
import com.example.cab302tailproject.model.Worksheet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class ReviewTeacherLessonModify {

    //<editor-fold desc="Field declarations">
    /**
     * Represents a ChoiceBox component that allows the user to select a specific week.
     */
    @FXML
    private ChoiceBox<Integer> weekCheckBox;

    /**
     * Represents a ChoiceBox component that allows the user to select a specific classroom.
     */
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
        System.out.println("initData called. Input MaterialID: " + material.getMaterialID());

        this.currentMaterial = material;
        this.contentDAO = new ContentDAO();
        materialID = currentMaterial.getMaterialID();
        materialType = currentMaterial.getMaterialType();
        currentMaterial = contentDAO.getMaterialContent(materialID, materialType);
        System.out.println("MaterialID = " + materialID + ", MaterialType = " + materialType);

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
//        this.currentMaterial = material;
//        this.contentDAO = new ContentDAO();
//        this.dynamicContentBox = dynamicContentBox;
//        this.previousView = previousView;
//        materialID = currentMaterial.getMaterialID();
//        setUpWeekCheckBox();
//        setUpClassCheckBox();
//        setupLabelToggleBehavior();
//
//        if (currentMaterial.getMaterialType().equals("lesson") || (currentMaterial.getMaterialType().equals("Lesson Plan"))) {
//            Lesson lesson = contentDAO.getLessonContent(materialID);
//            currentMaterial.setMaterialType("lesson");                  // Make it consistent with db
//
//            if (lesson != null) {
//                generatedTextArea.setText(lesson.getContent());
//                topicTextField.setText(lesson.getTopic());
//            } else {
//                showAlert(Alert.AlertType.WARNING, "No lesson found",
//                        "No lesson found for the given ID: " + materialID + ".");
//            }
//        }
//        else if (currentMaterial.getMaterialType().equals("worksheet") || (currentMaterial.getMaterialType().equals("Worksheet"))) {
//            Worksheet worksheet = contentDAO.getWorksheetContent(materialID);
//            currentMaterial.setMaterialType("worksheet");               // Make it consistent with db
//
//            if (worksheet != null) {
//                generatedTextArea.setText(worksheet.getContent());
//                topicTextField.setText(worksheet.getTopic());
//            } else {
//                showAlert(Alert.AlertType.WARNING, "No worksheet found",
//                        "No worksheet found for the given ID: " + materialID + ".");
//            }
//        }
//        else {
//            showAlert(Alert.AlertType.WARNING, "Invalid material type",
//                    "The material type is invalid: " + currentMaterial.getMaterialType());
//        }
    }
    //</editor-fold>

    //<editor-fold desc="Button functionality">
    /**
     * Handles the event triggered when the "Back" button is clicked.
     * Restores the previous view if one is available by clearing the current dynamic content
     * and replacing it with the previousView.
     */
    @FXML
    private void onBackClicked() {
        if (previousView != null) {
            navigateToGeneratedPlan(materialID);
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No Previous View",
                    "No previous state to navigate back to.");
        }
    }

    /**
     * Handles the event triggered when the "Modify" button is clicked.
     * Validates the current state and modifies the content and topic
     * of a material if valid inputs are provided. It verifies that both "generatedTextArea"
     * and "currentMaterial" are not null before proceeding. On successful modification,
     * the updated content and topic name are sent to the `contentDAO` for updating the database.
     */
    @FXML
    private void onSaveClicked() {
        if (generatedTextArea == null || currentMaterial == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "no content to modify");
            return;
        }
        // Retrieve updated content
        String updatedContent = generatedTextArea.getText();
        String newTopicName = topicTextField.getText();

        try {
            contentDAO.setContent(materialID, updatedContent, newTopicName, currentMaterial.getMaterialType());

            //showAlert(Alert.AlertType.INFORMATION, "Content Updated", "Content updated successfully.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Content Update Failed", "Could not update content. Error: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteClicked() {
        if (previousView != null) {
            System.out.println("Deleting content with materialID: " + materialID);
            contentDAO.deleteContent(materialID);

            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().addAll(previousView.getChildren());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No Previous View",
                    "No previous state to navigate back to.");
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
        int initialWeek = contentDAO.getWeek(materialID);
        weekCheckBox.setValue(initialWeek);
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

    //<editor-fold desc="Page navigation">
    private void navigateToGeneratedPlan(int materialID) {
        try {
            this.materialID = materialID;
            if (this.currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Material Not Found", "No material found with the given ID: " + materialID + ".");
                return;
            }

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
}
