// Application to directly launch LessonGen

package com.example.cab302tailproject;

import com.example.cab302tailproject.DAO.DatabaseInitializer;
import com.example.cab302tailproject.model.Session;
import com.example.cab302tailproject.model.UserDetail;
import com.example.cab302tailproject.model.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TeacherGenerateApplication extends Application {
    public static final String TITLE = "Worksheet and Lesson Plan Generator";
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TailApplication.class.getResource(
                "lesson_generator-teacher.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();

        // Set user
        Session.setLoggedInTeacherEmail("mrs_liat@tail.com");
        UserSession.getInstance().loginUser("Mrs", "Liat", "mrs_liat@tail.com", "Teacher");
    }

    public static void main(String[] args) {
        new DatabaseInitializer().initialize();
        launch();
    }
}
