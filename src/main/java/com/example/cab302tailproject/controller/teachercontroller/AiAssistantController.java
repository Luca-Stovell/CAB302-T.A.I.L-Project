package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.model.UserSession;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.example.cab302tailproject.ollama4j.OllamaSyncResponse;
import io.github.ollama4j.exceptions.OllamaBaseException;

import java.io.IOException;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;
import static com.example.cab302tailproject.utils.TextFormatting.bindTimeToLabel;


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
    @FXML private Button studentsButton;

    // --- FXML UI Element References for Main AI Assistant Content ---
    @FXML private TextField userInputTextField; // For user to type their question
    @FXML private Button sendHelpRequestButton; // Button to send the question
    @FXML private TextArea aiResponseArea;   // To display the AI's response

    // --- FXML UI Element references for dynamic labels ---
    @FXML private Label loggedInTeacherLabel;
    @FXML private Label timeLabel;

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
        loggedInTeacherLabel.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");
    }

    // --- Event Handlers for Sidebar/Top Buttons ---
    //<editor-fold desc="Navigation - Direct Scene Switching">
    /**
     * Handles clicks on the "Generate" button in the sidebar.
     * Reloads the lesson generation view on the current stage.
     */
    @FXML
    private void onSidebarGenerateClicked() throws IOException {
        loadScene("lesson_generator-teacher.fxml", sidebarGenerateButton, false);
    }

    /**
     * Handles clicks on the "Review" button in the sidebar.
     * Loads the teacher review view on the current stage.
     */
    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-teacher.fxml", sidebarReviewButton, false);
    }

    /**
     * Handles clicks on the "Analysis" button in the sidebar.
     * Loads the teacher analysis view on the current stage.
     */
    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-teacher.fxml", sidebarAnalysisButton, true);
    }

    /**
     * Handles clicks on the "A.I. Assistance" button in the sidebar.
     * Loads the teacher AI assistance view on the current stage.
     */
    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-teacher.fxml", sidebarAiAssistanceButton, true);
    }

    /**
     * Handles clicks on the "Library" button in the sidebar.
     * Loads the teacher library view on the current stage.
     */
    @FXML
    private void onSidebarLibraryClicked() throws IOException {
        loadScene("library-teacher.fxml", sidebarLibraryButton, true);
    }

    /**
     * Handles clicks on the "Students" button in the top navigation.
     * Loads a students view on the current stage.
     */
    @FXML
    private void onStudentsClicked() throws IOException {
        loadScene("classroom-teacher-view.fxml", studentsButton, true);
    }

    @FXML private void logoutButtonClicked() throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", studentsButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
    //</editor-fold>

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
}
