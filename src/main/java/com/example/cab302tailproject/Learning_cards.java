package com.example.cab302tailproject;

import com.example.cab302tailproject.DAO.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



public class Learning_cards extends Application {

    public static final String TITLE = "TAIL";
    public static final int WIDTH = 818;
    public static final int HEIGHT = 435;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource("learning_cards.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        new DatabaseInitializer().initialize();
        launch();
    }
}