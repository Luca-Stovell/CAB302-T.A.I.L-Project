package com.example.cab302tailproject.controller.teachercontroller;

import com.example.cab302tailproject.DAO.LibraryItemDAO;
import com.example.cab302tailproject.DAO.SqliteLibraryItemDAO;
import com.example.cab302tailproject.library.LibraryPaths;
import com.example.cab302tailproject.model.LibraryItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

public class LibraryController {

    @FXML
    private ListView<Path> fileList;
    private final ObservableList<Path> items = FXCollections.observableArrayList();


    private final LibraryItemDAO dao = new SqliteLibraryItemDAO();
    private final int teacherId = 0;


    @FXML
    private void initialize() {
        fileList.setItems(items);
        refreshList();

        fileList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) openSelected();
        });
    }


    @FXML
    private void handleUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择上传文件");
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

        } catch (IOException | java.sql.SQLException ex) {
            ex.printStackTrace(); // TODO 弹窗提示
        }
    }


    private void refreshList() {
        try {
            List<LibraryItem> list = dao.findByTeacher(teacherId);
            items.setAll(list.stream()
                    .map(li -> LibraryPaths.UPLOAD_DIR.resolve(li.getStoredName()))
                    .toList());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void openSelected() {
        Path sel = fileList.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                Desktop.getDesktop().open(sel.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onHomeClicked(ActionEvent event) {
        System.out.println("Home button clicked.");
        // TODO ADD FUNCTIONALITY
    }
}
