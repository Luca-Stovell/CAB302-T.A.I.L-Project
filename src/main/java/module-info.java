module com.example.cab302tailproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    requires java.net.http;
    requires ollama4j;

    opens com.example.cab302tailproject to javafx.fxml;
    exports com.example.cab302tailproject;
    exports com.example.cab302tailproject.controller;
    opens com.example.cab302tailproject.controller to javafx.fxml;
    exports com.example.cab302tailproject.model;
    opens com.example.cab302tailproject.model to javafx.fxml;
    exports com.example.cab302tailproject.ollama4j;
    opens com.example.cab302tailproject.ollama4j to javafx.fxml;
}