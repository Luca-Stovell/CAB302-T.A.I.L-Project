package com.example.cab302tailproject.controller;


import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.example.cab302tailproject.ollama4j.OllamaSyncResponse;
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
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. Sets up UI defaults and the FileChooser.
     */
    @FXML
    public void initialize() {
        // Ensure radio buttons are mutually exclusive
        if (generateToggleGroup == null) {
            // Fallback if FXML injection fails (shouldn't happen with correct FXML)
            System.err.println("WARN: generateToggleGroup was null during initialization. Check FXML fx:id.");
            generateToggleGroup = new ToggleGroup();
        }
        worksheetRadioButton.setToggleGroup(generateToggleGroup);
        lessonPlanRadioButton.setToggleGroup(generateToggleGroup);
        worksheetRadioButton.setSelected(true); // Default selection

        // Configure the FileChooser
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Save Generated Content");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
        this.fileChooser.getExtensionFilters().add(extFilter);
        this.fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    /**
     * Handles the action event when the "Generate" button is clicked.
     * Validates input, constructs a prompt, runs the Ollama generation
     * in a background task, and handles success (saving file) or failure (alert).
     */
    @FXML
    private void onGenerateClicked(ActionEvent event) {
        Toggle selectedToggle = generateToggleGroup.getSelectedToggle();
        String userInput = generatorTextField.getText();

        // --- Input Validation ---
        if (selectedToggle == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select 'Worksheet' or 'Lesson Plan'.");
            return;
        }
        String selectedGeneratorType = ((RadioButton) selectedToggle).getText();

        if (userInput == null || userInput.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Missing", "Please enter a topic or description to generate content about.");
            generatorTextField.requestFocus();
            return;
        }

        // --- Prepare for Generation ---
        String prompt = String.format("Generate a %s based on the following topic: %s",
                selectedGeneratorType, userInput.trim());

        // Disable UI elements to prevent concurrent requests
        generateButton.setDisable(true);
        generatorTextField.setDisable(true);

        // --- Run Ollama Call in Background Task ---
        Task<String> generateTask = new Task<>() {
            @Override
            protected String call() throws OllamaBaseException, IOException, InterruptedException {
                // This code runs on a separate, non-UI thread
                OllamaSyncResponse service = null;
                try {
                    // Create a new instance of the Ollama service class for this request
                    service = new OllamaSyncResponse(prompt);
                    // Execute the synchronous Ollama API call
                    String response = service.ollamaResponse();
                    return response; // Return the generated text
                } catch (OllamaBaseException | IOException | InterruptedException e) {
                    // Re-throw checked exceptions to be handled by onFailed
                    throw e;
                } catch (Throwable t) {
                    // Catch unexpected errors and wrap them
                    System.err.println("Unexpected error in background task: " + t.getMessage()); // Keep essential error log
                    t.printStackTrace(); // Keep stack trace for unexpected errors
                    throw new RuntimeException("Unexpected error in background task", t);
                }
            }
        };

        // --- Handle Task Completion (on JavaFX Application Thread) ---
        generateTask.setOnSucceeded(workerStateEvent -> {
            String generatedContent = generateTask.getValue(); // Get result from background task

            // Re-enable UI elements *before* showing the save dialog
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);

            // Prompt the user to save the generated content
            saveContentToFile(generatedContent, selectedGeneratorType, userInput.trim());
        });

        // --- Handle Task Failure (on JavaFX Application Thread) ---
        generateTask.setOnFailed(workerStateEvent -> {
            Throwable exception = generateTask.getException(); // Get the exception from the background task
            // Log the essential error details
            System.err.println("Ollama generation task failed: " + exception.getClass().getName() + " - " + exception.getMessage());
            exception.printStackTrace(); // Keep stack trace for debugging errors

            // Determine a user-friendly error message based on the exception type
            String errorMessage = "Could not generate content.";
            if (exception instanceof IOException) {
                errorMessage += " A network error occurred or the Ollama server might be down/unreachable.";
            } else if (exception instanceof OllamaBaseException) {
                errorMessage += " An error occurred within the Ollama API.";
            } else if (exception instanceof InterruptedException) {
                errorMessage += " The generation process was interrupted.";
                Thread.currentThread().interrupt(); // Reset the interrupt status
            } else if (exception.getCause() != null) {
                errorMessage += " An unexpected error occurred: " + exception.getCause().getClass().getSimpleName();
            } else {
                errorMessage += " An unexpected error occurred.";
            }

            // Show an error alert to the user
            showAlert(Alert.AlertType.ERROR, "Generation Failed", errorMessage + "\nPlease check application logs for details.\nError: " + exception.getMessage());

            // Re-enable UI elements after failure
            generateButton.setDisable(false);
            generatorTextField.setDisable(false);
        });

        // --- Start the Background Task ---
        new Thread(generateTask).start();
    }

    /**
     * Prompts the user to select a file location using FileChooser and saves
     * the provided content to the selected text file.
     */
    private void saveContentToFile(String content, String type, String topic) {
        if (content == null) {
            System.err.println("Error: Cannot save null content."); // Keep essential error log
            showAlert(Alert.AlertType.ERROR, "Save Error", "Cannot save null content.");
            return;
        }
        // Sanitize the topic to create a safe filename component
        String safeTopic = topic.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replace(" ", "_");
        if (safeTopic.length() > 50) { // Limit length
            safeTopic = safeTopic.substring(0, 50);
        }
        String suggestedFileName = String.format("%s-%s.txt", type.replace(" ", "_"), safeTopic);
        fileChooser.setInitialFileName(suggestedFileName);

        // Get the current window (Stage) to properly display the FileChooser
        Stage stage = (Stage) generateButton.getScene().getWindow();
        if (stage == null) {
            System.err.println("Error: Could not get stage to show save dialog."); // Keep essential error log
            showAlert(Alert.AlertType.ERROR, "UI Error", "Could not display the save file dialog.");
            return;
        }

        File file = null;
        try {
            file = fileChooser.showSaveDialog(stage);
        } catch (Exception e) {
            // Log errors related to showing the dialog itself
            System.err.println("Error showing save dialog: " + e.getMessage()); // Keep essential error log
            e.printStackTrace(); // Keep stack trace
            showAlert(Alert.AlertType.ERROR, "UI Error", "An error occurred while trying to show the save dialog.");
            return;
        }

        // If the user selected a file (didn't cancel)
        if (file != null) {
            // Use try-with-resources to ensure the writer is closed automatically
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(content); // Write the generated content to the file
                // Show a success message
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Content saved successfully to:\n" + file.getName());
            } catch (IOException e) {
                // Handle potential file writing errors
                System.err.println("Error saving file '" + file.getAbsolutePath() + "': " + e.getMessage()); // Keep essential error log
                e.printStackTrace(); // Keep stack trace
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not save the file.\nError: " + e.getMessage());
            } catch (Exception e) {
                // Catch any other unexpected errors during file write
                System.err.println("Unexpected error saving file '" + file.getAbsolutePath() + "': " + e.getMessage()); // Keep essential error log
                e.printStackTrace(); // Keep stack trace
                showAlert(Alert.AlertType.ERROR, "Save Failed", "An unexpected error occurred while saving the file.\nError: " + e.getMessage());
            }
        } else {
            // User cancelled the save dialog (optional: log this if desired)
            // System.out.println("File save operation cancelled by user.");
        }
    }


    /**
     * Helper method to display a standard JavaFX Alert dialog.
     * Ensures the alert is shown on the JavaFX Application Thread.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Ensure alerts run on the correct thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlert(alertType, title, message));
        } else {
            displayAlert(alertType, title, message);
        }
    }

    /** Displays the actual alert. Should only be called from the FX thread. */
    private void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Action Handlers for Navigation Buttons ---
    // Implement the actual navigation or actions for these buttons as needed.

    @FXML private void onHomeClicked(ActionEvent actionEvent) {
        // System.out.println("Home button clicked."); // Removed debug log
        generatorTextField.clear();
        if (dashboardListView != null && !dashboardListView.getItems().isEmpty()) {
            dashboardListView.getSelectionModel().clearSelection();
        }
    }
    @FXML private void onFilesClicked(ActionEvent actionEvent) {
        System.out.println("Files button clicked - Navigation/Action logic needed."); // Keep placeholder log
    }
    @FXML private void onStudentsClicked(ActionEvent actionEvent) {
        System.out.println("Students button clicked - Navigation/Action logic needed."); // Keep placeholder log
    }
    @FXML private void onSettingsClicked(ActionEvent actionEvent) {
        System.out.println("Settings button clicked - Navigation/Action logic needed."); // Keep placeholder log
    }
}
