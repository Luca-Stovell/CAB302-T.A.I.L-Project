package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.ContentDAO; // Assuming ContentDAO will have new methods
import com.example.cab302tailproject.DAO.StudentDAO;
import com.example.cab302tailproject.DAO.SqlStudentDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.UserSession; // For getting current teacher if needed for classroom context

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the Teacher Analytics view (analytics-teacher.fxml).
 * Displays a list of students and allows the teacher to view details,
 * reset passwords, and view learning card performance for selected students.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.6
 */
public class TeacherAnalyticsController {

    //<editor-fold desc="FXML UI Element References - Navigation">
    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button settingsButton;
    //</editor-fold>

    //<editor-fold desc="FXML UI Element References - Main Content">
    @FXML private ListView<Student> studentListView;
    @FXML private Label classAverageLabel;
    @FXML private VBox studentDetailPane;
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label emailLabel;
    @FXML private Button resetPasswordButton;
    @FXML private Label instructionLabel;

    // New FXML Fields for Learning Card Performance
    @FXML private ComboBox<Integer> weekSelectionComboBox;
    @FXML private TableView<CardPerformanceEntry> cardPerformanceTableView;
    @FXML private TableColumn<CardPerformanceEntry, String> cardQuestionColumn;
    @FXML private TableColumn<CardPerformanceEntry, String> resultColumn;
    //</editor-fold>

    //<editor-fold desc="Other Fields">
    private StudentDAO studentDao;
    private ContentDAO contentDao; // For fetching card responses and material weeks
    private ObservableList<Student> studentObservableList;
    private Student selectedStudent = null;
    private ObservableList<CardPerformanceEntry> cardPerformanceData = FXCollections.observableArrayList();
    //</editor-fold>

    /**
     * Simple inner class to represent a row in the card performance table.
     */
    public static class CardPerformanceEntry {
        private final SimpleStringProperty cardQuestion;
        private final SimpleStringProperty result;

        public CardPerformanceEntry(String cardQuestion, String result) {
            this.cardQuestion = new SimpleStringProperty(cardQuestion);
            this.result = new SimpleStringProperty(result);
        }

        public String getCardQuestion() { return cardQuestion.get(); }
        public void setCardQuestion(String cardQuestion) { this.cardQuestion.set(cardQuestion); }
        public SimpleStringProperty cardQuestionProperty() { return cardQuestion; }

        public String getResult() { return result.get(); }
        public void setResult(String result) { this.result.set(result); }
        public SimpleStringProperty resultProperty() { return result; }
    }


    @FXML
    public void initialize() {
        System.out.println("TeacherAnalyticsController initializing...");
        studentDao = new SqlStudentDAO();
        contentDao = new ContentDAO(); // Initialize ContentDAO

        studentObservableList = FXCollections.observableArrayList();
        if (studentListView != null) {
            studentListView.setItems(studentObservableList);
            studentListView.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> displayStudentDetails(newValue)
            );
        } else {
            System.err.println("ERROR: studentListView is null in TeacherAnalyticsController.");
            showAlert(Alert.AlertType.ERROR, "UI Error", "Student list component could not be initialized.");
        }

        loadStudentList();

        if (studentDetailPane != null) studentDetailPane.setVisible(false);
        if (resetPasswordButton != null) resetPasswordButton.setDisable(true);
        if (instructionLabel != null) instructionLabel.setVisible(true);
        if (classAverageLabel != null) classAverageLabel.setText("Class Average: (Feature Coming Soon)");

        // Initialize TableView columns for card performance
        if (cardQuestionColumn != null) cardQuestionColumn.setCellValueFactory(new PropertyValueFactory<>("cardQuestion"));
        if (resultColumn != null) resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        if (cardPerformanceTableView != null) cardPerformanceTableView.setItems(cardPerformanceData);

        // Add listener to week selection ComboBox
        if (weekSelectionComboBox != null) {
            weekSelectionComboBox.setOnAction(event -> loadCardPerformanceForSelectedWeek());
        }

