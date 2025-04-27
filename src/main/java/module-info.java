module ai_demo.aidemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires ollama4j;
    opens ai_demo.aidemo to javafx.fxml;
    exports ai_demo.aidemo;
}
