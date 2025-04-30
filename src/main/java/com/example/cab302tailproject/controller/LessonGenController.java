package com.example.cab302tailproject.controller; // Adjust if your package structure is different

// Core JavaFX imports
import javafx.application.Platform; // Import Platform for runLater
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// IO Imports for file writing
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// --- IMPORT YOUR OLLAMA CLASS ---
// Ensure this package matches the location of OllamaSyncResponse.java
import com.example.cab302tailproject.OllamaSyncResponse;
// Import specific exceptions your service might throw
import io.github.ollama4j.exceptions.OllamaBaseException;

/**
 * Controller for the Lesson Generation view.
 * Handles user input for lesson/worksheet generation type and topic,
 * interacts with the Ollama service in the background, and prompts
 * the user to save the generated content to a text file.
 */
public class LessonGenController {

    // --- FXML UI Element References ---
    @FXML private ListView<String> dashboardListView;
    @FXML private Button filesButton;
    @FXML private Button studentsButton;
    @FXML private Button homeButton;
    @FXML private Button settingsButton;
    @FXML private ToggleGroup generateToggleGroup;
    @FXML private RadioButton worksheetRadioButton;
    @FXML private RadioButton lessonPlanRadioButton;
    @FXML private TextField generatorTextField;
    @FXML private Button generateButton;

    // --- File Chooser for Saving ---
    private FileChooser fileChooser;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        System.out.println("[DEBUG] LessonGenController initializing..."); // Log init start
        // Ensure radio buttons are mutually exclusive
        if (generateToggleGroup == null) {
            System.err.println("[ERROR] generateToggleGroup is null. Check FXML connection.");
            generateToggleGroup = new ToggleGroup();
        } else {
            System.out.println("[DEBUG] generateToggleGroup found in FXML.");
        }
        worksheetRadioButton.setToggleGroup(generateToggleGroup);
        lessonPlanRadioButton.setToggleGroup(generateToggleGroup);
        worksheetRadioButton.setSelected(true);
        System.out.println("[DEBUG] Radio buttons configured.");

        // Configure the FileChooser
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Save Generated Content");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        this.fileChooser.getExtensionFilters().add(extFilter);
        this.fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        System.out.println("[DEBUG] FileChooser configured.");

