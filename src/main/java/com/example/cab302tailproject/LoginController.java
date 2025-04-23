package com.example.cab302tailproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML
    private Button loginButton;
    @FXML
    private Button registerPageButton;
    @FXML
    private PasswordField loginPasswordTextField;
    @FXML
    private TextField loginEmailTextField;
    @FXML

    //Precondition -
    public boolean isEmpty(ActionEvent event) {
        if (!loginEmailTextField.getText().isBlank() && !loginPasswordTextField.getText().isBlank()) {
            return true;
        }
        return false;
    }

    @FXML
    protected void onRegistrationButtonClick() throws IOException {
        Stage stage = (Stage) registerPageButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("RegistrationPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}
