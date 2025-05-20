package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.LibraryItemDAO;
import com.example.cab302tailproject.DAO.SqliteLibraryItemDAO;
import com.example.cab302tailproject.TailApplication;
import com.example.cab302tailproject.library.LibraryPaths;
import com.example.cab302tailproject.model.LibraryItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

public class LibraryController {

    @FXML private Button sidebarGenerateButton;
    @FXML private Button sidebarReviewButton;
    @FXML private Button sidebarAnalysisButton;
    @FXML private Button sidebarAiAssistanceButton;
    @FXML private Button sidebarLibraryButton;

    @FXML private Button filesButton;
    @FXML private Button studentsButton;
    @FXML private Button homeButton;
    @FXML private Button settingsButton;

    @FXML private ListView<Path> fileList;
    private final ObservableList<Path> items = FXCollections.observableArrayList();

    private final LibraryItemDAO dao = new SqliteLibraryItemDAO();
    private final int teacherId = 0;

    @FXML
    private void initialize() {
        fileList.setItems(items);
        refreshList();
        fileList.setOnMouseClicked(e -> { if (e.getClickCount() == 2) openSelected(); });
    }

    @FXML private void onSidebarGenerateClicked(ActionEvent e) { switchScene(sidebarGenerateButton, "lesson_generator-teacher.fxml"); }
    @FXML private void onSidebarReviewClicked(ActionEvent e)   { switchScene(sidebarReviewButton, "review-teacher.fxml"); }
    @FXML private void onSidebarAnalysisClicked(ActionEvent e) { switchScene(sidebarAnalysisButton, "analytics-teacher.fxml"); }
    @FXML private void onSidebarAiAssistanceClicked(ActionEvent e) { switchScene(sidebarAiAssistanceButton, "ai_assistant-teacher.fxml"); }
    @FXML private void onSidebarLibraryClicked(ActionEvent e)  {}

    @FXML private void onFilesClicked(ActionEvent e)    {}
    @FXML private void onHomeClicked(ActionEvent e)     {}
    @FXML private void onSettingsClicked(ActionEvent e) {}

    @FXML
    private void onStudentsClicked(ActionEvent e) {
        switchScene(studentsButton, "classroom-teacher-view.fxml");
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

    private void switchScene(Node anyNode, String fxmlPath) {
        try {
            Stage stage = (Stage) anyNode.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(TailApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), TailApplication.WIDTH, TailApplication.HEIGHT);
            stage.setScene(scene);
        } catch (IOException ignored) {}
    }
}
