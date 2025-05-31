package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.*;
import com.example.cab302tailproject.model.Classroom;
import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;
import static com.example.cab302tailproject.utils.TextFormatting.bindTimeToLabel;

/**
 * Controller for the teacher's classroom view.
 * Handles classroom creation, student assignment, and scene navigation for teachers.
 */
public class ClassroomViewController {

    // Sidebar navigation buttons
    @FXML private Button sidebarLibraryButton;
    @FXML private Button sidebarGenerateButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarAiAssistanceButton;
    @FXML private Button studentsButton;

    // Classroom controls
    @FXML private ComboBox<Classroom> classroomComboBox;
    @FXML private Button createClassroomButton;

    // Student display + actions
    @FXML private ListView<Student> allStudentsListView;
    @FXML private ListView<Student> classroomDisplayListview;
    @FXML private Button assignStudentButton;
    @FXML private Button removeStudentButton;

    private TeacherDAO teacherDao;
    private StudentDAO studentDao;
    private ClassroomDAO classroomDao;

    // --- FXML UI Element references for dynamic labels ---
    @FXML private Label loggedInTeacherLabel;
    @FXML private Label timeLabel;

    /**
     * Constructor for ClassroomViewController.
     * Initializes DAO instances for Teacher, Student, and Classroom.
     */
    public ClassroomViewController() {
        teacherDao = new SqliteTeacherDAO();
        studentDao = new SqlStudentDAO();
        classroomDao = new SqliteClassroomDAO();
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Loads classroom options and all students into their respective views.
     */
    @FXML
    public void initialize() {
        refreshClassroomComboBox();
        loadAllStudents();

        classroomComboBox.setOnAction(event -> {
            Classroom selectedClassroom = classroomComboBox.getValue();
            if (selectedClassroom != null) {
                loadStudentsInClass(selectedClassroom.getClassroomID());
            }
        });
        loggedInTeacherLabel.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");
    }

    /**
     * Refreshes the classroom combo box with the classrooms linked to the logged-in teacher.
     */
    private void refreshClassroomComboBox() {
        String teacherEmail = UserSession.getLoggedInTeacherEmail();
        List<Classroom> classrooms = classroomDao.getClassroomsByTeacherEmail(teacherEmail);
        classroomComboBox.getItems().setAll(classrooms);

    }

    /**
     * Loads all students into the "all students" ListView.
     */
    private void loadAllStudents() {
        allStudentsListView.getItems().setAll(studentDao.getAllStudents());
    }

    /**
     * Loads students enrolled in a specific classroom into the classroom ListView.
     *
     * @param classroomID The ID of the classroom.
     */
    private void loadStudentsInClass(int classroomID) {
        classroomDisplayListview.getItems().clear();
        List<Student> students = studentDao.getStudentsByClassroomID(classroomID);
        classroomDisplayListview.getItems().setAll(students);
    }

    /**
     * Assigns the selected student from the "all students" list to the selected classroom.
     *
     * @param event The action event triggered by clicking the assign button.
     */
    @FXML
    public void onAssignStudentToClassroom(ActionEvent event) {
        Student selectedStudent = allStudentsListView.getSelectionModel().getSelectedItem();
        Classroom selectedClassroom = classroomComboBox.getValue();

        if (selectedStudent != null && selectedClassroom != null) {
            boolean success = studentDao.addStudentToClassroom(
                    selectedStudent.getStudentID(),
                    selectedClassroom.getClassroomID()
            );

            if (success) {
                loadStudentsInClass(selectedClassroom.getClassroomID());
            }
        }
    }

    /**
     * Removes the selected student from the selected classroom.
     *
     * @param event The action event triggered by clicking the remove button.
     */
    @FXML
    public void onRemoveStudentFromClassroom(ActionEvent event) {
        Student selectedStudent = classroomDisplayListview.getSelectionModel().getSelectedItem();
        Classroom selectedClassroom = classroomComboBox.getValue();

        if (selectedStudent != null && selectedClassroom != null) {
            boolean success = studentDao.removeStudentFromClassroom(
                    selectedStudent.getStudentID(),
                    selectedClassroom.getClassroomID()
            );

            if (success) {
                loadStudentsInClass(selectedClassroom.getClassroomID());
                loadAllStudents();
            }
        }
    }

    /**
     * Creates a new classroom linked to the logged-in teacher and updates the combo box.
     *
     * @param event The action event triggered by clicking the create button.
     */
    @FXML
    public void onCreateClassRoom(ActionEvent event) {
        String teacherEmail = UserSession.getLoggedInTeacherEmail();
        Classroom classroom = new Classroom(teacherEmail);
        boolean created = classroomDao.createClassroom(classroom);

        if (created) {
            refreshClassroomComboBox();
            classroomComboBox.setValue(classroom);
        }
    }

    // --- Navigation Handlers ---

    //<editor-fold desc="Navigation - Direct Scene Switching">
    /**
     * Handles clicks on the "Generate" button in the sidebar.
     * Reloads the lesson generation view on the current stage.
     */
    @FXML
    private void onSidebarGenerateClicked() throws IOException {
        loadScene("lesson_generator-teacher.fxml", sidebarGenerateButton, false);
    }

    /**
     * Handles clicks on the "Review" button in the sidebar.
     * Loads the teacher review view on the current stage.
     */
    @FXML
    private void onSidebarReviewClicked() throws IOException {
        loadScene("review-teacher.fxml", sidebarReviewButton, false);
    }

    /**
     * Handles clicks on the "Analysis" button in the sidebar.
     * Loads the teacher analysis view on the current stage.
     */
    @FXML
    private void onSidebarAnalysisClicked() throws IOException {
        loadScene("analytics-teacher.fxml", sidebarAnalysisButton, true);
    }

    /**
     * Handles clicks on the "A.I. Assistance" button in the sidebar.
     * Loads the teacher AI assistance view on the current stage.
     */
    @FXML
    private void onSidebarAiAssistanceClicked() throws IOException {
        loadScene("ai_assistant-teacher.fxml", sidebarAiAssistanceButton, false);
    }

    /**
     * Handles clicks on the "Library" button in the sidebar.
     * Loads the teacher library view on the current stage.
     */
    @FXML
    private void onSidebarLibraryClicked() throws IOException {
        loadScene("library-teacher.fxml", sidebarLibraryButton, true);
    }

    /**
     * Handles clicks on the "Students" button in the top navigation.
     * Loads a students view on the current stage.
     */
    @FXML
    private void onStudentsClicked() throws IOException {
        loadScene("classroom-teacher-view.fxml", studentsButton, true);
    }

    /**
     * Handles actions performed when the logout button is clicked.
     * This method logs out the currently logged-in user, switches the view
     * to the login page, and displays a confirmation message.
     */
    @FXML private void logoutButtonClicked(ActionEvent actionEvent) throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", studentsButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
    //</editor-fold>
}
