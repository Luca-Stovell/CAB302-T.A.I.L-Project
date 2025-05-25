package com.example.cab302tailproject.controller.studentcontroller;

import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.UserSession;
import com.example.cab302tailproject.ollama4j.OllamaSyncResponse;
import io.github.ollama4j.exceptions.OllamaBaseException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;
import static com.example.cab302tailproject.utils.TextFormatting.bindTimeToLabel;

/**
 * Controller for the Student AI Helper view (student_ai_helper.fxml).
 * This controller enables students to interact with an AI model for assistance,
 * ask questions, and receive responses within a chat-like interface.
 * It manages user input, background AI service calls, and UI updates.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.0
 */
public class AiAssistantController_Student {
    @FXML private Button logoutButton;

    //<editor-fold desc="FXML UI Element References - Sidebar">
    /**
     * Button in the sidebar, potentially for navigating to a content generation or task area relevant to students.
     */
    @FXML private Button sidebarGenerateButton;
    /**
     * Button in the sidebar, potentially for students to review their work or feedback.
     */
    @FXML private Button sidebarReviewButton;
    /**
     * Button in the sidebar, possibly for students to view performance analysis or progress.
     */
    @FXML private Button sidebarAnalysisButton;
    /**
     * Button in the sidebar to navigate to or refresh the AI assistance view (this current view).
     */
    @FXML private Button sidebarAiAssistanceButton;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Top Navigation">
    /**
     * Button in the top navigation bar to navigate to a home screen or student dashboard.
     */
    @FXML private Button homeButton;
    /**
     * Button in the top navigation bar to access student-specific settings or preferences.
     */
    @FXML private Button settingsButton;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Main Content Area">
    /**
     * TextField where the student types their question or the topic they need help with.
     */
    @FXML private TextField userInputTextField;
    /**
     * Button that the student clicks to submit their query to the AI.
     */
    @FXML private Button sendHelpButton;
    /**
     * TextArea used to display the ongoing conversation with the AI, including the student's queries and the AI's responses.
     */
    @FXML private TextArea aiResponseArea;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Dynamic content">
        /**
     * This Label represents the UI element that displays the currently logged-in user's name.
     */
    @FXML
    Label LoggedInName;

    /**
     * Represents the JavaFX Label used to display the current time.
     */
    @FXML
    private Label timeLabel;
    //</editor-fold>

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method sets up the initial state of the UI elements, such as prompt text
     * for the AI response area and an action listener for the user input field to allow
     * submission by pressing the Enter key.
     */
    @FXML
    public void initialize() {
        System.out.println("StudentAiHelperController initialized.");
        if (aiResponseArea != null) {
            aiResponseArea.setPromptText("Hi Max! How can I help you study today? Ask a question above.");
            aiResponseArea.setWrapText(true); // Ensure text wraps within the TextArea
        }
        if (userInputTextField != null) {
            // Allow pressing Enter in the text field to trigger the help request
            userInputTextField.setOnAction(this::onSendHelpClicked);
        }

        LoggedInName.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");
    }

