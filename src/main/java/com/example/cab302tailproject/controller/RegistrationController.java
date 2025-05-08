package com.example.cab302tailproject.controller;

import com.example.cab302tailproject.DAO.ILoginDAO;
import com.example.cab302tailproject.DAO.SqliteLoginDAO;
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

    /**
     * Getter for the first name of the User.
     * @return The first name inputted by the user from the registration GUI.
     */
    public String getFirstName() {
        return firstNameTextField != null ? firstNameTextField.getText() : null;
    }

    /**
     * Getter for the last name of the user.
     * @return The last name inputted by the user from the registration GUI.
     */

    public String getLastName() {
        return lastNameTextField.getText();
    }
    /**
     * Getter for the email of the user.
     * @return The email inputted by the user from the registration GUI.
     */
    public String getEmail() {
        return emailTextField.getText();
    }
    /**
     * Getter for the password of the user.
     * @return The password inputted by the user from the registration GUI.
     */

    public String getPassword() {
        return registrationPasswordField.getText();
    }
    /**
     * Getter for the confirmation password of the user.
     * @return The confirmed password inputted by the user from the registration GUI.
     */
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

    /**
     * Initializes the registration button on the GUI as disabled.
     */
    @FXML
    public void initialize() {
        registrationButton.setDisable(true); // Start with register button disabled
    }

    private ILoginDAO registerDao;
    public RegistrationController() {
        registerDao = new SqliteLoginDAO();
    }

    /**
     * This is the registration button it uses helper functions to verify the details entered by the user through the
     * GUI and then loads the login page so that the user can login to the application.
     * @throws IOException Ensures that the program doesn't crash upon an unsuccessful registration.
     */
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        if (isRegistrationValid()) {
            createUser();
            addToDatabase();

            Stage stage = (Stage) registrationButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("login_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        }
    }

    /**
     * Depending on whether a user selects the teacher or student radio button loads their registration into the
     * according database. It allows for allocation of privileges.
     */
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

    /**
     * Boolean helper function which combines all validation logic to check whether a users registration is valid before
     * allowing a user to register.
     * @return true if all user inputs are valid.
     */
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

    /**
     * Validation Logic for Name TextFields which can be applied to first and last name text fields.
     * @param nameField which is the text field in which a user inputs their name.
     * @return true if the name entered is matches the Regex of all letters.
     */
    public boolean verifyName(TextField nameField){
        String name = nameField.getText();
        String nameRegex = "^[A-Za-z]+$";
        return name.matches(nameRegex);
    }

    /**
     * Calls the verifyName function to verify the validity of the name entered by the user.
     * @param firstNameTextField is the inputted name of the user registering for the application.
     * @return true if the name is valid.
     */
    private boolean verifyFirstName(TextField firstNameTextField){
        return verifyName(firstNameTextField);
    }
    /**
     * Calls the verifyName function to verify the validity of the name entered by the user.
     * @param lastNameTextField is the inputted name of the user registering for the application.
     * @return true if the name is valid.
     */
    private boolean verifyLastName(TextField lastNameTextField){
        return verifyName(lastNameTextField);
    }

    /**
     * Validation logic that confirms that passwords in both fields are correct and match.
     * @param passwordField is the field in which a user inputs their desired password.
     * @param confirmPasswordField is the field in which a user confirms their desired password.
     * @return true if the password entered by the user matches the regex of at least one uppercase letter, 8+
     * characters and at least one number as well as the same password is entered again in the confirm password field.
     */
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


    /**
     * Handles the validation of the email in the registration form.
     * @param emailTextField is the text field in which a user inputs their email.
     * @return TODO Update the regex for a more stringent criteria
     */
    public boolean verifyEmail(TextField emailTextField) {
        String email = emailTextField.getText();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email.matches(emailRegex);
    }

    /**
     *Used to track that a student or radio button has been selected and that the terms and conditions have been agreed
     * to before registration.
     * @param event is the parameter used to track the clicking of the radio and check boxes before the register button
     *              is re-enabled.
     */

    @FXML
    protected void updateRegisterButtonState(javafx.event.ActionEvent event) {
        boolean accepted = termsAndConditionsButton.isSelected();
        Toggle selectedToggle = userType.getSelectedToggle(); // Get the selected radio button

        // Store the selected user type
        if (selectedToggle != null) {
            String selectedUserType = ((RadioButton) selectedToggle).getText(); // "Student" or "Teacher"
        }

        boolean userTypeSelected = selectedToggle != null;
        registrationButton.setDisable(!(accepted && userTypeSelected)); // Disable button if either condition fails
    }

    /**
     * Depending on which radio button is selected an object of that type is created with the first name, last name,
     * email and password entered by the user, used as the parameters in the constructor of the object instance.
     */

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