package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.StudentDAO;
import com.example.cab302tailproject.DAO.TeacherDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.DAO.SqliteTeacherDAO;
import com.example.cab302tailproject.TailApplication;
// Model imports Student and Teacher are not strictly needed here if only passing data to DAO
// import com.example.cab302tailproject.model.Student;
// import com.example.cab302tailproject.model.Teacher;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent; // Import ActionEvent

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;

/**
 * Controller for the user registration view (registration_page.fxml).
 * Handles input validation and registration of new Student or Teacher accounts.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.3
 */
public class RegistrationController {
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField registrationPasswordField;
    @FXML private PasswordField registrationConfirmPasswordField;
    @FXML private Button registrationButton;
    @FXML private CheckBox termsAndConditionsButton;
    @FXML private Label errorText;
    @FXML private RadioButton setStudentButton;
    @FXML private RadioButton setTeacherButton;
    @FXML private ToggleGroup userType; // fx:id for the ToggleGroup in FXML

    private TeacherDAO teacherDao;
    private StudentDAO studentDao;

    /**
     * Constructor initializes the DAO instances.
     */
    public RegistrationController() {
        teacherDao = new SqliteTeacherDAO();
        studentDao = new SqlStudentDAO();
        // Database table creation should be handled once, e.g., by DatabaseInitializer in TailApplication
    }

