package com.example.cab302tailproject.controller.teachercontroller;

// Corrected DAO imports to use StudentDAO and its implementation
import com.example.cab302tailproject.DAO.StudentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
// Other necessary imports
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Student; // Your Student model
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Teacher Analytics view (e.g., analytics-teacher.fxml).
 * This controller is responsible for displaying a list of students,
 * showing details for a selected student, and allowing the teacher
 * to perform actions such as resetting a student's password.
 * It interacts with the {@link StudentDAO} to retrieve and update student data.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.5
 */
public class TeacherAnalyticsController {

    //<editor-fold desc="FXML UI Element References - Navigation">
    /** Button to navigate back to the previous view. */
    @FXML private Button backButton;
    /** Button to navigate to the home/dashboard view. */
    @FXML private Button homeButton;
    /** Button to navigate to the settings view. */
    @FXML private Button settingsButton;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Main Content">
    /** ListView component to display the list of students. */
    @FXML private ListView<Student> studentListView;
    /** Label to display the calculated class average (currently a placeholder). */
    @FXML private Label classAverageLabel;
    /** VBox container used to display the detailed information of the student selected from the ListView. */
    @FXML private VBox studentDetailPane;
    /** Label to display the first name of the selected student. */
    @FXML private Label firstNameLabel;
    /** Label to display the last name of the selected student. */
    @FXML private Label lastNameLabel;
    /** Label to display the email address of the selected student. */
    @FXML private Label emailLabel;
    /** Button that allows the teacher to initiate the password reset process for the selected student. */
    @FXML private Button resetPasswordButton;
    /** Label providing instructions to the user, visible when no student is selected. */
    @FXML private Label instructionLabel;
    //</editor-fold>

    //<editor-fold desc="Other Fields">
    /** Data Access Object for interacting with student-specific data in the database. */
    private StudentDAO studentDao;
    /** An ObservableList that holds Student objects for populating the studentListView. */
    private ObservableList<Student> studentObservableList;
    /** Stores the Student object that is currently selected in the studentListView. */
    private Student selectedStudent = null;
    //</editor-fold>

    /**
     * Initializes the controller class. This method is automatically called
     * by the FXMLLoader after the FXML file has been loaded.
     * It sets up the DAO, populates the student list, configures listeners,
     * and sets the initial state of UI components.
     */
    @FXML
    public void initialize() {
        System.out.println("TeacherAnalyticsController initializing...");
        studentDao = new SqlStudentDAO(); // Use the concrete implementation for StudentDAO

        studentObservableList = FXCollections.observableArrayList();
        if (studentListView != null) {
            studentListView.setItems(studentObservableList);
            studentListView.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> displayStudentDetails(newValue)
            );
        } else {
            System.err.println("ERROR: studentListView is null in TeacherAnalyticsController. Check FXML fx:id.");
            showAlert(Alert.AlertType.ERROR, "UI Error", "Student list component could not be initialized.");
        }

        loadStudentList(); // Populate the student list

        // Set initial visibility and state of UI components
        if (studentDetailPane != null) studentDetailPane.setVisible(false);
        if (resetPasswordButton != null) resetPasswordButton.setDisable(true);
        if (instructionLabel != null) instructionLabel.setVisible(true);
        if (classAverageLabel != null) classAverageLabel.setText("Class Average: (Feature Coming Soon)");