        System.out.println("TeacherAnalyticsController initialized.");
    }

    private void loadStudentList() {
        // ... (existing code for loading students)
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

    private void displayStudentDetails(Student student) {
        selectedStudent = student;
        cardPerformanceData.clear(); // Clear previous performance data
        weekSelectionComboBox.getItems().clear(); // Clear previous weeks
        weekSelectionComboBox.setValue(null); // Reset selection

        if (student != null) {
            if (firstNameLabel != null) firstNameLabel.setText(student.getFirstName());
            if (lastNameLabel != null) lastNameLabel.setText(student.getLastName());
            if (emailLabel != null) emailLabel.setText(student.getEmail());

            if (studentDetailPane != null) studentDetailPane.setVisible(true);
            if (resetPasswordButton != null) resetPasswordButton.setDisable(false);
            if (instructionLabel != null) instructionLabel.setVisible(false);
            System.out.println("Displayed details for student: " + student.getFullName());

            populateWeekComboBoxForStudent(student);

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

    private void populateWeekComboBoxForStudent(Student student) {
        if (student == null || contentDao == null) return;

        // First, get the classroom ID for the student.
        // This might need adjustment if a student can be in multiple classrooms
        // or if the classroom context is determined differently.
        int classroomId = studentDao.getClassroomIDForStudent(student.getStudentID());
        if (classroomId == -1) {
            System.err.println("Could not determine classroom for student: " + student.getEmail());
            weekSelectionComboBox.setDisable(true);
            weekSelectionComboBox.setPromptText("No classroom found");
            return;
        }
        weekSelectionComboBox.setDisable(false);
        weekSelectionComboBox.setPromptText("Choose Week");

        try {
            List<Integer> weeks = contentDao.getDistinctWeeksForClassroomLearningCards(classroomId);
            if (weeks != null && !weeks.isEmpty()) {
                weekSelectionComboBox.setItems(FXCollections.observableArrayList(weeks));
            } else {
                weekSelectionComboBox.setPromptText("No learning card weeks");
            }
        } catch (Exception e) {
            System.err.println("Error populating week ComboBox: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "Could not load available weeks.");
        }
    }

    private void loadCardPerformanceForSelectedWeek() {
        cardPerformanceData.clear();
        if (selectedStudent == null || weekSelectionComboBox.getValue() == null || contentDao == null) {
            return;
        }

        int studentId = selectedStudent.getStudentID();
        int selectedWeek = weekSelectionComboBox.getValue();
        int classroomId = studentDao.getClassroomIDForStudent(studentId); // Get classroom ID again or store it

        if (classroomId == -1) {
            System.err.println("Cannot load performance, classroom ID not found for student: " + selectedStudent.getEmail());
            return;
        }

        try {
            // This new DAO method will fetch StudentCardResponse joined with Material (for week)
            List<com.example.cab302tailproject.model.StudentCardResponse> responses =
                    contentDao.getStudentCardResponsesForWeek(studentId, classroomId, selectedWeek);

            if (responses != null) {
                for (com.example.cab302tailproject.model.StudentCardResponse response : responses) {
                    cardPerformanceData.add(new CardPerformanceEntry(
                            response.getCardQuestion(),
                            response.isCorrect() ? "Correct" : "Incorrect"
                    ));
                }
                System.out.println("Loaded " + responses.size() + " card responses for student " + studentId + ", week " + selectedWeek);
            }
            if (cardPerformanceTableView.getItems().isEmpty()) {
                cardPerformanceTableView.setPlaceholder(new Label("No responses found for this student for week " + selectedWeek + "."));
            }
        } catch (Exception e) {
            System.err.println("Error loading card performance data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error", "Could not load card performance data.");
        }
    }


    //<editor-fold desc="Event Handlers - Navigation">
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        System.out.println("Analytics: Back button clicked.");
        switchScene(event, "lesson_generator-teacher.fxml", "TAIL - Generate Content");
    }

    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Analytics: Home button clicked.");
        // Assuming teacher_dashboard_view.fxml is the correct home for teachers
        switchScene(event, "teacher_dashboard_view.fxml", "TAIL - Dashboard");
    }

    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Analytics: Settings button clicked.");
        // Assuming teacher_settings_view.fxml is the correct settings view
        switchScene(event, "teacher_settings_view.fxml", "TAIL - Settings");
    }
    //</editor-fold>

    //<editor-fold desc="Event Handler - Student Actions">
    @FXML
    private void onResetPasswordClicked(ActionEvent event) {
        // ... (existing code for resetting password)
        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "No Student Selected", "Please select a student from the list first to reset their password.");
            return;
        }
        if (studentDao == null) {
            System.err.println("ERROR: studentDao is not initialized in onResetPasswordClicked (TeacherAnalyticsController).");
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Cannot reset password: data service not available.");
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
    private void switchScene(ActionEvent event, String fxmlFileName, String windowTitle) {
        // ... (existing code for switching scenes)
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
                if (!fxmlFileName.startsWith("/")) {
                    fxmlUrl = TailApplication.class.getResource("/" + fxmlFileName);
                }
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find FXML file: " + fxmlFileName);
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the requested view: " + fxmlFileName + "\nReason: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException during scene switch: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not switch the view due to an internal state error.");
        } catch (Exception e) {
            System.err.println("Unexpected error switching scene to " + fxmlFileName + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred while trying to navigate to the requested view.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // ... (existing code for showing alerts)
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> displayAlertInternal(alertType, title, message));
        } else {
            displayAlertInternal(alertType, title, message);
        }
    }

    private void displayAlertInternal(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //</editor-fold>
}
