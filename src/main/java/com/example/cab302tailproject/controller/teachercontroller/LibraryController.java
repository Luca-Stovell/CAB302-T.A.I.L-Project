package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.LibraryItemDAO;
import com.example.cab302tailproject.DAO.SqliteLibraryItemDAO;
import com.example.cab302tailproject.library.LibraryPaths;
import com.example.cab302tailproject.model.LibraryItem;
import com.example.cab302tailproject.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.cab302tailproject.utils.Alerts.showAlert;
import static com.example.cab302tailproject.utils.SceneHandling.loadScene;
import static com.example.cab302tailproject.utils.TextFormatting.bindTimeToLabel;

public class LibraryController {

    @FXML private Button sidebarGenerateButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarAiAssistanceButton;
    @FXML private Button sidebarLibraryButton;

    @FXML private Button studentsButton;

    @FXML private ListView<Path> fileList;
    private final ObservableList<Path> items = FXCollections.observableArrayList();

    private final LibraryItemDAO dao = new SqliteLibraryItemDAO();
    private final int teacherId = 0;

    /**
     * This Label represents the UI element that displays the currently logged-in teacher's name.
     */
    @FXML private Label loggedInTeacherLabel;

    /**
     * Represents the JavaFX Label used to display the current time.
     */
    @FXML
    private Label timeLabel;

    @FXML
    private void initialize() {
        loggedInTeacherLabel.setText(UserSession.getInstance().getFullName());
        bindTimeToLabel(timeLabel, "hh:mm a");
        fileList.setItems(items);
        refreshList();
        fileList.setOnMouseClicked(e -> { if (e.getClickCount() == 2) openSelected(); });
    }

    @FXML
    private void handleUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select files to upload");
        List<File> chosen = chooser.showOpenMultipleDialog(fileList.getScene().getWindow());
        if (chosen == null) return;
        for (File file : chosen) copyAndRecord(file.toPath());
        refreshList();
    }

    private void copyAndRecord(Path src) {
        try {
            String unique = System.currentTimeMillis() + "_" + src.getFileName();
            Path target = LibraryPaths.UPLOAD_DIR.resolve(unique);
            Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);

            LibraryItem li = new LibraryItem(
                    teacherId, unique, src.getFileName().toString(),
                    Files.size(target), LocalDateTime.now());
            dao.add(li);
        } catch (IOException | java.sql.SQLException ignored) {}
    }

    private void refreshList() {
        try {
            List<LibraryItem> list = dao.findByTeacher(teacherId);
            items.setAll(list.stream()
                    .map(li -> LibraryPaths.UPLOAD_DIR.resolve(li.getStoredName()))
                    .toList());
        } catch (java.sql.SQLException ignored) {}
    }

    private void openSelected() {
        Path sel = fileList.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try { Desktop.getDesktop().open(sel.toFile()); }
            catch (IOException ignored) {}
        }
    }

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

    @FXML private void logoutButtonClicked() throws IOException {
        UserSession.getInstance().logoutUser();
        System.out.println("Log out successful");
        loadScene("login_page.fxml", studentsButton, true);
        showAlert(Alert.AlertType.INFORMATION, "Log Out Successful", "You have been logged out successfully.");
    }
    //</editor-fold>
}
