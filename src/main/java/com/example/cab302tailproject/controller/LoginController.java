package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.StudentDAO;
import com.example.cab302tailproject.DAO.TeacherDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.DAO.SqliteTeacherDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.UserSession;     // For storing logged-in user info
import com.example.cab302tailproject.model.UserDetail; // For fetching user names

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.application.Platform; // For showAlert

import java.io.IOException;
import java.net.URL; // For FXML loading

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

/**
 * Controller for the login view (login_page.fxml).
 * Handles user authentication based on email, password, and selected user type (Student/Teacher).
 * Navigates to the appropriate application view upon successful login and sets UserSession.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.3
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

    // --- DAO Instances ---
    private TeacherDAO teacherDao;
    private StudentDAO studentDao;

    /**
     * Constructor initializes the DAO instances.
     * Also ensures the database tables are created via DatabaseInitializer.
     */
    public LoginController() {
        new com.example.cab302tailproject.DAO.DatabaseInitializer().initialize(); // Ensure tables exist

        teacherDao = new SqliteTeacherDAO();
        studentDao = new SqlStudentDAO();
    }

    /**
     * Initializes the controller after FXML loading.
     * Ensures the error label is present and initially empty, and sets default radio button.
     */
    @FXML
    public void initialize() {
        if (loginErrorLabel != null) {
            loginErrorLabel.setText(""); // Clear error label on init
            loginErrorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Style for errors
        } else {
            System.err.println("WARN: loginErrorLabel is not injected. Check FXML fx:id 'loginErrorLabel'.");
        }

        // Ensure radio buttons and toggle group are injected from FXML
        if (userTypeToggleGroup == null || teacherRadioButton == null || studentRadioButton == null) {
            System.err.println("WARN: User type radio buttons or toggle group not injected. Check FXML fx:id attributes (userTypeToggleGroup, teacherRadioButton, studentRadioButton).");
        } else {
            // Set a default selection if not already done in FXML (Student is default in login_page.fxml)
            if (userTypeToggleGroup.getSelectedToggle() == null) {
                studentRadioButton.setSelected(true);
            }
        }
    }

    /**
     * Handles the action for the registration button click.
     * Navigates the user to the registration page scene.
     *
     * @param event The action event from the button click.
     */
    @FXML
    protected void onRegistrationButtonClick(ActionEvent event) {
        try {
            Node sourceNode = (Node) event.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            URL fxmlUrl = TailApplication.class.getResource("registration_page.fxml");
            if (fxmlUrl == null) throw new IOException("Cannot find registration_page.fxml. Check path in resources.");

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setTitle("TAIL - Registration");
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading registration_page.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open registration page.");
        } catch (IllegalStateException e) {
            System.err.println("Error getting stage/scene for registration: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot determine current window for registration.");
        }
    }

    /**
     * Handles the action for the login button click.
     * Validates credentials based on selected user type and navigates on success.
     * Displays an error message on failure.
     *
     * @param event The action event from the button click.
     */
    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        if (loginErrorLabel != null) loginErrorLabel.setText("");

        if (loginEmailTextField == null || loginPasswordField == null || userTypeToggleGroup == null ||
                teacherRadioButton == null || studentRadioButton == null) {
            System.err.println("ERROR: One or more login UI components are null. Check FXML fx:id.");
            setLoginError("Login UI is not properly initialized.");
            return;
        }

        String email = loginEmailTextField.getText().trim();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            setLoginError("Email and password cannot be empty.");
            return;
        }

        Toggle selectedToggle = userTypeToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            setLoginError("Please select whether you are a Teacher or Student.");
            return;
        }

        boolean loginSuccess = false;
        String targetFxml = null;
        String windowTitle = TailApplication.TITLE;
        UserDetail userDetails = null;
        String userRole = "";

        if (selectedToggle == teacherRadioButton) {
            userRole = "Teacher";
            // Use teacherDao to check credentials and get details
            if (teacherDao.checkEmail(email) && teacherDao.checkPassword(email, password)) {
                loginSuccess = true;
                userDetails = teacherDao.getUserNameDetails(email);
                targetFxml = "lesson_generator-teacher.fxml"; // Your teacher's main page
                windowTitle = "TAIL - Teacher Dashboard";
            }
        } else if (selectedToggle == studentRadioButton) {
            userRole = "Student";
            // Use studentDao to check credentials and get details
            if (studentDao.checkEmail(email) && studentDao.checkPassword(email, password)) {
                loginSuccess = true;
                userDetails = studentDao.getUserNameDetails(email);
                targetFxml = "student-page.fxml"; // Your student's main page
                windowTitle = "TAIL - Student Dashboard";
            }
        }

        if (loginSuccess && userDetails != null && targetFxml != null) {
            // Set UserSession with retrieved first name, last name, email, and role
            UserSession.getInstance().loginUser(userDetails.firstName(), userDetails.lastName(), email, userRole);
            System.out.println(userRole + " login successful for: " + email);

            try {
                Node sourceNode = (Node) event.getSource();
                Stage stage = (Stage) sourceNode.getScene().getWindow();
                URL fxmlUrl = TailApplication.class.getResource(targetFxml);
                if (fxmlUrl == null) throw new IOException("Cannot find target FXML: " + targetFxml + ". Check path in resources.");

                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
                stage.setTitle(windowTitle);
                stage.setScene(scene);
            } catch (IOException e) {
                System.err.println("Error loading target FXML (" + targetFxml + "): " + e.getMessage());
                e.printStackTrace();
                setLoginError("Login successful, but failed to load next page.");
                UserSession.getInstance().logoutUser(); // Clear session if navigation fails
            } catch (IllegalStateException e) {
                System.err.println("Error getting stage/scene for navigation post-login: " + e.getMessage());
                e.printStackTrace();
                setLoginError("Login successful, but failed to switch page.");
                UserSession.getInstance().logoutUser();
            }
        } else {
            setLoginError("Invalid email or password for selected user type.");
            System.out.println("Login failed for email: " + email + " as " + (selectedToggle == teacherRadioButton ? "Teacher" : "Student"));
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
            System.err.println("Login Error (Label not available): " + message);
            showAlert(Alert.AlertType.ERROR, "Login Failed", message); // Fallback alert
        }
    }

    /**
     * Helper method to display alerts.
     * @param alertType The type of alert.
     * @param title The title.
     * @param message The message.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
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
}
