package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.ILoginDAO;
import com.example.cab302tailproject.DAO.SqliteLoginDAO;
import com.example.cab302tailproject.TailApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

/**
 * Controller for the login view (login_page.fxml).
 * Handles user authentication based on email, password, and selected user type (Student/Teacher).
 * Navigates to the appropriate application view upon successful login.
 */
public class LoginController {

    // --- FXML UI Element References ---
    @FXML private Button loginButton;
    @FXML private Button registerPageButton;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginEmailTextField;
    @FXML private Label loginErrorLabel; // Label to display login errors

    // Radio buttons and ToggleGroup for selecting user type
    @FXML private RadioButton teacherRadioButton;
    @FXML private RadioButton studentRadioButton;
    @FXML private ToggleGroup userTypeToggleGroup; // Should match fx:id in login_page.fxml

    // --- DAO Instance ---
    private ILoginDAO loginDao;

    /**
     * Constructor initializes the DAO.
     */
    public LoginController() {
        loginDao = new SqliteLoginDAO();
    }

    /**
     * Initializes the controller after FXML loading.
     * Ensures the error label is present and initially empty.
     */
    @FXML
    public void initialize() {
        if (loginErrorLabel != null) {
            loginErrorLabel.setText(""); // Clear error label on init
            loginErrorLabel.setStyle("-fx-text-fill: red;"); // Style for errors
        } else {
            System.err.println("WARN: loginErrorLabel is not injected. Check FXML fx:id.");
        }
        // Ensure radio buttons and toggle group are injected
        if (userTypeToggleGroup == null || teacherRadioButton == null || studentRadioButton == null) {
            System.err.println("WARN: User type radio buttons or toggle group not injected. Check FXML fx:id.");
        } else {
            // Optional: Set a default selection if not done in FXML (Student is default in FXML)
            // studentRadioButton.setSelected(true);
        }
    }

    /**
     * Handles the action for the registration button click.
     * Navigates the user to the registration page scene.
     * @throws IOException if the registration FXML file cannot be loaded.
     */
    @FXML
    protected void onRegistrationButtonClick() throws IOException {
        try {
            // Ensure registerPageButton is not null before accessing its scene
            if (registerPageButton == null || registerPageButton.getScene() == null) {
                System.err.println("Error: Cannot get scene from registerPageButton.");
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot initiate registration navigation.");
                return;
            }
            Stage stage = (Stage) registerPageButton.getScene().getWindow();
            URL fxmlUrl = TailApplication.class.getResource("registration_page.fxml"); // Use URL
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: registration_page.fxml");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl); // Pass URL
            // Use dimensions defined in TailApplication or specific ones
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setTitle("TAIL - Registration"); // Set appropriate title
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading registration_page.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open registration page.");
        } catch (IllegalStateException e) {
            System.err.println("Error getting stage/scene: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot determine current window.");
        }
    }

    /**
     * Handles the action for the login button click.
     * Validates credentials based on selected user type and navigates on success.
     * Displays an error message on failure.
     */
    @FXML
    protected void onLoginButtonClick(ActionEvent event) { // Added ActionEvent parameter
        if (loginErrorLabel != null) loginErrorLabel.setText(""); // Clear previous errors

        // Ensure essential UI components are injected
        if (loginEmailTextField == null || loginPasswordField == null || userTypeToggleGroup == null ||
                teacherRadioButton == null || studentRadioButton == null) {
            System.err.println("ERROR: One or more login UI components are null. Check FXML fx:id.");
            setLoginError("Login UI is not properly initialized.");
            return;
        }

        String email = loginEmailTextField.getText().trim();
        String password = loginPasswordField.getText();

        // Basic validation for empty fields
        if (email.isEmpty() || password.isEmpty()) {
            setLoginError("Email and password cannot be empty.");
            return;
        }

        // Determine selected user type
        Toggle selectedToggle = userTypeToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            setLoginError("Please select whether you are a Teacher or Student.");
            return;
        }

        boolean loginSuccess = false;
        String targetFxml = null;
        String windowTitle = TailApplication.TITLE;

        if (selectedToggle == teacherRadioButton) {
            // Check teacher credentials
            loginSuccess = loginDao.checkTeacherLogin(email, password);
            if (loginSuccess) {
                targetFxml = "lesson_generator-teacher.fxml"; // Teacher's target page
                windowTitle = "TAIL - Teacher Dashboard";
                System.out.println("Teacher login successful for: " + email);
            }
        } else if (selectedToggle == studentRadioButton) {
            // Check student credentials
            loginSuccess = loginDao.checkStudentLogin(email, password);
            if (loginSuccess) {
                targetFxml = "student-page.fxml"; // Student's target page
                windowTitle = "TAIL - Student Dashboard";
                System.out.println("Student login successful for: " + email);
            }
        }

        // Handle login result
        if (loginSuccess && targetFxml != null) {
            // Navigate to the appropriate page
            try {
                // Get stage from the event source (the login button)
                Node sourceNode = (Node) event.getSource();
                if (sourceNode == null || sourceNode.getScene() == null) {
                    throw new IllegalStateException("Could not get scene from event source.");
                }
                Stage stage = (Stage) sourceNode.getScene().getWindow();
                if (stage == null) {
                    throw new IllegalStateException("Could not get stage from event source.");
                }

                URL fxmlUrl = TailApplication.class.getResource(targetFxml); // Use URL
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find target FXML file: " + targetFxml);
                }
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl); // Pass URL

                // Use dimensions defined in TailApplication or specific ones
                Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
                stage.setTitle(windowTitle);
                stage.setScene(scene);
            } catch (IOException e) {
                System.err.println("Error loading target FXML ("+ targetFxml +"): " + e.getMessage());
                e.printStackTrace();
                setLoginError("Login successful, but failed to load next page.");
            } catch (IllegalStateException e) {
                System.err.println("Error getting stage/scene: " + e.getMessage());
                e.printStackTrace();
                setLoginError("Login successful, but failed to switch page.");
            } catch (Exception e) {
                System.err.println("Unexpected error during scene switch after login: " + e.getMessage());
                e.printStackTrace();
                setLoginError("An unexpected error occurred after login.");
            }
        } else {
            // Login failed
            setLoginError("Invalid email or password for selected user type.");
            System.out.println("Login failed for email: " + email);
        }
    }

    /**
     * Sets the text of the login error label.
     * @param message The error message to display.
     */
    private void setLoginError(String message) {
        if (loginErrorLabel != null) {
            loginErrorLabel.setText(message);
        } else {
            // log error
            System.err.println("Login Error (Label not available): " + message);
            // Optionally show an alert as fallback
            showAlert(Alert.AlertType.ERROR, "Login Failed", message);
        }
    }

    /**
     * Helper method to display alerts (useful for errors if label fails or for general notifications).
     * Ensures the alert is shown on the JavaFX Application Thread.
     * @param alertType The type of alert.
     * @param title The title for the alert window.
     * @param message The message to display in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Ensure runs on FX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null); // No header
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
}
