package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.TailApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.fxml.FXML;

public class RegistrationController {
    //Makes the components in the Registration.FXML readable to the program
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField registrationPasswordField;
    @FXML
    private PasswordField registrationConfirmPasswordField;
    @FXML
    private Button registrationButton;
    @FXML
    private CheckBox termsAndConditionsButton;
    @FXML
    private Label errorText;
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

    // Handles the register button click by switching the current stage to the login page scene.
    // This is typically called after a successful registration to redirect the user back to login.
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        if (isRegistrationValid()) {
            Stage stage = (Stage) registrationButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("LoginPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
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
        boolean userTypeSelected = userType.getSelectedToggle() != null;
        registrationButton.setDisable(!(accepted && userTypeSelected));
    }

    @FXML
    protected void onAgreeToTermsAndConditions() {
        boolean accepted = termsAndConditionsButton.isSelected();
        registrationButton.setDisable(!accepted);
    }

    @FXML
    protected void onUserSelected() {
        Toggle selected = userType.getSelectedToggle();
        registrationButton.setDisable(selected == null); // disable if nothing is selected
    }
}