        System.out.println("[DEBUG] LessonGenController initialized successfully."); // Log init end
    }

    /**
     * Handles the action event when the "Generate" button is clicked.
     */
    @FXML
    private void onGenerateClicked(ActionEvent event) {
        System.out.println("[DEBUG] onGenerateClicked: Generate button clicked."); // Log button click

        Toggle selectedToggle = generateToggleGroup.getSelectedToggle();
        String userInput = generatorTextField.getText();

        // --- 1. Input Validation ---
        if (selectedToggle == null) {
            System.out.println("[DEBUG] onGenerateClicked: Input validation failed - No radio button selected.");
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select 'Worksheet' or 'Lesson Plan'.");
            return;
        }
        String selectedGeneratorType = ((RadioButton) selectedToggle).getText();
        System.out.println("[DEBUG] onGenerateClicked: Selected type: " + selectedGeneratorType);

        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("[DEBUG] onGenerateClicked: Input validation failed - Text field is empty.");
            showAlert(Alert.AlertType.WARNING, "Input Missing", "Please enter a topic or description to generate content about.");
            generatorTextField.requestFocus();
            return;
        }
        System.out.println("[DEBUG] onGenerateClicked: User input: " + userInput.trim());
        System.out.println("[DEBUG] onGenerateClicked: Input validation passed.");

        // --- 2. Prepare for Generation ---
        String prompt = String.format("Generate a %s based on the following topic: %s",
                selectedGeneratorType, userInput.trim());
        System.out.println("[DEBUG] onGenerateClicked: Constructed prompt: " + prompt);

        generateButton.setDisable(true);
        generatorTextField.setDisable(true);
        System.out.println("[DEBUG] onGenerateClicked: UI elements disabled.");

        // --- 3. Run Ollama Call in Background Task ---
        Task<String> generateTask = new Task<>() {
            @Override
            protected String call() throws OllamaBaseException, IOException, InterruptedException {
                System.out.println("[DEBUG] Task.call: Background task started.");
                System.out.println("[DEBUG] Task.call: Sending prompt to Ollama -> " + prompt);
                OllamaSyncResponse service = null;
                try {
                    // Create a new instance of the Ollama service class for this request
                    service = new OllamaSyncResponse(prompt);
                    System.out.println("[DEBUG] Task.call: OllamaSyncResponse instance created.");

                    // Execute the synchronous Ollama API call
                    String response = service.ollamaResponse();
                    System.out.println("[DEBUG] Task.call: Received response from Ollama (length: " + (response != null ? response.length() : "null") + ").");
                    return response; // Return the generated text
                } catch (OllamaBaseException | IOException | InterruptedException e) {
                    System.err.println("[ERROR] Task.call: Exception during Ollama call: " + e.getClass().getName() + " - " + e.getMessage());
                    // It's important to re-throw the exception so setOnFailed is triggered
                    throw e;
                } catch (Throwable t) { // Catch any other unexpected errors
                    System.err.println("[ERROR] Task.call: Unexpected Throwable during Ollama call: " + t.getClass().getName() + " - " + t.getMessage());
                    // Wrap in a runtime exception to ensure setOnFailed is triggered
                    throw new RuntimeException("Unexpected error in background task", t);
                } finally {
                    System.out.println("[DEBUG] Task.call: Background task finished execution.");
                }
            }
        };

        // --- 4. Handle Task Completion (on JavaFX Application Thread) ---
        generateTask.setOnSucceeded(workerStateEvent -> {
            System.out.println("[DEBUG] Task.onSucceeded: Task completed successfully.");
            String generatedContent = generateTask.getValue();
            System.out.println("[DEBUG] Task.onSucceeded: Retrieved generated content (length: " + (generatedContent != null ? generatedContent.length() : "null") + ").");

            // Re-enable UI elements *before* showing the save dialog
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
            System.out.println("[DEBUG] Task.onSucceeded: UI elements re-enabled.");

            // Prompt the user to save the generated content
            saveContentToFile(generatedContent, selectedGeneratorType, userInput.trim());
        });

        // --- 5. Handle Task Failure (on JavaFX Application Thread) ---
        generateTask.setOnFailed(workerStateEvent -> {
            System.err.println("[ERROR] Task.onFailed: Task failed execution.");
            Throwable exception = generateTask.getException();
            System.err.println("[ERROR] Task.onFailed: Exception type: " + exception.getClass().getName());
            System.err.println("[ERROR] Task.onFailed: Exception message: " + exception.getMessage());
            exception.printStackTrace(); // Print stack trace for detailed debugging

            String errorMessage = "Could not generate content.";
            // Add more specific messages based on exception type
            if (exception instanceof IOException) {
                errorMessage += " A network error occurred or the Ollama server might be down/unreachable.";
            } else if (exception instanceof OllamaBaseException) {
                errorMessage += " An error occurred within the Ollama API.";
            } else if (exception instanceof InterruptedException) {
                errorMessage += " The generation process was interrupted.";
                Thread.currentThread().interrupt();
            } else if (exception.getCause() != null) { // Check cause for wrapped exceptions
                errorMessage += " An unexpected error occurred: " + exception.getCause().getClass().getSimpleName();
            }
            else {
                errorMessage += " An unexpected error occurred.";
            }

            showAlert(Alert.AlertType.ERROR, "Generation Failed", errorMessage + "\nPlease check console logs for details.\nError: " + exception.getMessage());

            // Re-enable UI elements after failure
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
            System.out.println("[DEBUG] Task.onFailed: UI elements re-enabled after failure.");
        });

        // --- 6. Start the Background Task ---
        new Thread(generateTask).start();
        System.out.println("[DEBUG] onGenerateClicked: Background generation task thread started.");
    }

    /**
     * Prompts the user to select a file location and saves content.
     */
    private void saveContentToFile(String content, String type, String topic) {
        System.out.println("[DEBUG] saveContentToFile: Attempting to save content.");
        if (content == null) {
            System.err.println("[ERROR] saveContentToFile: Content to save is null.");
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save null content.");
            return;
        }
        String safeTopic = topic.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replace(" ", "_");
        if (safeTopic.length() > 50) {
            safeTopic = safeTopic.substring(0, 50);
        }
        String suggestedFileName = String.format("%s-%s.txt", type.replace(" ", "_"), safeTopic);
        fileChooser.setInitialFileName(suggestedFileName);
        System.out.println("[DEBUG] saveContentToFile: Suggested filename: " + suggestedFileName);

        Stage stage = (Stage) generateButton.getScene().getWindow();
        if (stage == null) {
            System.err.println("[ERROR] saveContentToFile: Could not get stage to show save dialog.");
            showAlert(Alert.AlertType.ERROR, "UI Error", "Could not display the save file dialog.");
            return;
        }
        System.out.println("[DEBUG] saveContentToFile: Stage obtained successfully.");

        File file = null;
        try {
            System.out.println("[DEBUG] saveContentToFile: Showing save dialog...");
            file = fileChooser.showSaveDialog(stage);
        } catch (Exception e) {
            System.err.println("[ERROR] saveContentToFile: Exception occurred showing save dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "UI Error", "An error occurred while trying to show the save dialog.");
            return;
        }


        if (file != null) {
            System.out.println("[DEBUG] saveContentToFile: User selected file: " + file.getAbsolutePath());
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                System.out.println("[DEBUG] saveContentToFile: Writing content to file...");
                writer.println(content);
                System.out.println("[DEBUG] saveContentToFile: Content successfully written.");
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Content saved successfully to:\n" + file.getName());
            } catch (IOException e) {
                System.err.println("[ERROR] saveContentToFile: IOException writing file '" + file.getAbsolutePath() + "': " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save the file.\nError: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ERROR] saveContentToFile: Unexpected exception writing file '" + file.getAbsolutePath() + "': " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Save Failed", "An unexpected error occurred while saving the file.\nError: " + e.getMessage());
            }
        } else {
            System.out.println("[DEBUG] saveContentToFile: File save operation cancelled by user.");
            // Optionally show feedback about cancellation
            // showAlert(Alert.AlertType.INFORMATION, "Save Cancelled", "File saving was cancelled.");
        }
    }


    /**
     * Helper method to display a standard JavaFX Alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        System.out.println("[DEBUG] showAlert: Displaying alert - Type: " + alertType + ", Title: " + title); // Log alert display
        // Ensure alerts are shown on the FX thread, especially if called unexpectedly from background
        if (!Platform.isFxApplicationThread()) {
            System.out.println("[DEBUG] showAlert: Not on FX thread, using Platform.runLater.");
            Platform.runLater(() -> {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        } else {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    // --- Placeholder Action Handlers ---
    @FXML private void onHomeClicked(ActionEvent actionEvent) { System.out.println("[DEBUG] Home button clicked."); generatorTextField.clear(); if (dashboardListView != null && !dashboardListView.getItems().isEmpty()) { dashboardListView.getSelectionModel().clearSelection(); } }
    @FXML private void onFilesClicked(ActionEvent actionEvent) { System.out.println("[DEBUG] Files button clicked."); }
    @FXML private void onStudentsClicked(ActionEvent actionEvent) { System.out.println("[DEBUG] Students button clicked."); }
    @FXML private void onSettingsClicked(ActionEvent actionEvent) { System.out.println("[DEBUG] Settings button clicked."); }
}
