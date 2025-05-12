module com.example.cab302tailproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires java.net.http;
    requires ollama4j;

    opens com.example.cab302tailproject to javafx.fxml, javafx.graphics;
    exports com.example.cab302tailproject;

    exports com.example.cab302tailproject.controller;
    exports com.example.cab302tailproject.LearningCards;
    opens com.example.cab302tailproject.controller to javafx.fxml;
    opens com.example.cab302tailproject.controller.teachercontroller to javafx.fxml;
    opens com.example.cab302tailproject.controller.studentcontroller to javafx.fxml;

    exports com.example.cab302tailproject.model;
    exports com.example.cab302tailproject.ollama4j;
}