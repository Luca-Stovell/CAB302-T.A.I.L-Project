package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.HelloApplication;
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
    private TextField setEmailTextField;
    @FXML
    private PasswordField registrationPasswordField;
    @FXML
    private PasswordField registrationConfirmPasswordField;
    @FXML
    private Button registerButton;


    // Handles the register button click by switching the current stage to the login page scene.
    // This is typically called after a successful registration to redirect the user back to login.

    //Validation for the registration needs to be completed
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    public boolean isFieldEmpty(TextInputControl field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }


    //Handles the validation of the email in the registration form.

    public boolean verifyEmail() {
        String email = setEmailTextField.getText();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(emailRegex);
    }

    /*public boolean verifyName() {
        String Name = firstNameTextField.getText();
    }*/

}
