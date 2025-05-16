package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.Worksheet;
import javafx.event.ActionEvent;
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
    //</editor-fold>

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
        this.dynamicContentBox = dynamicContentBox;
        this.previousView = previousView;
        materialID = currentMaterial.getMaterialID();

        if (currentMaterial.getMaterialType().equals("lesson") || (currentMaterial.getMaterialType().equals("Lesson Plan"))) {
            Lesson lesson = contentDAO.getLessonContent(materialID);
            currentMaterial.setMaterialType("lesson");                  // Make it consistent with db
            this.materialType = currentMaterial.getMaterialType();

            if (lesson != null) {
                TextFlow formattedContent = formatTextWithBold(lesson.getContent());
                generatedTextArea.getChildren().add(formattedContent);

                topicTextField.setText(lesson.getTopic());
            } else {
                showAlert(Alert.AlertType.WARNING, "No lesson found",
                        "No lesson found for the given ID: " + materialID + ".");
            }
        }
        else if (currentMaterial.getMaterialType().equals("worksheet") || (currentMaterial.getMaterialType().equals("Worksheet"))) {
            Worksheet worksheet = contentDAO.getWorksheetContent(materialID);
            currentMaterial.setMaterialType("worksheet");               // Make it consistent with db
            this.materialType = currentMaterial.getMaterialType();

            if (worksheet != null) {
                TextFlow formattedContent = formatTextWithBold(worksheet.getContent());
                generatedTextArea.getChildren().add(formattedContent);

                topicTextField.setText(worksheet.getTopic());
            } else {
                showAlert(Alert.AlertType.WARNING, "No worksheet found",
                        "No worksheet found for the given ID: " + materialID + ".");
            }
        }
        else {
            showAlert(Alert.AlertType.WARNING, "Invalid material type",
                    "The material type is invalid: " + currentMaterial.getMaterialType());
            return;
        }
    }

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
        // TODO: move this to util class
    }


    @FXML
    private void onBackClicked() { // TODO: make it delete the entry as well
        if (previousView != null) {
            System.out.println("Restoring previous view with children: " + previousView.getChildren().size());

            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().addAll(previousView.getChildren());
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No Previous View",
                    "No previous state to navigate back to.");
        }
    }

    public void onModifyClicked(ActionEvent event) {
        navigateToGeneratedPlan(materialID);
    }

    /**
     * Handles the save operation when the "Save" button is clicked.
     *
     * This method determines the type of material (lesson or worksheet) and attempts
     * to retrieve the corresponding content from the database. If the content is found,
     * it triggers any necessary modifications, retrieves the updated content, and saves
     * it to a file. The file save operation includes suggesting a file name based on the
     * material type and topic. If no content or an invalid material type is provided,
     * an appropriate alert is displayed to notify the user.
     *
     * - If an invalid material type is detected, a warning alert is displayed.
     * - If no content is found, a warning alert is displayed referencing the material ID.
     */
    @FXML
    private void onSaveClicked(){
        if (currentMaterial != null) {
            if ("lesson".equals(materialType)) {
                Lesson lesson = contentDAO.getLessonContent(materialID);
                if (lesson != null) {
                    System.out.println("Lesson plan saving...");
                    Lesson updated_lesson = contentDAO.getLessonContent(materialID);
                    saveContentToFileFromContentView(updated_lesson.getContent(), "Lesson Plan", updated_lesson.getTopic());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No content", "No lesson found with this materialID");
                }
            }
            else if ("worksheet".equals(materialType)) {
                Worksheet worksheet = contentDAO.getWorksheetContent(materialID);
                if (worksheet != null) {
                    System.out.println("Worksheet saving...");
                    Worksheet updated_worksheet = contentDAO.getWorksheetContent(materialID);
                    saveContentToFileFromContentView(updated_worksheet.getContent(), "Worksheet", updated_worksheet.getTopic());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No content", "No worksheet found with this materialID");
                }
            }
            else {
                showAlert(Alert.AlertType.WARNING, "Invalid material type", "The material type is invalid.");
            }
        }
        else {
            showAlert(Alert.AlertType.WARNING, "No material found", "No material found with this materialID: " + materialID + ". Current ID is " + currentMaterial);
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
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save file. Error: " + e.getMessage());
            }
        }
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

    private void navigateToGeneratedPlan(int materialID) {
        try {
            this.materialID = materialID;
            if (this.currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Material Not Found", "No material found with the given ID: " + materialID + ".");
                return;
            }

            // Save current view logic to return back to
            //previousView = new VBox();
            //previousView.getChildren().setAll(dynamicContentBox.getChildren()); // clone the current view

            // Moving to new view
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher-lesson_modify.fxml"));
            VBox layout = fxmlLoader.load();
            ReviewTeacherLessonModify controller = fxmlLoader.getController();
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
}
