package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.*;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.model.Classroom;
import com.example.cab302tailproject.model.Session;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.Teacher;

import java.util.List;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;

public class ClassroomViewController {
    // Sidebar buttons - @FXML fields are optional if only onAction is used.
    /**
     * Button in the sidebar to navigate to the Library view.
     */
    @FXML private Button sidebarLibraryButton;
    /**
     * Button in the sidebar for generating content.
     */
    @FXML private Button sidebarGenerateButton;
    /**
     * Button in the sidebar for reviewing content.
     */
    @FXML private Button sidebarReviewButton;
    /**
     * Button in the sidebar for analysis.
     */
    @FXML private Button sidebarAnalysisButton;
    /**
     * Button in the sidebar for AI assistance.
     */
    @FXML private Button sidebarAiAssistanceButton;
    /**
     * Button for navigating to a "Students" section.
     */
    @FXML private Button studentsButton;
    /**
     * Button for navigating to a "Home" or main dashboard.
     */
    @FXML private Button homeButton;
    /**
     * Listview for displaying the students in the class.
     */
    @FXML private ListView classroomDisplayListview;

    @FXML private Button createClassroomButton;

    @FXML private ComboBox<Classroom> classroomComboBox;

    private TeacherDAO teacherDao;
    private StudentDAO studentDao;
    private ClassroomDAO classroomDao;


    public ClassroomViewController() {
        teacherDao = new SqliteTeacherDAO();
        studentDao = new SqlStudentDAO();
        classroomDao = new SqliteClassroomDAO();
    }

    @FXML
    private void onSidebarGenerateClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarGenerateButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("Student_Ai_Assist.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles action events for the "Review" button in the sidebar.
     * Placeholder for navigation or review functionality.
     * @param event The {@link ActionEvent} triggered by the button click.
     */
    @FXML
    private void onSidebarReviewClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarReviewButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("review-student.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
    /**
     * Handles clicks on the "Analysis" button in the sidebar.
     * Loads the teacher analysis view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarAnalysisClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarAnalysisButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("analytics-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
    /**
     * Handles clicks on the "A.I. Assistance" button in the sidebar.
     * Loads the teacher AI assistance view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarAiAssistanceClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarAiAssistanceButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("ai_assistant-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
    /**
     * Handles clicks on the "Library" button in the sidebar.
     * Loads the teacher library view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onSidebarLibraryClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) sidebarLibraryButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("library-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles clicks on the "Files" button in the top navigation.
     * Loads a files view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onFilesClicked(ActionEvent event) {
        System.out.println("Files button clicked.");
        // TODO ADD FUNCTIONALITY
    }

    /**
     * Handles clicks on the "Students" button in the top navigation.
     * Loads a students view on the current stage.
     * @param event The action event.
     */
    @FXML
    private void onStudentsClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) studentsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("classroom-teacher-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
        stage.setScene(scene);
    }
    @FXML
    private void onSettingsClicked(ActionEvent event) {
        System.out.println("Settings button clicked.");
        // TODO ADD FUNCTIONALITY
    }
    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Home button clicked.");
        // TODO ADD FUNCTIONALITY
    }
    @FXML
    public void loadStudentData() {
        List<Student> students = studentDao.getAllStudents();

        // Populate ListView with full names
        for (Student s : students) {
            String fullName = s.getFirstName() + " " + s.getLastName();
            classroomDisplayListview.getItems().add(fullName);
        }
    }

    @FXML
    public void initialize() {
        refreshClassroomComboBox();
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

    private void refreshClassroomComboBox() {
        String teacherEmail = Session.getLoggedInTeacherEmail();
        List<Classroom> classrooms = classroomDao.getClassroomsByTeacherEmail(teacherEmail);
        classroomComboBox.getItems().setAll(classrooms);
    }

}
