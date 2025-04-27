package ai_demo.aidemo; // Updated package name

import io.github.ollama4j.exceptions.OllamaBaseException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller class for the OllamaChatView.fxml layout.
 * Handles user interaction, calls the Ollama API, and updates the UI.
 */
public class OllamaJavaFXController {

    // --- FXML Injected Fields ---
    @FXML
    private TextField promptInput;

    @FXML
    private TextArea responseOutput;

    @FXML
    private Button sendButton;

    @FXML
    private Label statusLabel;

    // Reference to the class that checks Ollama server status
    private OllamaAPITest ollamaAPITest;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        statusLabel.setText("Checking Ollama server...");
        checkOllamaStatus();
    }

    /**
     * Checks the Ollama server status when the application starts.
     */
    private void checkOllamaStatus() {
        // Run the check in a background thread to avoid blocking the UI
        Task<Boolean> statusCheckTask = new Task<>() {
            @Override
            protected Boolean call() {
                // This runs on a background thread
                try {
                    ollamaAPITest = new OllamaAPITest();
                    return ollamaAPITest.isServerRunning();
                } catch (Exception e) {
                    System.err.println("Error checking Ollama status: " + e.getMessage());
                    return false; // Indicate failure
                }
            }
        };

        // Update UI on the JavaFX Application Thread when the task completes
        statusCheckTask.setOnSucceeded(event -> {
            boolean isRunning = statusCheckTask.getValue();
            Platform.runLater(() -> { // Ensure UI updates happen on the correct thread
                if (isRunning) {
                    statusLabel.setText("Ollama server detected. Ready.");
                    // enableInput(true); // Input is already enabled by default
                } else {
                    statusLabel.setText("Warning: Ollama server not detected. Ready."); // Update status but keep input enabled
                    // enableInput(false); // No longer disabling input here
                }
            });
        });

        // Handle failure during the task execution (e.g., unexpected error in Task)
        statusCheckTask.setOnFailed(event -> Platform.runLater(() -> {
            statusLabel.setText("Error checking Ollama server status. Ready."); // Update status but keep input enabled
            // enableInput(false); // No longer disabling input here
        }));

        // Start the background task
        new Thread(statusCheckTask).start();
    }


    /**
     * Handles the action event for the send button and the prompt input field (on Enter press).
     * Gets the prompt, validates it, triggers the background task to call the Ollama API,
     * and updates the UI with the response or error message.
     */
    @FXML
    private void handleSendPrompt() {
        String prompt = promptInput.getText();

        if (prompt == null || prompt.trim().isEmpty()) {
            promptInput.requestFocus();
            return;
        }

        // Disable input elements during processing
        enableInput(false);
        responseOutput.clear();
        statusLabel.setText("Sending prompt to AI...");

        // Create a background task for the potentially long-running network call
        Task<String> ollamaTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                // This code runs on a background thread
                OllamaSyncResponse generateResponse = new OllamaSyncResponse(prompt);
                try {
                    return generateResponse.ollamaResponse();
                } catch (OllamaBaseException | IOException | InterruptedException e) {
                    System.err.println("Error calling Ollama API: " + e.getMessage());
                    return "Error communicating with Ollama: " + e.getMessage();
                } catch (Exception e) {
                    System.err.println("Unexpected error during Ollama call: " + e.getMessage());
                    e.printStackTrace();
                    return "An unexpected error occurred.";
                }
            }
        };

        // Define what happens when the background task successfully completes
        ollamaTask.setOnSucceeded(event -> {
            String result = ollamaTask.getValue();
            Platform.runLater(() -> {
                responseOutput.setText(result != null ? result : "No response received.");
                statusLabel.setText("Response received. Ready.");
                enableInput(true); // Re-enable input fields
                promptInput.clear();
                promptInput.requestFocus();
            });
        });

        // Define what happens if the background task fails (throws an exception)
        ollamaTask.setOnFailed(event -> {
            Throwable exception = ollamaTask.getException();
            Platform.runLater(() -> {
                responseOutput.setText("Failed to get response: " + (exception != null ? exception.getMessage() : "Unknown error"));
                statusLabel.setText("Error occurred. Ready.");
                enableInput(true); // Re-enable input fields
                promptInput.requestFocus();
            });
        });

        // Start the background task in a new thread
        new Thread(ollamaTask).start();
    }

    /**
     * Helper method to enable or disable the input field and send button.
     * Ensures UI changes happen on the JavaFX Application Thread.
     * @param enable True to enable, false to disable.
     */
    private void enableInput(boolean enable) {
        if (Platform.isFxApplicationThread()) {
            promptInput.setDisable(!enable);
            sendButton.setDisable(!enable);
        } else {
            Platform.runLater(() -> {
                promptInput.setDisable(!enable);
                sendButton.setDisable(!enable);
            });
        }
    }
}