    /**
     * Initializes the controller after FXML loading.
     * Sets initial state for UI elements, like disabling the registration button
     * and adding listeners to enable it based on input.
     */
    @FXML
    public void initialize() {
        if (errorText != null) {
            errorText.setText(""); // Clear any previous error messages
            errorText.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            System.err.println("WARN: errorText Label is not injected in RegistrationController. Check FXML fx:id 'errorText'.");
        }

        if (userType == null || setStudentButton == null || setTeacherButton == null || termsAndConditionsButton == null || registrationButton == null) {
            System.err.println("WARN: One or more UI elements for registration state not injected. Check FXML fx:id attributes.");
        } else {
            termsAndConditionsButton.setOnAction(this::updateRegisterButtonState);
            // Add listener to the ToggleGroup for radio button changes
            userType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateRegisterButtonState(null));
            // Initial state of the button
            updateRegisterButtonState(null); // Pass null or a dummy event if needed
        }
    }

    /**
     * Handles the registration button click. Validates input, adds the user
     * to the appropriate database table, and navigates back to the login page on success.
     *
     * @param event The action event from the button click.
     */
    @FXML
    protected void onRegisterButtonClick(ActionEvent event) {
        if (errorText != null) errorText.setText("");

        if (isRegistrationValid()) {
            if (addToDatabase()) { // If adding to DB was successful
                try {
                    Node sourceNode = (Node) event.getSource();
                    Stage stage = (Stage) sourceNode.getScene().getWindow();
                    URL fxmlUrl = TailApplication.class.getResource("login_page.fxml");
                    if (fxmlUrl == null) throw new IOException("Cannot find login_page.fxml. Check path in resources.");

                    FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                    Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
                    stage.setTitle("TAIL - Login");
                    stage.setScene(scene);
                } catch (IOException e) {
                    System.err.println("Error loading login_page.fxml: " + e.getMessage());
                    e.printStackTrace();
                    setRegistrationError("Error navigating to login page.");
                } catch (IllegalStateException e) {
                    System.err.println("Error getting stage/scene for navigation: " + e.getMessage());
                    e.printStackTrace();
                    setRegistrationError("Navigation error after registration.");
                }
            }
            // If addToDatabase returned false, an error message should have been set there by it
        }
        // If isRegistrationValid is false, an error message should have been set by it
    }

    /**
     * Attempts to add the new user to the database based on the selected role.
     * The respective DAO methods (AddStudent/AddTeacher) now handle checking for existing emails.
     * Sets error messages on failure.
     * @return true if the user was successfully added, false otherwise.
     */
    private boolean addToDatabase() {
        String email = emailTextField.getText().trim();
        String fName = firstNameTextField.getText().trim();
        String lName = lastNameTextField.getText().trim();
        String password = registrationPasswordField.getText(); // Password validation already done

        boolean success = false;
        if (setTeacherButton.isSelected()) {
            // DAO methods now check for existing email internally before adding
            success = teacherDao.AddTeacher(email, fName, lName, password);
            if (!success) {
                // The DAO implementation should print a more specific error to console.
                // We set a general error for the user.
                setRegistrationError("Failed to register teacher. Email might already exist or a database error occurred.");
                System.out.println("Registration failed: DAO could not add teacher for " + email);
            } else {
                System.out.println("Teacher registered successfully: " + email);
            }
        } else if (setStudentButton.isSelected()) {
            success = studentDao.AddStudent(email, fName, lName, password);
            if (!success) {
                setRegistrationError("Failed to register student. Email might already exist or a database error occurred.");
                System.out.println("Registration failed: DAO could not add student for " + email);
            } else {
                System.out.println("Student registered successfully: " + email);
            }
        } else {
            setRegistrationError("Please select a role (Student or Teacher).");
            System.out.println("Registration failed: No role selected.");
        }
        return success;
    }

    /**
     * Validates all registration input fields. Sets error messages on failure using {@link #setRegistrationError(String)}.
     * @return true if all fields are valid, false otherwise.
     */
    private boolean isRegistrationValid() {
        setRegistrationError(""); // Clear previous errors

        if (firstNameTextField.getText().trim().isEmpty() || !verifyName(firstNameTextField)) {
            setRegistrationError("Please enter a valid First Name (letters only).");
            return false;
        }
        if (lastNameTextField.getText().trim().isEmpty() || !verifyName(lastNameTextField)) {
            setRegistrationError("Please enter a valid Last Name (letters only).");
            return false;
        }
        if (!verifyEmail(emailTextField)) {
            // errorText is set within verifyEmail if it fails
            return false;
        }
        if (!verifyPassword(registrationPasswordField, registrationConfirmPasswordField)) {
            // errorText is set within verifyPassword if it fails
            return false;
        }
        if (termsAndConditionsButton == null || !termsAndConditionsButton.isSelected()) { // Added null check
            setRegistrationError("Please accept the Terms and Conditions.");
            return false;
        }
        if (userType == null || userType.getSelectedToggle() == null) { // Added null check
            setRegistrationError("Please select whether you are a Student or Teacher.");
            return false;
        }
        return true;
    }

    /**
     * Validates that a name field contains only letters and is not empty.
     * @param nameField The TextField to validate.
     * @return true if the name is valid, false otherwise.
     */
    public boolean verifyName(TextField nameField){
        String name = nameField.getText().trim();
        String nameRegex = "^[A-Za-z]+$"; // Allows one or more letters
        return !name.isEmpty() && name.matches(nameRegex);
    }

    /**
     * Validates the password fields. Checks if passwords match and meet complexity requirements.
     * Sets the {@link #errorText} label if validation fails.
     * @param passwordField The primary password field.
     * @param confirmPasswordField The confirmation password field.
     * @return true if passwords are valid and match, false otherwise.
     */
    public boolean verifyPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.isEmpty()) { // Check for empty password first
            setRegistrationError("Password cannot be empty.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            setRegistrationError("Passwords do not match.");
            return false;
        }
        // Regex: At least 8 chars, 1 uppercase, 1 lowercase, 1 digit.
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (!password.matches(passwordRegex)) {
            setRegistrationError("Password: 8+ chars, 1 upper, 1 lower, 1 digit.");
            return false;
        }
        return true;
    }

    /**
     * Validates the format of the email address. Sets {@link #errorText} if invalid.
     * @param emailTextField The TextField containing the email.
     * @return true if the email format is valid, false otherwise.
     */
    public boolean verifyEmail(TextField emailTextField) {
        String email = emailTextField.getText().trim();
        if (email.isEmpty()) {
            setRegistrationError("Email cannot be empty.");
            return false;
        }
        // Basic email regex. For production, consider a more robust library or regex.
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            setRegistrationError("Invalid email format. Example: user@example.com");
            return false;
        }
        return true;
    }

    /**
     * Updates the enabled state of the registration button based on whether
     * the terms and conditions are accepted and a user type (Student/Teacher) is selected.
     * This method is called by the onAction event of the CheckBox and RadioButtons.
     * @param event The action event (can be null if called programmatically, e.g., from initialize).
     */
    @FXML
    protected void updateRegisterButtonState(ActionEvent event) { // Changed parameter to ActionEvent
        if (termsAndConditionsButton == null || userType == null || registrationButton == null) {
            System.err.println("WARN: updateRegisterButtonState - UI elements not ready.");
            return;
        }
        boolean termsAccepted = termsAndConditionsButton.isSelected();
        boolean userTypeSelected = userType.getSelectedToggle() != null;
        registrationButton.setDisable(!(termsAccepted && userTypeSelected));
    }

    /**
     * Sets the text of the registration error label.
     * @param message The error message to display.
     */
    private void setRegistrationError(String message) {
        if (errorText != null) {
            errorText.setText(message);
        } else {
            System.err.println("Registration Error (Label not available): " + message);
            showAlert(Alert.AlertType.ERROR, "Registration Error", message); // Fallback alert
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
