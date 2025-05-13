package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.*;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Session;
import com.example.cab302tailproject.model.UserDetail;
import com.example.cab302tailproject.model.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

/**
 * Controller class responsible for handling user login interactions.
 * Supports login for both Teachers and Students.
 */
public class LoginController {

    @FXML private Button loginButton;
    @FXML private Button registerPageButton;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginEmailTextField;
    @FXML private Label loginErrorLabel;

    @FXML private RadioButton teacherRadioButton;
    @FXML private RadioButton studentRadioButton;
    @FXML private ToggleGroup userTypeToggleGroup;

    private TeacherDAO teacherDao;
    private StudentDAO studentDao;

    /**
     * Constructor initializes DAO instances and ensures database is initialized.
     */
    public LoginController() {
        new DatabaseInitializer().initialize(); // Ensure tables exist
        teacherDao = new SqliteTeacherDAO();
        studentDao = new SqlStudentDAO();
    }

    /**
     * Initializes the controller after FXML loading.
     * Sets default radio button and styles the error label.
     */
    @FXML
    public void initialize() {
        if (loginErrorLabel != null) {
            loginErrorLabel.setText("");
            loginErrorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        if (userTypeToggleGroup != null && teacherRadioButton != null && studentRadioButton != null) {
            if (userTypeToggleGroup.getSelectedToggle() == null) {
                studentRadioButton.setSelected(true); // Default to Student
            }
        } else {
            System.err.println("WARN: Radio buttons or ToggleGroup not injected correctly.");
        }
    }

    /**
     * Navigates to the registration page when registration button is clicked.
     *
     * @param event The triggered action event.
     */
    @FXML
    protected void onRegistrationButtonClick(ActionEvent event) {
        try {
            Node sourceNode = (Node) event.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            URL fxmlUrl = TailApplication.class.getResource("registration_page.fxml");

            if (fxmlUrl == null) throw new IOException("registration_page.fxml not found.");

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setTitle("TAIL - Registration");
            stage.setScene(scene);
        } catch (IOException | IllegalStateException e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open registration page.");
        }
    }

    /**
     * Handles login button click. Authenticates based on user type selection.
     *
     * @param event The triggered action event.
     */
    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        loginErrorLabel.setText("");

        if (loginEmailTextField == null || loginPasswordField == null || userTypeToggleGroup == null) {
            setLoginError("Login form not initialized properly.");
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
            setLoginError("Please select Teacher or Student.");
            return;
        }

        boolean loginSuccess = false;
        String targetFxml = null;
        String windowTitle = TailApplication.TITLE;
        UserDetail userDetails = null;
        String userRole = "";

        if (selectedToggle == teacherRadioButton) {
            userRole = "Teacher";
            if (teacherDao.checkEmail(email) && teacherDao.checkPassword(email, password)) {
                loginSuccess = true;
                userDetails = teacherDao.getUserNameDetails(email);
                targetFxml = "lesson_generator-teacher.fxml";
                windowTitle = "TAIL - Teacher Dashboard";
                Session.setLoggedInTeacherEmail(email);
            }
        } else if (selectedToggle == studentRadioButton) {
            userRole = "Student";
            if (studentDao.checkEmail(email) && studentDao.checkPassword(email, password)) {
                loginSuccess = true;
                userDetails = studentDao.getUserNameDetails(email);
                targetFxml = "student-page.fxml";
                windowTitle = "TAIL - Student Dashboard";
            }
        }

        if (loginSuccess && userDetails != null) {
            UserSession.getInstance().loginUser(userDetails.firstName(), userDetails.lastName(), email, userRole);
            try {
                Node sourceNode = (Node) event.getSource();
                Stage stage = (Stage) sourceNode.getScene().getWindow();
                URL fxmlUrl = TailApplication.class.getResource(targetFxml);

                if (fxmlUrl == null) throw new IOException("Target FXML not found: " + targetFxml);

                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
                stage.setTitle(windowTitle);
                stage.setScene(scene);
            } catch (IOException | IllegalStateException e) {
                System.err.println("Navigation error after login: " + e.getMessage());
                e.printStackTrace();
                setLoginError("Login successful, but failed to load dashboard.");
                UserSession.getInstance().logoutUser();
            }
        } else {
            setLoginError("Invalid email or password.");
        }
    }

    /**
     * Sets an error message on the login form.
     *
     * @param message The message to display.
     */
    private void setLoginError(String message) {
        if (loginErrorLabel != null) {
            loginErrorLabel.setText(message);
        } else {
            System.err.println("Login error: " + message);
        }
    }

    /**
     * Shows a styled alert dialog.
     *
     * @param type    The alert type.
     * @param title   The window title.
     * @param message The message body.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
