package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.ILoginDAO;
import com.example.cab302tailproject.SqliteLoginDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.Teacher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.fxml.FXML;

public class RegistrationController {
    //Makes the components in the Registration.FXML readable to the program
    @FXML
    private TextField firstNameTextField = new TextField();
    @FXML
    private TextField lastNameTextField = new TextField();
    @FXML
    private TextField emailTextField = new TextField();
    @FXML
    private PasswordField registrationPasswordField = new PasswordField();
    @FXML
    private PasswordField registrationConfirmPasswordField = new PasswordField();
    @FXML
    private Button registrationButton;
    @FXML
    private CheckBox termsAndConditionsButton;
    @FXML
    private Label errorText;

    // --- Getters --- //

    public String getFirstName() {
        return firstNameTextField != null ? firstNameTextField.getText() : null;
    }

    public String getLastName() {
        return lastNameTextField.getText();
    }

    public String getEmail() {
        return emailTextField.getText();
    }

    public String getPassword() {
        return registrationPasswordField.getText();
    }

    public String getConfirmPassword() {
        return registrationConfirmPasswordField.getText();
    }

    // ---- Setters ---- //

    public void setFirstName(String firstName) {
        firstNameTextField.setText(firstName); // Access initialized component
    }

    public void setLastName(String lastName) {
        lastNameTextField.setText(lastName);
    }

    public void setEmail(String email) {
        emailTextField.setText(email);
    }

    public void setPassword(String password) {
        registrationPasswordField.setText(password);
    }

    public void setConfirmPassword(String confirmPassword) {
        registrationConfirmPasswordField.setText(confirmPassword);
    }

    // ---- Program --- //
    @FXML
    private RadioButton setStudentButton;
    @FXML
    private RadioButton setTeacherButton;
    @FXML
    private ToggleGroup userType;

    @FXML
    public void initialize() {
        registrationButton.setDisable(true); // Start with register button disabled
    }

    private ILoginDAO registerDao;
    public RegistrationController() {
        registerDao = new SqliteLoginDAO();
    }


    // Handles the register button click by switching the current stage to the login page scene.
    // This is typically called after a successful registration to redirect the user back to login.
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        if (isRegistrationValid()) {
            createUser();
            addToDatabase();

            Stage stage = (Stage) registrationButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("LoginPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        }
    }
    private void addToDatabase() {
        String email = emailTextField.getText();
        String fName = firstNameTextField.getText();
        String lName = lastNameTextField.getText();
        String password = registrationPasswordField.getText();

        // Check if student is selected
        if (setTeacherButton.isSelected()) {
            if (!registerDao.CheckEmail(email)) {
                registerDao.AddTeacher(email, fName, lName, password);
            } else {
                System.out.println("Email already in use.");
            }
        }
        // Check if teacher is selected
        else if (setStudentButton.isSelected()) {
            if (!registerDao.CheckEmail(email)) {
                registerDao.AddStudent(email, fName, lName, password);
            } else {
                System.out.println("Email already in use.");
            }
        } else {
            System.out.println("Please select a role (Student or Teacher).");
        }
    }

    private boolean isRegistrationValid() {
        // Verify first name
        if (!verifyFirstName(firstNameTextField)) {
            errorText.setText("Please enter a valid First Name");
            return false;
        }

        // Verify last name
        if (!verifyLastName(lastNameTextField)) {
            errorText.setText("Please enter a valid Last Name");
            return false;
        }

        // Verify email
        if (!verifyEmail(emailTextField)) {
            errorText.setText("Please enter a valid Email Address");
            return false;
        }
        // Error message is already set inside verifyPassword()
        return verifyPassword(registrationPasswordField, registrationConfirmPasswordField);
        // All validations passed
    }


    //Validation Logic for Name TextFields which can be applied to first and last name text fields.
    public boolean verifyName(TextField nameField){
        String name = nameField.getText();
        String nameRegex = "^[A-Za-z]+$";
        return name.matches(nameRegex);
    }

    private boolean verifyFirstName(TextField firstNameTextField){
        return verifyName(firstNameTextField);
    }

    private boolean verifyLastName(TextField lastNameTextField){
        return verifyName(lastNameTextField);
    }
    // Validation logic that confirms that passwords in both fields are correct and match
    public boolean verifyPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (errorText == null) { errorText = new Label(); } // Just to figure out the JUnit testing for now

        if (!password.equals(confirmPassword)) {
            errorText.setText("Passwords do not match.");
            return false;
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        if (!password.matches(passwordRegex)) {
            errorText.setText("Invalid Password");
            return false;
        }

        return true;
    }


    //Handles the validation of the email in the registration form.
    public boolean verifyEmail(TextField emailTextField) {
        String email = emailTextField.getText();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(emailRegex);
    }

    @FXML
    protected void updateRegisterButtonState() {
        boolean accepted = termsAndConditionsButton.isSelected();
        Toggle selectedToggle = userType.getSelectedToggle(); // Get the selected radio button

        // Store the selected user type
        if (selectedToggle != null) {
            String selectedUserType = ((RadioButton) selectedToggle).getText(); // "Student" or "Teacher"
        }

        boolean userTypeSelected = selectedToggle != null;
        registrationButton.setDisable(!(accepted && userTypeSelected)); // Disable button if either condition fails
    }

    public void createUser() {
        // Check if the student button is selected
        if (setStudentButton.isSelected()) {
            // Create a new student user (you can customize the User creation as needed)
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String email = emailTextField.getText();
            String password = registrationPasswordField.getText();

            // Create a new student with the details
            Student student = new Student(firstName, lastName, email, password);
        } else if (setTeacherButton.isSelected()) {
            // Create a new teacher user
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String email = emailTextField.getText();
            String password = registrationPasswordField.getText();

            // Create a new Teacher with the details
            Teacher teacher = new Teacher(firstName, lastName, email, password);
        }
    }

}