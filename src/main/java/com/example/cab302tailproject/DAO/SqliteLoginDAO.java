package com.example.cab302tailproject.DAO;

import java.sql.*;
import java.security.MessageDigest; // For hashing
import java.security.NoSuchAlgorithmException; // For hashing
import java.util.Base64; // For encoding hash

/**
 * SQLite implementation of the ILoginDAO interface.
 * Handles database operations for Student and Teacher tables related to login and registration.
 */
public class SqliteLoginDAO implements ILoginDAO {
    private Connection connection;

    /**
     * Constructor initializes the database connection and ensures tables exist.
     */
    public SqliteLoginDAO() {
        connection = SqliteConnection.getInstance();
        // Ensure tables are created if they don't exist
        createTeacherTable();
        createStudentTable();
    }

    /**
     * Creates the Teacher table if it doesn't already exist.
     */
    private void createTeacherTable() {
        // Using TEXT for password to store the hash
        String query =
                "CREATE TABLE IF NOT EXISTS Teacher ("
                        + "TeacherID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "email TEXT UNIQUE NOT NULL, "
                        + "firstName TEXT NOT NULL, "
                        + "lastName TEXT NOT NULL, "
                        + "password TEXT NOT NULL" // Stores hashed password
                        + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating Teacher table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the Student table if it doesn't already exist.
     */
    private void createStudentTable() {
        // Using TEXT for password to store the hash
        String query =
                "CREATE TABLE IF NOT EXISTS Student ("
                        + "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "email TEXT UNIQUE NOT NULL, "
                        + "firstName TEXT NOT NULL, "
                        + "lastName TEXT NOT NULL, "
                        + "password TEXT NOT NULL, " // Stores hashed password
                        + "teacherID INTEGER, " // Optional: Link student to a teacher
                        + "FOREIGN KEY (teacherID) REFERENCES Teacher(TeacherID)"
                        + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating Student table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if an email exists in the specified table.
     * @param email The email to check.
     * @param tableName The name of the table ("Student" or "Teacher").
     * @return true if the email exists in the table, false otherwise.
     */
    private boolean checkEmailExistsInTable(String email, String tableName) {
        // Basic validation to prevent SQL injection via tableName, although it's internal use here.
        if (!tableName.equals("Student") && !tableName.equals("Teacher")) {
            System.err.println("Invalid table name provided to checkEmailExistsInTable: " + tableName);
            return false;
        }
        String query = "SELECT COUNT(1) FROM " + tableName + " WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1; // Check if count is 1
            }
        } catch (SQLException e) {
            System.err.println("Error checking email in " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkStudentEmailExists(String email) {
        return checkEmailExistsInTable(email, "Student");
    }

    @Override
    public boolean checkTeacherEmailExists(String email) {
        return checkEmailExistsInTable(email, "Teacher");
    }

    /**
     * Retrieves the stored hashed password for a given email from the specified table.
     * @param email The email of the user.
     * @param tableName The table to query ("Student" or "Teacher").
     * @return The stored hashed password, or null if the email is not found or an error occurs.
     */
    private String getPasswordHash(String email, String tableName) {
        if (!tableName.equals("Student") && !tableName.equals("Teacher")) {
            System.err.println("Invalid table name provided to getPasswordHash: " + tableName);
            return null;
        }
        String query = "SELECT password FROM " + tableName + " WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving password hash from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if email not found or on error
    }

    /**
     * Checks if the provided password matches the stored hash for the given email in the specified table.
     * @param email The user's email.
     * @param plainPassword The plain text password entered by the user.
     * @param tableName The table to check against ("Student" or "Teacher").
     * @return true if the password matches, false otherwise.
     */
    private boolean checkLoginCredentials(String email, String plainPassword, String tableName) {
        String storedHash = getPasswordHash(email, tableName);
        if (storedHash == null) {
            return false; // Email not found in the specified table
        }
        String enteredHash = hashPassword(plainPassword); // Hash the entered password
        return storedHash.equals(enteredHash); // Compare hashes
    }

    @Override
    public boolean checkStudentLogin(String email, String password) {
        return checkLoginCredentials(email, password, "Student");
    }

    @Override
    public boolean checkTeacherLogin(String email, String password) {
        return checkLoginCredentials(email, password, "Teacher");
    }

    /**
     * Adds a user record to the specified table.
     * @param email User's email.
     * @param firstName User's first name.
     * @param lastName User's last name.
     * @param password User's plain text password (will be hashed).
     * @param tableName The table to insert into ("Student" or "Teacher").
     * @return true if insertion was successful, false otherwise.
     */
    private boolean addUser(String email, String firstName, String lastName, String password, String tableName) {
        if (!tableName.equals("Student") && !tableName.equals("Teacher")) {
            System.err.println("Invalid table name provided to addUser: " + tableName);
            return false;
        }
        String hashedPassword = hashPassword(password); // Hash the password
        if (hashedPassword == null) {
            System.err.println("Password hashing failed. Cannot add user.");
            return false;
        }

        // Check if email already exists in the *specific* table before inserting
        if (tableName.equals("Student") ? checkStudentEmailExists(email) : checkTeacherEmailExists(email)) {
            System.err.println("Cannot add user: Email '" + email + "' already exists in table '" + tableName + "'.");
            return false; // Indicate failure due to existing email
        }

        String query = "INSERT INTO " + tableName + " (email, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected == 1; // Return true if exactly one row was inserted
        } catch (SQLException e) {
            // Specifically check for unique constraint violation (email already exists)
            if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed")) { // SQLite error code for UNIQUE constraint
                System.err.println("Cannot add user: Email '" + email + "' already exists (caught by DB constraint).");
                // This might be redundant if the checkEmailExistsInTable works reliably, but good as a safeguard.
            } else {
                System.err.println("Error adding " + tableName + ": " + e.getMessage());
                e.printStackTrace();
            }
            return false; // Return false on any exception
        }
    }

    @Override
    public boolean addStudent(String email, String firstName, String lastName, String password) {
        return addUser(email, firstName, lastName, password, "Student");
    }

    @Override
    public boolean addTeacher(String email, String firstName, String lastName, String password) {
        return addUser(email, firstName, lastName, password, "Teacher");
    }


    /**
     * Hashes a plain text password using SHA-256.
     * @param password The plain text password.
     * @return The Base64 encoded SHA-256 hash of the password, or null if hashing fails.
     */
    private static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash); // Encode hash to Base64 string for storage
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Password hashing algorithm not found: " + e.getMessage());
            e.printStackTrace();
            return null; // Indicate hashing failure
        }
    }


    /** @deprecated Use checkStudentLogin or checkTeacherLogin instead. */
    @Deprecated
    public String GetPassword(String email) {
        // This method doesn't know whether to check Student or Teacher table.
        // Returning null or throwing an exception might be better.
        System.err.println("Warning: GetPassword(email) is deprecated. Use methods specifying user type.");
        // Attempt to find in Teacher first, then Student (arbitrary choice)
        String pass = getPasswordHash(email, "Teacher");
        if (pass == null) {
            pass = getPasswordHash(email, "Student");
        }
        return pass; // Might be null
    }

    /** @deprecated Use checkStudentLogin or checkTeacherLogin instead. */
    @Deprecated
    public boolean checkPassword(String email, String password) {
        // This method doesn't know which table to check.
        System.err.println("Warning: checkPassword(email, password) is deprecated. Use methods specifying user type.");
        // Check teacher first, then student
        if (checkLoginCredentials(email, password, "Teacher")) {
            return true;
        }
        return checkLoginCredentials(email, password, "Student");
    }
}
