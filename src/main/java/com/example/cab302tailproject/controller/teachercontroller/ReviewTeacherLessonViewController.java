package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Material;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReviewTeacherLessonViewController {

    //<editor-fold desc="Field declarations>
    public Button backButton;
    public Button modifyButton;
    public Button saveToDisk;
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
    private VBox generatedTextArea;
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
        this.materialID = currentMaterial.getMaterialID();
        materialType = currentMaterial.getMaterialType();
        this.currentMaterial = contentDAO.getMaterialContent(materialID, materialType);

        this.dynamicContentBox = dynamicContentBox;
        this.previousView = previousView;

        if (currentMaterial != null) {
            TextFlow formattedContent = formatTextWithBold(currentMaterial.getContent());
            generatedTextArea.getChildren().add(formattedContent);

            topicTextField.setText(currentMaterial.getTopic());
        }

        else {
            showAlert(Alert.AlertType.WARNING, "Invalid material type",
                    "The content is invalid. Material type: " + currentMaterial.getMaterialType() + "with ID: " + materialID + ".");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Text formatting">
    /**
     * Formats the given text content by applying bold styling to the portions of
     * the text enclosed between double asterisks (**). Content outside the markers
     * remains unstyled.
     *
     * @param content the input text containing double asterisks to indicate portions
     *                of the text to be bolded. Text outside the markers will not
     *                have bold styling applied.
     * @return a TextFlow object containing the formatted text with bold styling
     *         applied to the portions specified by the markers.
     */
    public TextFlow formatTextWithBold(String content) {
        // Create a TextFlow to render styled text
        TextFlow textFlow = new TextFlow();

        // Regular Expression to find text between `**`
        String regex = "\\*\\*(.*?)\\*\\*";
        String[] parts = content.split(regex);

        // Add non-bold text
        for (String part : parts) {
            Text normalText = new Text(part);
            textFlow.getChildren().add(normalText);

            int startIdx = content.indexOf(part) + part.length();
            if (startIdx < content.length() && content.startsWith("**", startIdx)) {
                int endIdx = content.indexOf("**", startIdx + 2) + 2;
                if (endIdx >= 0) {
                    String boldTextStr = content.substring(startIdx + 2, endIdx - 2); // Remove `**`
                    Text boldText = new Text(boldTextStr);
                    boldText.setStyle("-fx-font-weight: bold;");
                    textFlow.getChildren().add(boldText);
                    content = content.substring(endIdx); // Update remaining content
                }
            }
        }
        return textFlow;
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

            Object controller = previousView.getProperties().get("controller");
            if (controller instanceof ReviewTeacherAllContentController originController) {
                originController.reloadTableData();
            }

            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().addAll(previousView.getChildren());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No Previous View",
                    "No previous state to navigate back to.");
        }
    }

    /**
     * Handles the event triggered when the "Modify" button is clicked.
     * This method navigates the user to the "modify" view of the generated plan
     * for the current material.
     */
    public void onModifyClicked() {
        navigateToGeneratedPlan(materialID);
    }

    /**
     * Handles the save operation when the "Export" button is clicked.
     * Determines the type of material (lesson or worksheet) and attempts
     * to retrieve the corresponding content from the database. If the content is found,
     * it triggers any necessary modifications, retrieves the updated content, and saves
     * it to a file. The file save operation includes suggesting a file name based on the
     * material type and topic. If no content or an invalid material type is provided,
     * an appropriate alert is displayed to notify the user.
     */
    @FXML
    private void onSaveClicked(){
        if (currentMaterial != null) {
            System.out.println("Content saving...");
            Material updated_material = contentDAO.getMaterialContent(materialID, materialType);
            saveContentToFileFromContentView(updated_material.getContent(), materialType, updated_material.getTopic());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No material found", "Provided MaterialID is null");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Export file">
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
    //</editor-fold>

    //<editor-fold desc="Navigation">
    /**
     * Navigates to the generated plan view based on the given material ID.
     * On successful navigation, the dynamic content view is replaced with
     * the content for the selected material. Unlike other versions of this method,
     * it does not track this current page as a "previousView".
     *
     * @param materialID the unique identifier of the material to load and display
     *                   in the generated plan view
     */
    private void navigateToGeneratedPlan(int materialID) {
        try {
            if (this.currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Material Not Found", "No material found with the given ID: " + materialID + ".");
                return;
            }

            // Moving to new view
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher-lesson_modify.fxml"));
            VBox layout = fxmlLoader.load();
            ReviewTeacherLessonModify controller = fxmlLoader.getController();
            System.out.println("Navigating to generated plan for materialID: " + currentMaterial.getMaterialID() + ".");
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

    //<editor-fold desc="Util methods">
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
