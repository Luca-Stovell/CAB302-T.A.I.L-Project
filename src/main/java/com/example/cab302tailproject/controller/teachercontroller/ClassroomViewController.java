package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.*;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Classroom;
import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
    @FXML private Button homeButton;

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

    /**
     * Navigates to the AI assistance view.
     *
     * @param event The action event triggered by the sidebar button.
     * @throws IOException If the FXML cannot be loaded.
     */
    @FXML
    private void onSidebarGenerateClicked(ActionEvent event) throws IOException {
        loadScene("lesson_generator-teacher.fxml");
    }

    /**
     * Navigates to the review view.
     *
     * @param event The action event triggered by the sidebar button.
     * @throws IOException If the FXML cannot be loaded.
     */
    @FXML
    private void onSidebarReviewClicked(ActionEvent event) throws IOException {
        loadScene("review-student.fxml");
    }

    /**
     * Navigates to the analysis view.
     *
     * @param event The action event triggered by the sidebar button.
     * @throws IOException If the FXML cannot be loaded.
     */
    @FXML
    private void onSidebarAnalysisClicked(ActionEvent event) throws IOException {
        loadScene("analytics-teacher.fxml");
    }

    /**
     * Navigates to the AI assistant view.
     *
     * @param event The action event triggered by the sidebar button.
     * @throws IOException If the FXML cannot be loaded.
     */
    @FXML
    private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException {
        loadScene("ai_assistant-teacher.fxml");
    }

    /**
     * Navigates to the library view.
     *
     * @param event The action event triggered by the sidebar button.
     * @throws IOException If the FXML cannot be loaded.
     */
    @FXML
    private void onSidebarLibraryClicked(ActionEvent event) throws IOException {
        loadScene("library-teacher.fxml");
    }

    /**
     * Navigates back to the classroom view.
     *
     * @param event The action event triggered by the sidebar button.
     * @throws IOException If the FXML cannot be loaded.
     */
    @FXML
    private void onStudentsClicked(ActionEvent event) throws IOException {
        loadScene("classroom-teacher-view.fxml");
    }

    /**
     * Stub for the home button click handler.
     *
     * @param event The action event triggered by the home button.
     */
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Home button clicked.");
    }

    /**
     * Stub for the files button click handler.
     *
     * @param event The action event triggered by the files button.
     */
    @FXML
    private void onFilesClicked(ActionEvent event) {
        System.out.println("Files button clicked.");
    }

    /**
     * Stub for the settings button click handler.
     *
     * @param event The action event triggered by the settings button.
     */
    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Settings button clicked.");
    }

    /**
     * Loads and switches to the specified FXML scene.
     *
     * @param fxml The FXML file name to load.
     * @throws IOException If the file cannot be loaded.
     */
    private void loadScene(String fxml) throws IOException {
        Stage stage = (Stage) studentsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(TailApplication.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
}
