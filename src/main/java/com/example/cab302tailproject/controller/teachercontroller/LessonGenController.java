package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.IContentDAO;
import com.example.cab302tailproject.DAO.ContentDAO;
import com.example.cab302tailproject.TeacherGenerateApplication;
import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.Worksheet;
import com.example.cab302tailproject.ollama4j.OllamaSyncResponse;

import com.example.cab302tailproject.TailApplication;
import io.github.ollama4j.exceptions.OllamaBaseException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Controller for the Teacher's Lesson Generation view.
 * Handles user input for lesson/worksheet generation, interacts with the Ollama service,
 * prompts for saving content, and manages navigation to other views by directly switching
 * scenes on the current stage within each action handler.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.6
 */
public class LessonGenController {
    //<editor-fold desc="FXML UI Element References - Main Content">
    /**
     * TextField for user input to generate lessons or worksheets.
     */
    @FXML private TextField generatorTextField;
    /**
     * Button to trigger the generation process.
     */
    @FXML private Button generateButton;
    /**
     * RadioButton option for generating a worksheet.
     */
    @FXML private RadioButton worksheetRadioButton;
    /**
     * RadioButton option for generating a lesson plan.
     */
    @FXML private RadioButton lessonPlanRadioButton;
    /**
     * ToggleGroup for the worksheet and lesson plan radio buttons.
     */
    @FXML private ToggleGroup generateToggleGroup;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Navigation & Layout">
    /**
     * ListView for dashboard items, if applicable to this view.
     */
    @FXML private ListView<String> dashboardListView;
    /**
     * Button for navigating to a "Files" section.
     */
    @FXML private Button filesButton;
    /**
     * Button for navigating to a "Students" section.
     */
    @FXML private Button studentsButton;
    /**
     * Button for navigating to a "Home" or main dashboard.
     */
    @FXML private Button homeButton;
    /**
     * Button for navigating to "Settings".
     */
    @FXML private Button settingsButton;

    // Sidebar buttons - @FXML fields are optional if only onAction is used.
    /**
     * Button in the sidebar to navigate to the Library view.
     */
    @FXML private Button sidebarLibraryButton;
    /**
     * Button in the sidebar for generating content.
     */
    @FXML private Button sidebarGenerateButton;
    /**
     * Button in the sidebar for reviewing content.
     */
    @FXML private Button sidebarReviewButton;
    /**
     * Button in the sidebar for analysis.
     */
    @FXML private Button sidebarAnalysisButton;
    /**
     * Button in the sidebar for AI assistance.
     */
    @FXML private Button sidebarAiAssistanceButton;

    //</editor-fold>

    //<editor-fold desc="Other Fields">
    /**
     * FileChooser for saving generated content.
     */
    private FileChooser fileChooser;

    /**
     * Defines the default width for new scenes loaded on the current stage.
     */
    private static final double SCENE_WIDTH = 900;
    /**
     * Defines the default height for new scenes loaded on the current stage.
     */
    private static final double SCENE_HEIGHT = 600;

    /**
     * Represents the identifier of the currently selected or generated material.
     */
    private Material currentMaterial;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Dynamic content">
    /**
     * A reference to a VBox in the JavaFX view.
     * Represents a dynamic content container in the user interface, allowing the content
     * within it to be programmatically changed at runtime.
     *
     * This element is used in the context of lesson or worksheet generation and navigation,
     * enabling the controller to update or switch the displayed content as required based
     * on user interaction or application logic.
     */
    @FXML
    private VBox dynamicContentBox;  // to change the content view

    /**
     * Holds a reference to the previously displayed view within the application.
     * Used to facilitate navigation between views by tracking the last active view.
     * Typically updated when transitioning between different scenes in the interface.
     */
    private static VBox previousView;

