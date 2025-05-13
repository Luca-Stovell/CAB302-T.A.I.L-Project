package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.*;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Classroom;
import com.example.cab302tailproject.model.Session;
import com.example.cab302tailproject.model.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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

    public ClassroomViewController() {
        teacherDao = new SqliteTeacherDAO();
        studentDao = new SqlStudentDAO();
        classroomDao = new SqliteClassroomDAO();
    }

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

    private void refreshClassroomComboBox() {
        String teacherEmail = Session.getLoggedInTeacherEmail();
        List<Classroom> classrooms = classroomDao.getClassroomsByTeacherEmail(teacherEmail);
        classroomComboBox.getItems().setAll(classrooms);
    }

    private void loadAllStudents() {
        allStudentsListView.getItems().setAll(studentDao.getAllStudents());
    }

    private void loadStudentsInClass(int classroomID) {
        classroomDisplayListview.getItems().clear();
        List<Student> students = studentDao.getStudentsByClassroomID(classroomID);
        classroomDisplayListview.getItems().setAll(students);
    }

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


    @FXML
    public void onCreateClassRoom(ActionEvent event) {
        String teacherEmail = Session.getLoggedInTeacherEmail();
        Classroom classroom = new Classroom(teacherEmail);
        boolean created = classroomDao.createClassroom(classroom);

        if (created) {
            refreshClassroomComboBox();
            classroomComboBox.setValue(classroom);
        }
    }

    // Navigation methods
    @FXML
    private void onSidebarGenerateClicked(ActionEvent event) throws IOException {
        loadScene("Student_Ai_Assist.fxml");
    }

    @FXML
    private void onSidebarReviewClicked(ActionEvent event) throws IOException {
        loadScene("review-student.fxml");
    }

    @FXML
    private void onSidebarAnalysisClicked(ActionEvent event) throws IOException {
        loadScene("analytics-teacher.fxml");
    }

    @FXML
    private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException {
        loadScene("ai_assistant-teacher.fxml");
    }

    @FXML
    private void onSidebarLibraryClicked(ActionEvent event) throws IOException {
        loadScene("library-teacher.fxml");
    }

    @FXML
    private void onStudentsClicked(ActionEvent event) throws IOException {
        loadScene("classroom-teacher-view.fxml");
    }

    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Home button clicked.");
    }

    @FXML
    private void onFilesClicked(ActionEvent event) {
        System.out.println("Files button clicked.");
    }

    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Settings button clicked.");
    }

    private void loadScene(String fxml) throws IOException {
        Stage stage = (Stage) studentsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(TailApplication.class.getResource(fxml));
        Scene scene = new Scene(loader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
}
