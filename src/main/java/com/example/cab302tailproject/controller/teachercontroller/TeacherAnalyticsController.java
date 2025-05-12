package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.StudentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Student;
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
 * Controller for the Teacher Analytics view (analytics-teacher.fxml).
 * Displays a list of students and allows the teacher to view details
 * and reset passwords for selected students. Also shows placeholder class average.
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
    /** ListView to display the names of students. */
    @FXML private ListView<Student> studentListView;
    /** Label to display the class average (placeholder). */
    @FXML private Label classAverageLabel;
    /** VBox container for displaying selected student's details. */
    @FXML private VBox studentDetailPane;
    /** Label to display the selected student's first name. */
    @FXML private Label firstNameLabel;
    /** Label to display the selected student's last name. */
    @FXML private Label lastNameLabel;
    /** Label to display the selected student's email. */
    @FXML private Label emailLabel;
    /** Button to initiate the password reset process for the selected student. */
    @FXML private Button resetPasswordButton;
    /** Label providing instructions to the user. */
    @FXML private Label instructionLabel;
    //</editor-fold>

    //<editor-fold desc="Other Fields">
    /** Data Access Object for interacting with student data. */
    private StudentDAO studentDao;
    /** Observable list to hold student data for the ListView. */
    private ObservableList<Student> studentObservableList;
    /** Holds the currently selected student from the list. */
    private Student selectedStudent = null;
    //</editor-fold>

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It initializes the DAO, loads student data,
     * sets up the ListView listener, and configures initial UI states.
     */
    @FXML
    public void initialize() {
        System.out.println("TeacherAnalyticsController initializing...");
        studentDao = new SqlStudentDAO();

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

        loadStudentList();

        if (studentDetailPane != null) studentDetailPane.setVisible(false);
        if (resetPasswordButton != null) resetPasswordButton.setDisable(true);
        if (instructionLabel != null) instructionLabel.setVisible(true);
        if (classAverageLabel != null) classAverageLabel.setText("Class Average: (Feature Coming Soon)");

        System.out.println("TeacherAnalyticsController initialized.");
    }

    /**
     * Loads the list of students from the database and populates the ListView.
     * Handles potential errors during data fetching.
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
     * Displays the details of the selected student in the central pane.
     * Updates labels and enables the reset password button. Hides the instruction label.
     * If no student is selected (newValue is null), it hides the detail pane and shows instructions.
     *
     * @param student The {@link Student} object selected in the ListView, or null if selection is cleared.
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
            // Using getFullName() from the Student model
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
     * Handles the action for the "Back" button.
     * Navigates the user back to the previous screen (e.g., teacher dashboard or lesson generator).
     * @param event The action event.
     */
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        System.out.println("Analytics: Back button clicked.");
        switchScene(event, "lesson_generator-teacher.fxml", "TAIL - Generate Content");
    }

    /**
     * Handles the action for the "Home" button in the top navigation bar.
     * Navigates the user to the main teacher dashboard.
     * @param event The {@link ActionEvent} triggered by the button click.
     */
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Analytics: Home button clicked.");
        switchScene(event, "teacher_dashboard_view.fxml", "TAIL - Dashboard");
    }

    /**
     * Handles the action for the "Settings" button in the top navigation bar.
     * Navigates the user to the application settings view.
     * @param event The {@link ActionEvent} triggered by the button click.
     */
    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Analytics: Settings button clicked.");
        switchScene(event, "teacher_settings_view.fxml", "TAIL - Settings");
    }
    //</editor-fold>

    //<editor-fold desc="Event Handler - Student Actions">
    /**
     * Handles the action for the "Reset Student Password" button.
     * Prompts the teacher for a new password and updates the selected student's
     * password in the database via the DAO.
     * @param event The action event.
     */
    @FXML
    private void onResetPasswordClicked(ActionEvent event) {
        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "No Student Selected", "Please select a student from the list first to reset their password.");
            return;
        }
        if (studentDao == null) {
            System.err.println("ERROR: studentDao is not initialized in onResetPasswordClicked (TeacherAnalyticsController).");
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Cannot reset password: data service is not available.");
            return;
        }

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Reset Student Password");
        // Using getFullName() from the Student model
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
     * @param event        The {@link ActionEvent} that triggered the navigation, used to find the current stage.
     * @param fxmlFileName The name of the FXML file to be loaded (e.g., "my_view.fxml").
     * This path is resolved relative to the location of the {@link TailApplication} class.
     * @param windowTitle  The title to be set for the window after the scene switch.
     */
    private void switchScene(ActionEvent event, String fxmlFileName, String windowTitle) {
        Stage stage;
        try {
            Node sourceNode = (Node) event.getSource();
            if (sourceNode == null || sourceNode.getScene() == null) {
                throw new IllegalStateException("Error switching scene: Could not get scene from the event source. Ensure the event source is part of a scene.");
            }
            stage = (Stage) sourceNode.getScene().getWindow();
            if (stage == null) {
                throw new IllegalStateException("Error switching scene: Could not get the stage from the event source's scene.");
            }

            URL fxmlUrl = TailApplication.class.getResource(fxmlFileName);
            if (fxmlUrl == null) {
                // Attempt with a leading slash if it's an absolute path from the classpath root
                if (!fxmlFileName.startsWith("/")) {
                    fxmlUrl = TailApplication.class.getResource("/" + fxmlFileName);
                }
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find FXML file: " + fxmlFileName + ". Ensure the file is in the correct resources path (e.g., same package as TailApplication or specify full path like '/com/example/yourpackage/view.fxml').");
                }
            }
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

            // Use dimensions from TailApplication class or define them as constants in this controller
            Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);

            stage.setTitle(windowTitle);
            stage.setScene(scene);
            System.out.println("Successfully switched scene to: " + fxmlFileName);

        } catch (IOException e) {
            System.err.println("IOException loading FXML (" + fxmlFileName + "): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested view: " + fxmlFileName + "\nReason: " + e.getMessage() + "\nPlease check if the FXML file exists and its controller is correctly defined.");
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException during scene switch: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not switch the view due to an internal state error.");
        } catch (Exception e) { // Catch-all for other unexpected runtime errors during FXML loading or scene setup
            System.err.println("Unexpected error switching scene to " + fxmlFileName + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred while trying to navigate to the requested view.");
        }
    }

    /**
     * Displays a standard JavaFX Alert dialog to the user.
     * This method ensures that the alert is shown on the JavaFX Application Thread.
     *
     * @param alertType The type of alert to display (e.g., {@link Alert.AlertType#INFORMATION}, {@link Alert.AlertType#ERROR}).
     * @param title     The title for the alert dialog window.
     * @param message   The main content message to be displayed in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    /**
     * Internal helper method to create and display an alert.
     * This method should only be called from the JavaFX Application Thread.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert.
     * @param message   The content message of the alert.
     */
    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //</editor-fold>
}