    /**
     * A JavaFX TextArea UI component that is used to display generated text content
     * within the lesson generation view of the application. This component is
     * updated dynamically when new text is generated based on user inputs or actions
     * such as generating lessons or worksheets.
     */
    @FXML
    private TextArea generatedTextArea;
    //</editor-fold>

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the radio button toggle group and the file chooser.
     */
    @FXML
    public void initialize() {
        System.out.println("LessonGenController initializing (direct scene switching)...");
        if (generateToggleGroup == null || worksheetRadioButton == null || lessonPlanRadioButton == null) {
            System.err.println("WARN: One or more generation UI elements (ToggleGroup, RadioButtons) are null. Check FXML fx:id connections.");
            if (generateToggleGroup == null) generateToggleGroup = new ToggleGroup();
        }
        if (worksheetRadioButton != null) {
            worksheetRadioButton.setToggleGroup(generateToggleGroup);
            worksheetRadioButton.setSelected(true);
        }
        if (lessonPlanRadioButton != null) {
            lessonPlanRadioButton.setToggleGroup(generateToggleGroup);
        }

        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Save Generated Content");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        this.fileChooser.getExtensionFilters().add(extFilter);
        this.fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        System.out.println("LessonGenController initialized.");
    }

    //<editor-fold desc="AI Content Generation Logic">
    /**
     * Handles the action event when the "Generate" button (for lessons/worksheets) is clicked.
     * Validates input, constructs a prompt, runs the Ollama generation
     * in a background task, and handles success (saving file) or failure (alert).
     * @param event The action event.
     */
    @FXML
    private void onGenerateClicked(ActionEvent event) {
        System.out.println("Lesson/Worksheet Generate button clicked.");
        if (generateToggleGroup == null || generatorTextField == null || generateButton == null ||
                worksheetRadioButton == null || lessonPlanRadioButton == null) {
            showAlert(Alert.AlertType.ERROR, "UI Error", "Core generation UI elements are not initialized properly. Check FXML connections.");
            return;
        }

        Toggle selectedToggle = generateToggleGroup.getSelectedToggle();
        String userInput = generatorTextField.getText();

        if (selectedToggle == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select 'Worksheet' or 'Lesson Plan'.");
            return;
        }
        String selectedGeneratorType = ((RadioButton) selectedToggle).getText();

        if (userInput == null || userInput.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Missing", "Please enter a topic or description.");
            generatorTextField.requestFocus();
            return;
        }
        String prompt = String.format("Generate a %s based on the following topic: %s",
                selectedGeneratorType, userInput.trim());
        generateButton.setDisable(true);
        generatorTextField.setDisable(true);

        Task<String> generateTask = new Task<>() {
            @Override
            protected String call() throws OllamaBaseException, IOException, InterruptedException {
                OllamaSyncResponse service = new OllamaSyncResponse(prompt);
                return service.ollamaResponse();
            }
        };
        generateTask.setOnSucceeded(workerStateEvent -> {
            String generatedContent = generateTask.getValue();
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
            int generatedID = saveLessonToDatabase(generatedContent, selectedGeneratorType, userInput.trim());
            currentMaterial = new Material(generatedID, selectedGeneratorType);
            //saveContentToFile(generatedContent, selectedGeneratorType, userInput.trim());
            navigateToGeneratedPlan(generatedID);
        });
        generateTask.setOnFailed(workerStateEvent -> {
            Throwable exception = generateTask.getException();
            System.err.println("Ollama generation task failed: " + exception.getClass().getName() + " - " + exception.getMessage());
            exception.printStackTrace();
            String errorMessage = "Could not generate content.";
            if (exception instanceof IOException) errorMessage += " Network/server issue.";
            else if (exception instanceof OllamaBaseException) errorMessage += " AI API error.";
            else if (exception instanceof InterruptedException) {
                errorMessage += " Process interrupted.";
                Thread.currentThread().interrupt();
            }
            showAlert(Alert.AlertType.ERROR, "Generation Failed", errorMessage + "\nDetails: " + exception.getMessage());
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
        });
        new Thread(generateTask).start();
    }

