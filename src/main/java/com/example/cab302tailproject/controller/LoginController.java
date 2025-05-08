package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.ILoginDAO;
import com.example.cab302tailproject.DAO.SqliteLoginDAO;
import com.example.cab302tailproject.TailApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML
    private Button loginButton;
    @FXML
    private Button registerPageButton;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private TextField loginEmailTextField;
    @FXML

    private ILoginDAO loginDao;
    public LoginController() {
        loginDao = new SqliteLoginDAO();
    }

    //Precondition -
    public boolean isEmpty(ActionEvent event) {
        if (!loginEmailTextField.getText().isBlank() && !loginPasswordField.getText().isBlank()) {
            return true;
        }
        return false;
    }

    @FXML
    protected void onRegistrationButtonClick() throws IOException {
        Stage stage = (Stage) registerPageButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("registration_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        if (checkLogin()) {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("student-page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        }
    }

    private boolean checkLogin() {
        String email = loginEmailTextField.getText();
        String password = loginPasswordField.getText();
        if (!loginDao.CheckEmail(email)){
            // TODO output error text to GUI
            return false;
        }
        return loginDao.checkPassword(email, password);


    }

}
