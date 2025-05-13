package com.example.cab302tailproject;

import com.example.cab302tailproject.DAO.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;            // ← add this
import javafx.scene.Scene;             // ← and this
import javafx.stage.Stage;

public class LibraryTestApp extends Application {

    public static final String TITLE  = "Teacher Library Test";
    public static final int WIDTH     = 400;
    public static final int HEIGHT    = 500;

    @Override
    public void start(Stage stage) throws Exception {
        new DatabaseInitializer().initialize();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass()
                .getResource("library-teacher.fxml")
        );
        Parent root = loader.load();   // now Parent is recognized
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}