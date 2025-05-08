package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.ILoginDAO;
import com.example.cab302tailproject.DAO.SqliteLoginDAO;
import com.example.cab302tailproject.TailApplication;
// Removed unused model imports (Student, Teacher) as they are not directly used here
// import com.example.cab302tailproject.model.Student;
// import com.example.cab302tailproject.model.Teacher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox; // Keep if terms checkbox is used

import java.io.IOException;

import javafx.fxml.FXML;

/**
 * Controller for the user registration view.
 * Handles input validation and registration of new Student or Teacher accounts.
 */
public class RegistrationController {

    // --- FXML UI Element References ---
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField registrationPasswordField;
    @FXML private PasswordField registrationConfirmPasswordField;
    @FXML private Button registrationButton;
    @FXML private CheckBox termsAndConditionsButton; // Assuming this is still used
    @FXML private Label errorText; // Label to display validation/registration errors

    // Radio buttons and ToggleGroup for selecting user type
    @FXML private RadioButton setStudentButton;
    @FXML private RadioButton setTeacherButton;
    @FXML private ToggleGroup userType; // Should match fx:id in registration_page.fxml

    // --- DAO Instance ---
    private ILoginDAO registerDao;

    /**
     * Constructor initializes the DAO.
     */
    public RegistrationController() {
        registerDao = new SqliteLoginDAO();
    }