    //<editor-fold desc="Sidebar Buttons">
    @FXML
    public void onSidebarGenerateClicked() throws IOException {
        loadScene("learning_cards.fxml", sidebarGenerateButton, true);
    }

    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-student.fxml", sidebarReviewButton, false);
    }

    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-student.fxml", sidebarAnalysisButton, true);
    }

    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException { // TODO: Add any specific logic for re-activating or resetting this view
        loadScene("ai_assistant-student.fxml", sidebarAiAssistanceButton, true);
    }
    //</editor-fold>


    //</editor-fold>

    //<editor-fold desc="Event Handler - AI Interaction">
    /**
     * Handles the primary action of sending a student's query to the AI.
     * This method is triggered by clicking the "Help" button or pressing Enter in the
     * {@link #userInputTextField}. It validates the input, constructs a prompt,
     * calls the AI service in a background {@link Task}, and updates the {@link #aiResponseArea}
     * with the AI's response or any encountered errors.
     *
     * @param event The {@link ActionEvent} that triggered this handler.
     */
    @FXML
    private void onSendHelpClicked(ActionEvent event) {
        // Ensure UI components are available
        if (userInputTextField == null || aiResponseArea == null || sendHelpButton == null) {
            System.err.println("Error: Student AI Helper UI elements are not properly initialized. Check FXML fx:id connections.");
            showAlert(Alert.AlertType.ERROR, "UI Initialization Error", "Cannot proceed with AI help, essential UI components are missing.");
            return;
        }

        String studentQuery = userInputTextField.getText();
        // Validate that the student has entered some text
        if (studentQuery == null || studentQuery.trim().isEmpty()) {
            aiResponseArea.appendText("AI: Please type your question or the topic you need help with.\n\n");
            userInputTextField.requestFocus(); // Set focus back to the input field
            return;
        }

        String trimmedQuery = studentQuery.trim();
        aiResponseArea.appendText("You (Max): " + trimmedQuery + "\n"); // Display student's query
        userInputTextField.clear(); // Clear the input field for the next query

        // Construct a prompt for the AI model. This can be customized for student interactions.
        String promptForOllama = "A student named Max needs help with the following: \"" + trimmedQuery + "\". Please provide a clear, helpful, and age-appropriate response.";

        // Disable UI elements during the AI call to provide feedback and prevent multiple submissions
        sendHelpButton.setDisable(true);
        userInputTextField.setDisable(true);
        aiResponseArea.appendText("AI: Thinking...\n"); // Provide immediate feedback

        // --- Execute AI Service Call in a Background Task ---
        Task<String> aiHelpTask = new Task<>() {
            @Override
            protected String call() throws OllamaBaseException, IOException, InterruptedException {
                OllamaSyncResponse service;
                try {
                    // Instantiate the AI service with the constructed prompt
                    service = new OllamaSyncResponse(promptForOllama);
                    // Perform the synchronous call to the AI service
                    String response = service.ollamaResponse();
                    return response; // Return the AI's textual response
                } catch (OllamaBaseException | IOException | InterruptedException e) {
                    // Log error and re-throw to be handled by onFailed on the JavaFX Application Thread
                    System.err.println("Error during AI service call in student helper background task: " + e.getMessage());
                    throw e;
                } catch (Throwable t) {
                    // Catch any other unexpected exceptions
                    System.err.println("Unexpected error in student AI helper background task: " + t.getMessage());
                    t.printStackTrace(); // Log the full stack trace for debugging
                    // Wrap in a RuntimeException if it's not one of the expected checked exceptions
                    throw new RuntimeException("Unexpected error encountered in student AI task", t);
                }
            }
        };

        // --- Define Action on Successful AI Task Completion (runs on JavaFX Application Thread) ---
        aiHelpTask.setOnSucceeded(workerStateEvent -> {
            String aiGeneratedResponse = aiHelpTask.getValue();
            aiResponseArea.appendText("AI: " + (aiGeneratedResponse != null ? aiGeneratedResponse.trim() : "Sorry, I couldn't get a response that time.") + "\n\n");

            // Re-enable UI elements
            sendHelpButton.setDisable(false);
            userInputTextField.setDisable(false);
            userInputTextField.requestFocus(); // Return focus to the input field for convenience
        });

        // --- Define Action on Failed AI Task Completion (runs on JavaFX Application Thread) ---
        aiHelpTask.setOnFailed(workerStateEvent -> {
            Throwable exception = aiHelpTask.getException();
            String userFriendlyErrorMessage = "AI: I'm sorry, Max, I ran into a problem trying to help.";

            // Customize error message based on the type of exception
            if (exception instanceof IOException) {
                userFriendlyErrorMessage += " (It seems there's a network issue or the AI helper is currently unavailable)";
            } else if (exception instanceof OllamaBaseException) {
                userFriendlyErrorMessage += " (There was an issue with the AI service itself)";
            } else if (exception instanceof InterruptedException) {
                userFriendlyErrorMessage += " (The help request was interrupted)";
                Thread.currentThread().interrupt(); // Important to restore the interrupt status
            }

            aiResponseArea.appendText(userFriendlyErrorMessage + "\n\n");
            System.err.println("Student AI help task failed: " + exception.getClass().getName() + " - " + exception.getMessage());
            exception.printStackTrace(); // Log the full stack trace for debugging

            // Display an alert dialog to the student with error information
            showAlert(Alert.AlertType.ERROR, "AI Assistance Error", "Failed to get help from AI. " + exception.getMessage());

            // Re-enable UI elements
            sendHelpButton.setDisable(false);
            userInputTextField.setDisable(false);
            userInputTextField.requestFocus();
        });

        // --- Start the Background Task ---
        // The AI call is executed on a new thread to keep the UI responsive
        new Thread(aiHelpTask).start();
    }
    //</editor-fold>

    @FXML private void logoutButtonClicked(ActionEvent actionEvent) throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", sidebarAnalysisButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
}
