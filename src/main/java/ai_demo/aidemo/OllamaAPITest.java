package ai_demo.aidemo;

import io.github.ollama4j.OllamaAPI;
// Import necessary exceptions if they ARE potentially thrown by ping() in your specific library version
// import io.github.ollama4j.exceptions.OllamaBaseException;
// import java.io.IOException;
// import java.lang.InterruptedException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Simple class to check if the Ollama Server is running locally by attempting a ping.
 * Updated to simplify exception handling based on IDE warnings.
 */
public class OllamaAPITest {

    // The default host address for a local Ollama installation.
    private static final String HOST = "http://localhost:11434/";
    // Flag to store the server status after checking.
    private boolean serverRunning = false;

    /**
     * Constructor attempts to ping the Ollama server upon instantiation.
     * It prints the status to the console and sets the internal serverRunning flag.
     * Simplified the catch block based on the assumption that ping() might not
     * throw the specific checked exceptions in the user's environment.
     */
    public OllamaAPITest() {
        // Create an OllamaAPI instance targeting the local server.
        OllamaAPI ollamaAPI = new OllamaAPI(HOST);
        // Set a short timeout for the ping check to avoid long waits if server is down.
        ollamaAPI.setRequestTimeoutSeconds(5); // 5 seconds timeout

        try {
            // Attempt to ping the server.
            // Assuming ollamaAPI.ping() returns boolean and might throw runtime exceptions
            // or specific exceptions like ConnectException/SocketTimeoutException nested within a runtime exception.
            if (ollamaAPI.ping()) {
                System.out.println("Ollama Server Check: The Ollama Server is running at " + HOST);
                this.serverRunning = true;
            } else {
                // This case might occur if ping returns false without an exception.
                System.out.println("Ollama Server Check: The Ollama Server responded to ping, but indicated not ready.");
                this.serverRunning = false;
            }
            // Removed specific catch for OllamaBaseException, IOException, InterruptedException
            // as per the error message indicating they are not thrown by the try block.
            // } catch (OllamaBaseException | IOException | InterruptedException e) { ... }
        } catch (Exception e) { // Catch broader exceptions that might occur
            // Check for common network-related causes nested within the caught exception
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException || e instanceof ConnectException) {
                System.err.println("Ollama Server Check: Connection refused. Is the Ollama server running at " + HOST + "?");
            } else if (cause instanceof SocketTimeoutException || e instanceof SocketTimeoutException) {
                System.err.println("Ollama Server Check: Connection timed out. The server might be down or unresponsive.");
            } else {
                // Log other potential issues during the ping.
                System.err.println("Ollama Server Check: Failed to ping the Ollama Server at " + HOST + ". Error: " + e.getMessage());
                // Optionally print stack trace for debugging unexpected errors
                // e.printStackTrace();
            }
            this.serverRunning = false;
        }
    }

    /**
     * Returns whether the server was detected as running during the last check
     * performed by the constructor.
     *
     * @return true if the server ping was successful, false otherwise.
     */
    public boolean isServerRunning() {
        return this.serverRunning;
    }
}
