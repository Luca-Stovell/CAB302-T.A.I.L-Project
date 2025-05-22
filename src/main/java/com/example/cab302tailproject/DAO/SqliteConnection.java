package com.example.cab302tailproject.DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {

    private static final String URL = "jdbc:sqlite:Tail.db";

    // Private constructor to prevent instantiation from outside.
    private SqliteConnection() {
        // This constructor is not strictly needed if getInstance directly creates connections.
    }

    /**
     * Gets a new instance of a database connection.
     * This method should be called each time a DAO method needs to perform a database operation,
     * allowing it to be used within a try-with-resources block for proper closing.
     *
     * @return A new Connection object to the SQLite database.
     * @throws SQLException if a database access error occurs or the url is null.
     */
    public static Connection getInstance() throws SQLException {
        // Always return a new connection.
        // The calling DAO method will be responsible for closing it using try-with-resources.
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Failed to create a new SQLite connection: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception so the caller is aware of the failure.
        }
    }
}
