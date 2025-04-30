package com.example.cab302tailproject;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder; // Assuming default options are okay

// Removed SLF4J imports

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException; // Import specifically if needed for catch

/**
 * Handles sending a synchronous request (prompt) to the Ollama API
 * and retrieving the generated response.
 * Timeout is 300 seconds
 */
public class OllamaSyncResponse {

    // Define the model to use (make sure this model is available in your Ollama instance)
    static final String OLLAMA_MODEL = "llama3.2:1b"; // Example: Use llama3.2:1b
    // Default host for local Ollama instance
    static final String OLLAMA_HOST = "http://localhost:11434/";
    // Standard HTTP OK status code
    private static final int HTTP_OK = 200;
    // Define the timeout in seconds
    private static final int REQUEST_TIMEOUT_SECONDS = 300; // 5 minutes

    // The user's prompt
    private final String prompt;
    // The Ollama API client instance
    private final OllamaAPI ollamaAPI;


    // Constructor initializes the Ollama API client and stores the prompt.
    public OllamaSyncResponse(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty.");
        }
        this.prompt = prompt;
        // Initialize the API client with the host address.
        this.ollamaAPI = new OllamaAPI(OLLAMA_HOST);
        // Set the request timeout
        this.ollamaAPI.setRequestTimeoutSeconds(REQUEST_TIMEOUT_SECONDS);
        System.out.println("OllamaSyncResponse initialized for host: " + OLLAMA_HOST + " with model: " + OLLAMA_MODEL + ", timeout: " + REQUEST_TIMEOUT_SECONDS + "s");
    }


    // Sends the stored prompt to the configured Ollama model and retrieves the response.

    public String ollamaResponse() throws OllamaBaseException, IOException, InterruptedException {
        System.out.println("Sending prompt to model '" + OLLAMA_MODEL + "'...");

        try {
            // Call the generate API method with the model name, prompt, and default options.
            OllamaResult result = ollamaAPI.generate(OLLAMA_MODEL, prompt, false, new OptionsBuilder().build());

            // Check if the HTTP status code indicates success.
            if (result.getHttpStatusCode() == HTTP_OK) {
                String response = result.getResponse();
                if (response != null && !response.isEmpty()) {
                    System.out.println("Received successful response from Ollama.");
                    return response.trim();
                } else {
                    System.err.println("Warning: Ollama request successful (HTTP 200), but response body was empty.");
                    return "Received an empty response from the AI.";
                }
            } else {
                // Log and handle non-OK HTTP status codes.
                String errorDetails = result.getResponse() != null ? result.getResponse() : "No details in response body.";
                System.err.println("Ollama request failed with HTTP status code: " + result.getHttpStatusCode() + ". Details: " + errorDetails);
                throw new IOException("Ollama request failed with HTTP status: " + result.getHttpStatusCode() + ". Details: " + errorDetails);
            }
            // Catch specific exceptions first if they provide more context
        } catch (HttpConnectTimeoutException e) { // Catch the specific timeout exception
            System.err.println("Connection timed out while trying to reach Ollama server at " + OLLAMA_HOST + " within " + REQUEST_TIMEOUT_SECONDS + " seconds. Ensure the server is running and responsive.");
            throw new IOException("Connection to Ollama server timed out.", e); // Re-throw as IOException or custom exception
        } catch (ConnectException e) { // Catch connection refused specifically
            System.err.println("Connection refused by Ollama server at " + OLLAMA_HOST + ". Ensure the server is running and accessible.");
            throw new IOException("Connection refused by Ollama server.", e); // Re-throw
        } catch (OllamaBaseException | IOException | InterruptedException e) {
            // Handle other communication errors
            System.err.println("Error during Ollama API call: " + e.getMessage());
            throw e; // Re-throw the original exception
        } catch (Exception e) {
            // Catch unexpected runtime exceptions
            System.err.println("An unexpected error occurred during Ollama communication: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for unexpected errors
            throw new RuntimeException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }
}