    /**
     * Prompts the user to select a file location and saves the provided content.
     * @param content The string content to save.
     * @param type    The type of content (e.g., "Worksheet").
     * @param topic   The topic of the content.
     */
    private void saveContentToFile(String content, String type, String topic) {
        if (content == null) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save null content.");
            return;
        }
        String safeTopic = topic.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replace(" ", "_");
        if (safeTopic.length() > 50) safeTopic = safeTopic.substring(0, 50);
        String suggestedFileName = String.format("%s-%s.txt", type.replace(" ", "_"), safeTopic);
        fileChooser.setInitialFileName(suggestedFileName);

        Stage stage = null;
        if (generateButton != null && generateButton.getScene() != null) {
            stage = (Stage) generateButton.getScene().getWindow();
        } else if (homeButton != null && homeButton.getScene() != null) { // Fallback
            stage = (Stage) homeButton.getScene().getWindow();
        }

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
    //</editor-fold>

    /**
     * Saves the supplied content to the database. Assumes the teacher and classroom.
     * @param content The full text of content to be added
     * @param type The type of material it is: "Lesson Plan" or "Worksheet".
     * @param topic The topic of the content. Generally the AI prompt entered to create the content.
     */
    private int saveLessonToDatabase(String content, String type, String topic) {
        if (content == null) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save null content.");
            return -1;
        }
        if (topic.length() > 50) topic = topic.substring(0, 50);
        try {
            IContentDAO contentDAO = new ContentDAO();

            if (type.equals("Lesson Plan")) {
                // Create LessonContent object
                Lesson lessonContent = new Lesson(
                        topic,
                        content,                        // Generated lesson content
                        123456,                         // Placeholder TeacherID    TODO: retrieve teacher ID
                        654321                         // Placeholder ClassroomID  TODO: retrieve class ID
                );

                // Save to database
                int isSaved = contentDAO.addLessonContent(lessonContent);

                if (isSaved != -1) {
                    System.out.println("Lesson plan saved successfully!");
                    return isSaved;
                } else {
                    System.err.println("Failed to save lesson to the database.");
                }
            }
            else if (type.equals("Worksheet")) {
                // Create LessonContent object
                Worksheet worksheet = new Worksheet(
                        topic,
                        content,                        // Generated lesson content
                        123456,                         // Placeholder TeacherID    TODO: retrieve teacher ID
                        654321                         // Placeholder ClassroomID  TODO: retrieve class ID
                );
                // Save to database
                int isSaved = contentDAO.addWorksheetContent(worksheet);

                if (isSaved != -1) {
                    System.out.println("Worksheet saved successfully!");
                    return isSaved;
                } else {
                    System.err.println("Failed to save worksheet to the database.");
                }
            }
            else {
                System.out.println("Invalid content type: " + type + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred while saving the content: " + e.getMessage());
        }
        return -1;
    }

    private void navigateToGeneratedPlan(int materialID) {
        try {
            IContentDAO contentDAO = new ContentDAO();
            if (this.currentMaterial == null) {
                showAlert(Alert.AlertType.WARNING, "Material Not Found", "No material found with the given ID: " + materialID + ".");
                return;
            }

            System.out.println("Navigating to Generated Plan...");
            System.out.println("Material Retrieved: ID = " + currentMaterial.getMaterialID() +
                    ", Type = " + currentMaterial.getMaterialType());


            // Save current view logic to return back to
            System.out.println("Saving previous view with children: " + dynamicContentBox.getChildren().size());
            previousView = new VBox();
            previousView.getChildren().setAll(dynamicContentBox.getChildren()); // clone the current view
            System.out.println("Previous view saved: " + (previousView != null ? previousView.getChildren().size() : 0));

            FXMLLoader fxmlLoader = new FXMLLoader(TeacherGenerateApplication.class.getResource("lesson_plan-teacher.fxml"));
            VBox generatedContentView = fxmlLoader.load();

            LessonGenController newController = fxmlLoader.getController();

            if (currentMaterial.getMaterialType().equals("lesson") || (currentMaterial.getMaterialType().equals("Lesson Plan"))) {
                Lesson lesson = contentDAO.getLessonContent(materialID);

                if (lesson != null) {
                    newController.generatedTextArea.setText(lesson.getContent());
                } else {
                    newController.showAlert(Alert.AlertType.WARNING, "No lesson found",
                            "No lesson found for the given ID: " + materialID + ".");
                }
            }
            else if (currentMaterial.getMaterialType().equals("worksheet") || (currentMaterial.getMaterialType().equals("Worksheet"))) {
                Worksheet worksheet = contentDAO.getWorksheetContent(materialID);

                if (worksheet != null) {
                    newController.generatedTextArea.setText(worksheet.getContent());
                } else {
                    newController.showAlert(Alert.AlertType.WARNING, "No worksheet found",
                            "No worksheet found for the given ID: " + materialID + ".");
                }
            }
            else {
                newController.showAlert(Alert.AlertType.WARNING, "Invalid material type",
                        "The material type is invalid: " + currentMaterial.getMaterialType());
                return;
            }

            // Replace content in the dynamic container
            dynamicContentBox.getChildren().clear();
            dynamicContentBox.getChildren().add(generatedContentView);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load generated content view.\n" +e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClicked() {
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
        int materialID = currentMaterial.getMaterialID(); // placeholder

        IContentDAO contentDAO = new ContentDAO();
        Material material = contentDAO.getMaterialType(materialID);

        if (material != null) {
            if ("lesson".equals(material.getMaterialType())) {
                Lesson lesson = contentDAO.getLessonContent(materialID);
                if (lesson != null) {
                    System.out.println("Lesson plan saving...");
                    saveContentToFileFromContentView(lesson.getContent(), "Lesson Plan", lesson.getTopic());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No content", "No lesson found with this materialID");
                }
            }
            else if ("worksheet".equals(material.getMaterialType())) {
                Worksheet worksheet = contentDAO.getWorksheetContent(materialID);
                if (worksheet != null) {
                    System.out.println("Worksheet saving...");
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
        ;
    }

    //<editor-fold desc="Sidebar Navigation Event Handlers - Direct Scene Switching">
    /**
     * Handles clicks on the "Generate" button in the sidebar.
     * Reloads the lesson generation view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarGenerateClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarGenerateButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("lesson_generator-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "Review" button in the sidebar.
     * Loads the teacher review view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarReviewClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarReviewButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "Analysis" button in the sidebar.
     * Loads the teacher analysis view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarAnalysisClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarAnalysisButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("analytics-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "A.I. Assistance" button in the sidebar.
     * Loads the teacher AI assistance view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarAiAssistanceButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("ai_assistant-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "Library" button in the sidebar.
     * Loads the teacher library view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarLibraryClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarLibraryButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("library-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
    //</editor-fold>

    //<editor-fold desc="Top Navigation Event Handlers - Direct Scene Switching">
    /**
     * Handles clicks on the "Files" button in the top navigation.
     * Loads a files view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onFilesClicked(ActionEvent event) {
        System.out.println("Files button clicked.");
        // TODO ADD FUNCTIONALITY
    }

    /**
     * Handles clicks on the "Students" button in the top navigation.
     * Loads a students view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onStudentsClicked(ActionEvent event) throws IOException {
        // Load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("classroom-teacher-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);

        // Get the controller associated with the FXML
        ClassroomViewController controller = fxmlLoader.getController();

        // Now call the method on the actual instance
        controller.loadStudentData();

        // Set the new scene
        Stage stage = (Stage) studentsButton.getScene().getWindow();
        stage.setScene(scene);
    }


    /**
     * Handles clicks on the "Home" button in the top navigation.
     * Loads the main dashboard/home view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Home button clicked.");
        // TODO ADD FUNCTIONALITY
    }

    /**
     * Handles clicks on the "Settings" button in the top navigation.
     * Loads a settings view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Settings button clicked.");
        // TODO ADD FUNCTIONALITY
    }
    //</editor-fold>

    //<editor-fold desc="Utility Methods">

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
