/**
 * Defines the module for the AI Demo application.
 * Specifies dependencies on JavaFX, the Java HTTP Client, and the Ollama4j library.
 */
module ai_demo.aidemo {
    // Required for JavaFX UI components (TextField, Button, Label, TextArea, Layouts etc.)
    requires javafx.controls;
    // Required for loading the FXML file
    requires javafx.fxml;
    // Required for HttpConnectTimeoutException and other HTTP client features
    // (even if used indirectly by ollama4j)
    requires java.net.http;

    // Required for the Ollama4j library.
    // Changed to match the artifactId 'ollama4j' from the user's pom.xml.
    // This assumes the library JAR uses an automatic module name derived from the artifact ID.
    requires ollama4j; // <-- Changed from io.github.ollama4j

    // Required if you were still using SLF4J logging
    // requires org.slf4j;

    // Opens the package containing the controller to JavaFX FXML loader,
    // allowing it to access @FXML annotated fields and methods via reflection.
    opens ai_demo.aidemo to javafx.fxml;

    // Exports the package containing the main Application class,
    // allowing the JavaFX graphics module to instantiate it.
    exports ai_demo.aidemo; // <-- Added this line
}
