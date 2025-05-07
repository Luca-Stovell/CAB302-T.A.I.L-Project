package com.example.cab302tailproject.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.example.cab302tailproject.OllamaSyncResponse;
import io.github.ollama4j.exceptions.OllamaBaseException;
import java.io.IOException;


/**
 * Controller for the ai_assistant-teacher.fxml view.
 * This controller manages UI elements and logic specific to the AI Assistant page,
 * including interacting with an AI service to provide help.
 */
public class AiAssistantController {

    // --- FXML UI Element References for Sidebar ---
    @FXML private Button sidebarGenerateButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarAiAssistanceButton;
    @FXML private Button sidebarLibraryButton;

    // --- FXML UI Element References for Top Navigation ---
    @FXML private Button filesButton;
    @FXML private Button studentsButton;
    @FXML private Button homeButton;
    @FXML private Button settingsButton;

    // --- FXML UI Element References for Main AI Assistant Content ---
    @FXML private TextField userInputTextField; // For user to type their question
    @FXML private Button sendHelpRequestButton; // Button to send the question
    @FXML private TextArea aiResponseArea;   // To display the AI's response


    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file (ai_assistant-teacher.fxml) has been loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("AiAssistantController initialized.");
        if (aiResponseArea != null) {
            aiResponseArea.setPromptText("AI response will appear here. Ask a question above and click 'Help'.");
            aiResponseArea.setWrapText(true);
        }
        if (userInputTextField != null) {
            userInputTextField.setOnAction(this::onSendHelpRequestClicked); // Allow Enter key to send
        }
    }

    // --- Event Handlers for Sidebar Buttons ---
    // TODO (Placeholder implementations - add actual navigation/logic)
    @FXML private void onSidebarGenerateClicked(ActionEvent event) { System.out.println("Sidebar Generate button clicked."); }
    @FXML private void onSidebarReviewClicked(ActionEvent event) { System.out.println("Sidebar Review button clicked."); }
    @FXML private void onSidebarAnalysisClicked(ActionEvent event) { System.out.println("Sidebar Analysis button clicked."); }
    @FXML private void onSidebarAiAssistanceClicked(ActionEvent event) {
        System.out.println("Sidebar A.I. Assistance button clicked (current view).");
        if (userInputTextField != null) userInputTextField.clear();
        if (aiResponseArea != null) {
            aiResponseArea.clear();
            aiResponseArea.setPromptText("AI response will appear here. Ask a question above and click 'Help'.");
        }
    }
    @FXML private void onSidebarLibraryClicked(ActionEvent event) { System.out.println("Sidebar Library button clicked."); }

    // --- Event Handlers for Top Navigation Buttons ---
    // TODO (Placeholder implementations - add actual navigation/logic)
    @FXML private void onFilesClicked(ActionEvent event) { System.out.println("Files button clicked."); }
    @FXML private void onStudentsClicked(ActionEvent event) { System.out.println("Students button clicked."); }
    @FXML private void onHomeClicked(ActionEvent event) { System.out.println("Home button clicked."); }
    @FXML private void onSettingsClicked(ActionEvent event) { System.out.println("Settings button clicked."); }


    /**
     * Handles the action when the "Help" button is clicked or Enter is pressed in the input field.
     * Gets user input, calls the AI service in a background task, and displays the response.
     */
    @FXML
    private void onSendHelpRequestClicked(ActionEvent event) {
        if (userInputTextField == null || aiResponseArea == null || sendHelpRequestButton == null) {
            System.err.println("Error: UI elements for AI interaction are not properly initialized.");
            showAlert(Alert.AlertType.ERROR, "UI Error", "Cannot proceed, UI components missing.");
            return;
        }

        String userInput = userInputTextField.getText();
        if (userInput == null || userInput.trim().isEmpty()) {
            aiResponseArea.appendText("AI: Please type a question or topic you need help with.\n\n");
            userInputTextField.requestFocus();
            return;
        }

        String userQuery = userInput.trim();
        aiResponseArea.appendText("You: " + userQuery + "\n");
        userInputTextField.clear(); // Clear input after sending

        // Ollama query
        String promptForOllama = "User needs help with the following: " + userQuery + "\nProvide a helpful and concise response.";

        // Disable UI during AI call
        sendHelpRequestButton.setDisable(true);
        userInputTextField.setDisable(true);
        aiResponseArea.appendText("AI: Thinking...\n");

        // --- Run Ollama Call in Background Task ---
        Task<String> aiHelpTask = new Task<>() {
            @Override
            protected String call() throws OllamaBaseException, IOException, InterruptedException {
                // This code runs on a separate, non-UI thread
                OllamaSyncResponse service;
                try {
                    service = new OllamaSyncResponse(promptForOllama); // Pass the constructed prompt
                    String response = service.ollamaResponse();
                    return response;
                } catch (OllamaBaseException | IOException | InterruptedException e) {
                    System.err.println("Error during AI service call: " + e.getMessage());
                    throw e; // Re-throw to be caught by onFailed
                } catch (Throwable t) {
                    System.err.println("Unexpected error in AI background task: " + t.getMessage());
                    t.printStackTrace();
                    throw new RuntimeException("Unexpected error in AI task", t);
                }
            }
        };

        // --- Handle Task Completion (on JavaFX Application Thread) ---
        aiHelpTask.setOnSucceeded(workerStateEvent -> {
            String aiGeneratedResponse = aiHelpTask.getValue();
            aiResponseArea.appendText("AI: " + (aiGeneratedResponse != null ? aiGeneratedResponse.trim() : "Sorry, I didn't get a response.") + "\n\n");
            // Re-enable UI
            sendHelpRequestButton.setDisable(false);
            userInputTextField.setDisable(false);
            userInputTextField.requestFocus(); // Set focus back to input field
        });

        // --- Handle Task Failure (on JavaFX Application Thread) ---
        aiHelpTask.setOnFailed(workerStateEvent -> {
            Throwable exception = aiHelpTask.getException();
            String errorMessage = "AI: Sorry, I encountered an error while trying to help.";
            if (exception instanceof IOException) {
                errorMessage += " (Network or server issue)";
            } else if (exception instanceof OllamaBaseException) {
                errorMessage += " (AI API error)";
            }
            aiResponseArea.appendText(errorMessage + "\n\n");
            System.err.println("AI help task failed: " + exception.getClass().getName() + " - " + exception.getMessage());
            exception.printStackTrace();

            // Show a more detailed alert for the user
            showAlert(Alert.AlertType.ERROR, "AI Error", "Failed to get help from AI. " + exception.getMessage());

            // Re-enable UI
            sendHelpRequestButton.setDisable(false);
            userInputTextField.setDisable(false);
            userInputTextField.requestFocus();
        });

        // --- Start the Background Task ---
        new Thread(aiHelpTask).start();
    }

    /**
     * Helper method to display a standard JavaFX Alert dialog.
     * Ensures the alert is shown on the JavaFX Application Thread.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    /** Displays the actual alert. Should only be called from the FX thread. */
    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
