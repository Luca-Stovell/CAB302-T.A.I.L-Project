package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ILoginDAO;
import com.example.cab302tailproject.DAO.SqliteLoginDAO;
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
    @FXML private ListView<Student> studentListView; // Changed to ListView<Student>
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
    /** Data Access Object for interacting with login/user data. */
    private ILoginDAO loginDAO;
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
        loginDAO = new SqliteLoginDAO(); // Initialize DAO

        // Initialize the observable list
        studentObservableList = FXCollections.observableArrayList();
        studentListView.setItems(studentObservableList); // Bind list to ListView

        // Add listener to handle student selection changes
        studentListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayStudentDetails(newValue)
        );

        // Load initial student data
        loadStudentList();

        // Set initial state for detail pane
        studentDetailPane.setVisible(false); // Hide details initially
        resetPasswordButton.setDisable(true); // Disable reset button initially
        instructionLabel.setVisible(true); // Show instruction label

        // TODO: Implement logic to calculate and display class average
        classAverageLabel.setText("Class Average: (Calculation Pending)");

        System.out.println("TeacherAnalyticsController initialized.");
    }

    /**
     * Loads the list of students from the database and populates the ListView.
     * Handles potential errors during data fetching.
     */
    private void loadStudentList() {
        try {
            List<Student> students = loginDAO.getAllStudents();
            studentObservableList.setAll(students); // Update the observable list
            System.out.println("Loaded " + students.size() + " students.");
        } catch (Exception e) {
            System.err.println("Error loading student list: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to load student list.");
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
        selectedStudent = student; // Store the selected student
        if (student != null) {
            firstNameLabel.setText(student.getFirstName());
            lastNameLabel.setText(student.getLastName());
            emailLabel.setText(student.getEmail());
            studentDetailPane.setVisible(true); // Show the details pane
            resetPasswordButton.setDisable(false); // Enable reset button
            instructionLabel.setVisible(false); // Hide instructions
            System.out.println("Displayed details for: " + student.getFullName());
        } else {
            // No student selected, clear details and reset UI state
            firstNameLabel.setText("-");
            lastNameLabel.setText("-");
            emailLabel.setText("-");
            studentDetailPane.setVisible(false); // Hide details pane
            resetPasswordButton.setDisable(true); // Disable reset button
            instructionLabel.setVisible(true); // Show instructions
            System.out.println("Student selection cleared.");
        }
    }

    //<editor-fold desc="Event Handlers - Navigation">
    /**
     * Handles the action for the "Back" button.
     * Navigates the user back to the previous screen (e.g., teacher dashboard).
     * @param event The action event.
     */
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        System.out.println("Back button clicked.");
        switchScene(event, "lesson_generator-teacher.fxml", "TAIL - Generate Content");
    }

    /**
     * Handles the action for the "Home" button in the top navigation.
     * Navigates to the main teacher dashboard.
     * @param event The action event.
     */
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Home button clicked.");
        //TODO implement feature
    }

    /**
     * Handles the action for the "Settings" button in the top navigation.
     * Navigates to the settings view.
     * @param event The action event.
     */
    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Settings button clicked.");
        //TODO Implement feature
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
            showAlert(Alert.AlertType.WARNING, "No Student Selected", "Please select a student from the list first.");
            return;
        }

        // Use TextInputDialog to get the new password securely
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Reset Password");
        passwordDialog.setHeaderText("Reset Password for " + selectedStudent.getFullName());
        passwordDialog.setContentText("Enter new password:");

        Optional<String> result = passwordDialog.showAndWait();

        result.ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Password Reset Failed", "New password cannot be empty.");
                return;
            }
            // TODO: Add password complexity validation here

            // Call DAO to reset the password
            boolean success = loginDAO.resetStudentPassword(selectedStudent.getEmail(), newPassword.trim());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Password Reset", "Password for " + selectedStudent.getFullName() + " has been reset successfully.");
                System.out.println("Password reset successfully for: " + selectedStudent.getEmail());
            } else {
                showAlert(Alert.AlertType.ERROR, "Password Reset Failed", "Could not reset the password for " + selectedStudent.getFullName() + ". Check logs for details.");
                System.err.println("Password reset failed for: " + selectedStudent.getEmail());
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Utility Methods">
    /**
     * Switches the scene on the current stage to the view specified by the FXML file.
     * Uses fixed dimensions (900x600).
     *
     * @param event        The {@link ActionEvent} from which the current stage can be derived.
     * @param fxmlFileName The name of the FXML file to load.
     * @param windowTitle  The title to set for the stage.
     */
    private void switchScene(ActionEvent event, String fxmlFileName, String windowTitle) {
        Stage stage = null;
        try {
            Node sourceNode = (Node) event.getSource();
            if (sourceNode == null || sourceNode.getScene() == null) {
                throw new IllegalStateException("Error switching scene: Could not get scene from event source.");
            }
            stage = (Stage) sourceNode.getScene().getWindow();
            if (stage == null) {
                throw new IllegalStateException("Error switching scene: Could not get stage from event source.");
            }

            URL fxmlUrl = TailApplication.class.getResource(fxmlFileName);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFileName);
            }
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

            Scene scene = new Scene(fxmlLoader.load(), 820, 435); // Using fixed size

            stage.setTitle(windowTitle);
            stage.setScene(scene);
            System.out.println("Successfully switched scene to: " + fxmlFileName);

        } catch (IOException e) {
            System.err.println("IOException loading FXML (" + fxmlFileName + "): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load view: " + fxmlFileName);
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException during scene switch: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not switch view.");
        } catch (Exception e) {
            System.err.println("Unexpected error switching scene to " + fxmlFileName + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred.");
        }
    }

    /**
     * Displays a standard JavaFX Alert dialog. Ensures execution on the FX thread.
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
