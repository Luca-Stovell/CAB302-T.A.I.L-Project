package com.example.cab302tailproject.controller.teachercontroller;

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
            saveContentToFile(generatedContent, selectedGeneratorType, userInput.trim());
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
    private void onStudentsClicked(ActionEvent event) {
        System.out.println("Students button clicked.");
        // TODO ADD FUNCTIONALITY
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
