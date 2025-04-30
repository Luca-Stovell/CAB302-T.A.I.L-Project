package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.TailApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

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


    // Handles the register button click by switching the current stage to the login page scene.
    // This is typically called after a successful registration to redirect the user back to login.

    //Validation for the registration needs to be completed
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        if (isRegistrationValid()) {
            Stage stage = (Stage) registrationButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("LoginPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        }
    }

    //Validation function that is used to compile all other validation logic into one function.
    private boolean isRegistrationValid() {
        // Verify first name
        if (!verifyFirstName(firstNameTextField)) {

            return false;
        }

        // Verify last name
        if (!verifyLastName(lastNameTextField)) {
            return false;
        }

        // Verify password and confirm password
        if (!verifyPassword(registrationPasswordField, registrationConfirmPasswordField)) {
            return false;
        }

        // Verify email
        if (!verifyEmail(emailTextField)) {
            return false;
        }

        // If all validations pass
        return true;
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
    public boolean confirmPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        return password.equals(confirmPassword);
    }
    //Validates that the passwords is strong enough.
    public boolean verifyPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        if (!confirmPassword(passwordField, confirmPasswordField)) {
            return false;
        }
        String password = passwordField.getText();
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

    //Handles the validation of the email in the registration form.
    public boolean verifyEmail(TextField emailTextField) {
        String email = emailTextField.getText();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(emailRegex);
    }
    @FXML
    protected void agreeToTermsAndConditions() {
        boolean accepted = termsAndConditionsButton.isSelected();
        registrationButton.setDisable(!accepted);
    }

}