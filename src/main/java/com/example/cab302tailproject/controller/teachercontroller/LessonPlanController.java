package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.Worksheet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class LessonPlanController {

    @FXML
    private Label editableLabel;

    @FXML
    private TextField editField;


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
     * Typically updated when transitioning between different scenes in the interface.
     */
    private static VBox previousView;

    private Material currentMaterial;
    private IContentDAO contentDAO;
    @FXML
    private VBox dynamicContentBox;

    private String materialType;
    private int materialID;

    public LessonPlanController(){
        ;
    }

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
                generatedTextArea.setText(lesson.getContent());
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
                generatedTextArea.setText(worksheet.getContent());
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
        setupLabelToggleBehavior();
    }

    @FXML
    public void initialize() {
        ;
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

    @FXML
    private void onSaveClicked(){
        if (currentMaterial != null) {
            if ("lesson".equals(materialType)) {
                Lesson lesson = contentDAO.getLessonContent(materialID);
                if (lesson != null) {
                    System.out.println("Lesson plan saving...");
                    onModifyClicked();
                    saveContentToFileFromContentView(lesson.getContent(), "Lesson Plan", lesson.getTopic());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No content", "No lesson found with this materialID");
                }
            }
            else if ("worksheet".equals(materialType)) {
                Worksheet worksheet = contentDAO.getWorksheetContent(materialID);
                if (worksheet != null) {
                    System.out.println("Worksheet saving...");
                    onModifyClicked();
                    saveContentToFileFromContentView(worksheet.getContent(), "Worksheet", worksheet.getTopic());
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

    @FXML
    private void onModifyClicked() {
        if (generatedTextArea == null || currentMaterial == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "no content to modify");
            return;
        }
        // Retrieve updated content
        String updatedContent = generatedTextArea.getText();

        try {
            contentDAO.setContent(materialID, updatedContent);

            //showAlert(Alert.AlertType.INFORMATION, "Content Updated", "Content updated successfully.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Content Update Failed", "Could not update content. Error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }



    private void setupLabelToggleBehavior() {
        // Convert Label into editable field on click
        editableLabel.setOnMouseClicked(event -> enableEditing());

        // Save updated text on pressing Enter (or losing focus)
        editField.setOnAction(event -> updateTopicEdit());
        editField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // When the TextField loses focus
                updateTopicEdit();
            }
        });
    }

    /**
     * Enables editing of the label by switching to a TextField.
     */
    private void enableEditing() {
        // Set the text field's content to the label's current text
        editField.setText(editableLabel.getText());

        // Hide the label & show the text field
        editableLabel.setVisible(false);
        editField.setVisible(true);
        editField.requestFocus(); // Focus on the text field
    }

    /**
     * Saves the text from the TextField back to the Label.
     */
    private void updateTopicEdit() {
        String newText = editField.getText(); // Get the text from the input field

        // Update the label to reflect the new text
        editableLabel.setText(newText);

        // Hide the TextField and show the Label
        editField.setVisible(false);
        editableLabel.setVisible(true);
    }


}