        System.out.println("TeacherAnalyticsController initialized.");
    }

    /**
     * Fetches the list of all students from the database using the studentDao
     * and populates the studentListView.
     */
    private void loadStudentList() {
        if (studentDao == null) {
            System.err.println("ERROR: studentDao is not initialized in loadStudentList (TeacherAnalyticsController).");
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Cannot load student data: data service not available.");
            return;
        }
        try {
            List<com.example.cab302tailproject.model.Student> students = studentDao.getAllStudents();
            if (students != null) {
                studentObservableList.setAll(students);
                System.out.println("Loaded " + students.size() + " students into analytics view.");
            } else {
                System.err.println("Error in TeacherAnalyticsController: getAllStudents returned null.");
                showAlert(Alert.AlertType.ERROR, "Data Retrieval Error", "Failed to retrieve student list (null data returned from source).");
            }
        } catch (Exception e) {
            System.err.println("Error loading student list in TeacherAnalyticsController: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "An unexpected error occurred while loading the student list.");
        }
    }

    /**
     * Updates the UI to display the details of the specified student.
     * If the provided student is null, it resets the detail view.
     *
     * @param student The Student whose details are to be displayed.
     */
    private void displayStudentDetails(Student student) {
        selectedStudent = student;
        if (student != null) {
            if (firstNameLabel != null) firstNameLabel.setText(student.getFirstName());
            if (lastNameLabel != null) lastNameLabel.setText(student.getLastName());
            if (emailLabel != null) emailLabel.setText(student.getEmail());

            if (studentDetailPane != null) studentDetailPane.setVisible(true);
            if (resetPasswordButton != null) resetPasswordButton.setDisable(false);
            if (instructionLabel != null) instructionLabel.setVisible(false);
            System.out.println("Displayed details for student: " + student.getFullName());
        } else {
            if (firstNameLabel != null) firstNameLabel.setText("-");
            if (lastNameLabel != null) lastNameLabel.setText("-");
            if (emailLabel != null) emailLabel.setText("-");

            if (studentDetailPane != null) studentDetailPane.setVisible(false);
            if (resetPasswordButton != null) resetPasswordButton.setDisable(true);
            if (instructionLabel != null) instructionLabel.setVisible(true);
            System.out.println("Student selection cleared in analytics view.");
        }
    }

    //<editor-fold desc="Event Handlers - Navigation">
    /**
     * Handles the action event for the "Back" button.
     * Navigates the user to a previous screen.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        System.out.println("Analytics: Back button clicked.");
        switchScene(event, "lesson_generator-teacher.fxml", "TAIL - Generate Content");
    }

    /**
     * Handles the action event for the "Home" button in the top navigation bar.
     * Navigates the user to the main teacher dashboard.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Analytics: Home button clicked.");
        switchScene(event, "teacher_dashboard_view.fxml", "TAIL - Dashboard");
    }

    /**
     * Handles the action event for the "Settings" button in the top navigation bar.
     * Navigates the user to the application settings view.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Analytics: Settings button clicked.");
        switchScene(event, "teacher_settings_view.fxml", "TAIL - Settings");
    }
    //</editor-fold>

    //<editor-fold desc="Event Handler - Student Actions">
    /**
     * Handles the action event for the "Reset Student Password" button.
     * Prompts for a new password and updates it via the DAO.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void onResetPasswordClicked(ActionEvent event) {
        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "No Student Selected", "Please select a student from the list before attempting to reset their password.");
            return;
        }
        if (studentDao == null) {
            System.err.println("ERROR: studentDao is not initialized in onResetPasswordClicked (TeacherAnalyticsController).");
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Cannot reset password: data service is not available.");
            return;
        }

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Reset Student Password");
        passwordDialog.setHeaderText("Resetting password for: " + selectedStudent.getFullName());
        passwordDialog.setContentText("Please enter the new password:");

        Optional<String> result = passwordDialog.showAndWait();

        result.ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Password Reset Failed", "The new password cannot be empty.");
                return;
            }
            // TODO: Implement more robust password complexity validation here

            boolean success = studentDao.resetStudentPassword(selectedStudent.getEmail(), newPassword.trim());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Password Reset Successful", "The password for " + selectedStudent.getFullName() + " has been successfully reset.");
                System.out.println("Password reset successfully for student: " + selectedStudent.getEmail());
            } else {
                showAlert(Alert.AlertType.ERROR, "Password Reset Failed", "Could not reset the password for " + selectedStudent.getFullName() + ". Please check the application logs for more details.");
                System.err.println("Password reset failed for student: " + selectedStudent.getEmail());
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Utility Methods">
    /**
     * Switches the current scene of the primary stage to the view specified by the FXML file.
     *
     * @param event        The ActionEvent that triggered the navigation.
     * @param fxmlFileName The name of the FXML file to be loaded.
     * @param windowTitle  The title to be set for the window.
     */
    private void switchScene(ActionEvent event, String fxmlFileName, String windowTitle) {
        Stage stage;
        try {
            Node sourceNode = (Node) event.getSource();
            if (sourceNode == null || sourceNode.getScene() == null) {
                throw new IllegalStateException("Error switching scene: Could not get scene from the event source.");
            }
            stage = (Stage) sourceNode.getScene().getWindow();
            if (stage == null) {
                throw new IllegalStateException("Error switching scene: Could not get the stage from the event source's scene.");
            }

            URL fxmlUrl = TailApplication.class.getResource(fxmlFileName);
            if (fxmlUrl == null) {
                if (!fxmlFileName.startsWith("/")) { // Try with a leading slash for absolute classpath lookup
                    fxmlUrl = TailApplication.class.getResource("/" + fxmlFileName);
                }
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find FXML file: " + fxmlFileName + ". Ensure it's in the correct resources path.");
                }
            }
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);

            stage.setTitle(windowTitle);
            stage.setScene(scene);
            System.out.println("Successfully switched scene to: " + fxmlFileName);

        } catch (IOException e) {
            System.err.println("IOException loading FXML (" + fxmlFileName + "): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load view: " + fxmlFileName + "\nReason: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException during scene switch: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not switch view due to an internal state error.");
        } catch (Exception e) {
            System.err.println("Unexpected error switching scene to " + fxmlFileName + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred while trying to navigate.");
        }
    }

    /**
     * Displays a standard JavaFX Alert dialog.
     * @param alertType The type of alert.
     * @param title     The title of the alert window.
     * @param message   The main message content.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    /** Internal helper to show alert (must be called on FX thread). */
    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //</editor-fold>
}