    /**
     * Initializes the controller after FXML loading.
     * Sets initial state, e.g., disabling the registration button.
     */
    @FXML
    public void initialize() {
        // Ensure errorText label exists and is initially empty
        if (errorText != null) {
            errorText.setText("");
        } else {
            System.err.println("WARN: errorText Label is not injected. Check FXML fx:id.");
            // Optionally create a fallback label if needed, though FXML should provide it
            // errorText = new Label();
        }

        // Ensure radio buttons and toggle group are injected
        if (userType == null || setStudentButton == null || setTeacherButton == null) {
            System.err.println("WARN: User type radio buttons or toggle group not injected. Check FXML fx:id.");
        } else {
            // Add listeners to radio buttons and checkbox to enable/disable registration button
            termsAndConditionsButton.setOnAction(event -> updateRegisterButtonState());
            userType.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateRegisterButtonState());
            // Set initial state
            updateRegisterButtonState();
        }
    }

    /**
     * Handles the registration button click. Validates input, adds the user
     * to the appropriate database table, and navigates back to the login page on success.
     */
    @FXML
    protected void onRegisterButtonClick() {
        errorText.setText(""); // Clear previous errors

        if (isRegistrationValid()) {
            if (addToDatabase()) { // If adding to DB was successful
                try {
                    // Navigate back to login page
                    Stage stage = (Stage) registrationButton.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("login_page.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
                    stage.setScene(scene);
                } catch (IOException e) {
                    System.err.println("Error loading login_page.fxml: " + e.getMessage());
                    e.printStackTrace();
                    errorText.setText("Error navigating back to login."); // Show error
                } catch (NullPointerException e) {
                    System.err.println("Error: login_page.fxml not found.");
                    e.printStackTrace();
                    errorText.setText("Critical error: Cannot find login page.");
                }
            }
            // If addToDatabase returned false, an error message should have been set there
        }
        // If isRegistrationValid is false, an error message should have been set there
    }

    /**
     * Attempts to add the new user to the database based on the selected role.
     * Checks for existing email before adding. Sets error messages on failure.
     * @return true if the user was successfully added, false otherwise.
     */
    private boolean addToDatabase() {
        String email = emailTextField.getText().trim();
        String fName = firstNameTextField.getText().trim();
        String lName = lastNameTextField.getText().trim();
        String password = registrationPasswordField.getText(); // Password validation already done

        boolean success = false;
        if (setTeacherButton.isSelected()) {
            // Check if email already exists for a teacher
            if (registerDao.checkTeacherEmailExists(email)) {
                errorText.setText("Email already registered for a teacher account.");
                System.out.println("Registration failed: Email already in use (Teacher).");
            } else {
                // Attempt to add teacher
                success = registerDao.addTeacher(email, fName, lName, password);
                if (!success) {
                    errorText.setText("Failed to register teacher account. Please try again.");
                    System.out.println("Registration failed: DAO could not add teacher.");
                } else {
                    System.out.println("Teacher registered successfully.");
                }
            }
        } else if (setStudentButton.isSelected()) {
            // Check if email already exists for a student
            if (registerDao.checkStudentEmailExists(email)) {
                errorText.setText("Email already registered for a student account.");
                System.out.println("Registration failed: Email already in use (Student).");
            } else {
                // Attempt to add student
                success = registerDao.addStudent(email, fName, lName, password);
                if (!success) {
                    errorText.setText("Failed to register student account. Please try again.");
                    System.out.println("Registration failed: DAO could not add student.");
                } else {
                    System.out.println("Student registered successfully.");
                }
            }
        } else {
            // This case should ideally not be reachable if updateRegisterButtonState works correctly
            errorText.setText("Please select a role (Student or Teacher).");
            System.out.println("Registration failed: No role selected.");
        }
        return success;
    }

    /**
     * Validates all registration input fields. Sets error messages on failure.
     * @return true if all fields are valid, false otherwise.
     */
    private boolean isRegistrationValid() {
        // Ensure errorText is available
        if (errorText == null) {
            System.err.println("CRITICAL: errorText Label is null in isRegistrationValid.");
            // Cannot display errors to user. Maybe throw exception or return false early.
            return false;
        }
        errorText.setText(""); // Clear previous errors

        // Verify first name
        if (firstNameTextField.getText().trim().isEmpty() || !verifyName(firstNameTextField)) {
            errorText.setText("Please enter a valid First Name (letters only).");
            return false;
        }

        // Verify last name
        if (lastNameTextField.getText().trim().isEmpty() || !verifyName(lastNameTextField)) {
            errorText.setText("Please enter a valid Last Name (letters only).");
            return false;
        }

        // Verify email
        if (!verifyEmail(emailTextField)) {
            errorText.setText("Please enter a valid Email Address.");
            return false;
        }

        // Verify password (error message is set within verifyPassword)
        if (!verifyPassword(registrationPasswordField, registrationConfirmPasswordField)) {
            return false;
        }

        // Verify Terms and Conditions are checked
        if (!termsAndConditionsButton.isSelected()) {
            errorText.setText("Please accept the Terms and Conditions.");
            return false;
        }

        // Verify a user type is selected
        if (userType.getSelectedToggle() == null) {
            errorText.setText("Please select whether you are a Student or Teacher.");
            return false;
        }

        // All validations passed
        return true;
    }


    /**
     * Validates that a name field contains only letters.
     * @param nameField The TextField to validate.
     * @return true if the name is valid, false otherwise.
     */
    public boolean verifyName(TextField nameField){
        String name = nameField.getText().trim();
        // Regex allows one or more letters (A-Z, a-z)
        String nameRegex = "^[A-Za-z]+$";
        return !name.isEmpty() && name.matches(nameRegex);
    }

    // Removed verifyFirstName and verifyLastName as verifyName handles both

    /**
     * Validates the password fields. Checks if passwords match and meet complexity requirements.
     * Sets the errorText label if validation fails.
     * @param passwordField The primary password field.
     * @param confirmPasswordField The confirmation password field.
     * @return true if passwords are valid and match, false otherwise.
     */
    public boolean verifyPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            errorText.setText("Passwords do not match.");
            return false;
        }

        // Check for empty password (shouldn't happen if validation is done right before)
        if (password.isEmpty()) {
            errorText.setText("Password cannot be empty.");
            return false;
        }

        // Regex: At least 8 chars, 1 uppercase, 1 lowercase, 1 digit.
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (!password.matches(passwordRegex)) {
            errorText.setText("Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one number.");
            return false;
        }

        // Passwords are valid and match
        return true;
    }


    /**
     * Validates the format of the email address.
     * @param emailTextField The TextField containing the email.
     * @return true if the email format is valid, false otherwise.
     */
    public boolean verifyEmail(TextField emailTextField) {
        String email = emailTextField.getText().trim();
        // Basic email regex (consider using a more comprehensive one or a library for production)
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return !email.isEmpty() && email.matches(emailRegex);
    }

    /**
     * Updates the enabled state of the registration button based on whether
     * the terms and conditions are accepted and a user type is selected.
     */
    @FXML
    protected void updateRegisterButtonState() {
        // Check if controls are initialized before accessing them
        if (termsAndConditionsButton == null || userType == null || registrationButton == null) {
            System.err.println("WARN: Cannot update register button state - UI elements not ready.");
            return;
        }
        boolean termsAccepted = termsAndConditionsButton.isSelected();
        boolean userTypeSelected = userType.getSelectedToggle() != null;

        // Enable button only if both conditions are true
        registrationButton.setDisable(!(termsAccepted && userTypeSelected));
    }

    // Removed createUser method as it wasn't directly adding to DB and was redundant
    // with the logic now in addToDatabase.
}
