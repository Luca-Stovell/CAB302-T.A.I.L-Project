module com.example.cab302tailproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cab302tailproject to javafx.fxml;
    exports com.example.cab302tailproject;
    exports com.example.cab302tailproject.controller;
    opens com.example.cab302tailproject.controller to javafx.fxml;
}