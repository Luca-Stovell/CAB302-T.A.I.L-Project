package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.*;
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

    private TeacherDAO teacherDao;
    private StudentDAO studentDAO;
    public LoginController() {
        teacherDao = new SqliteTeacherDAO();
        studentDAO = new SqlStudentDAO();
    }

    //Precondition -
    public boolean isEmpty(ActionEvent event) {
        if (!loginEmailTextField.getText().isBlank() && !loginPasswordField.getText().isBlank()) {
            return true;
        }
        return false;
    }

    /**
     * Button used to take a user to registration page to register for the application.
     * @throws IOException
     */

    @FXML
    protected void onRegistrationButtonClick() throws IOException {
        Stage stage = (Stage) registerPageButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("registration_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Button which checks the validity of the login attempt against the data store in the Sqlite database and then
     * the user to the appropriate view.
     * @throws IOException
     */

    @FXML
    protected void onLoginButtonClick() throws IOException {
        if (checkLogin()) {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("lesson_generator-teacher.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        }
    }

    /**
     * Boolean function that checks whether the user has registered by using the inputted email and password and checking
     * it against the database.
     * @return true if the credentials inputted are valid.
     */
    private boolean checkLogin() {
        String email = loginEmailTextField.getText();
        String password = loginPasswordField.getText();

        return tryLogin(teacherDao, email, password)
                || tryLogin(studentDAO, email, password);
    }

    private boolean tryLogin(UserDAO dao, String email, String password) {
        return dao.checkEmail(email) && dao.checkPassword(email, password);
    }


}

