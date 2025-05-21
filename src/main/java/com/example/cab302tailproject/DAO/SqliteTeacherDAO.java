package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.UserDetail;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SQLite implementation of the {@link TeacherDAO} interface.
 * Handles database operations for the Teacher table.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.5
 */
public class SqliteTeacherDAO implements TeacherDAO {

    private Connection connection;

    /**
     * Constructor initializes the database connection.
     * Table creation is expected to be handled by {@link DatabaseInitializer}.
     * Handles potential SQLException during connection acquisition.
     */
    public SqliteTeacherDAO() {
        try {
            this.connection = SqliteConnection.getInstance();
            if (this.connection == null || this.connection.isClosed()) {
                System.err.println("Failed to establish database connection in SqliteTeacherDAO: getInstance() returned null or closed connection.");
                throw new RuntimeException("Failed to establish database connection in SqliteTeacherDAO.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection in SqliteTeacherDAO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection in SqliteTeacherDAO.", e);
        }
    }

    /**
     * Hashes a plain text password using SHA-256 and encodes it in Base64.
     * @param password The plain text password.
     * @return The hashed password string or null if hashing fails or password is empty.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            System.err.println("Password to hash cannot be null or empty.");
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Password hashing algorithm (SHA-256) not found: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a new teacher to the database.
     *
     * @param email     The teacher's email.
     * @param firstName First name.
     * @param lastName  Last name.
     * @param password  Plain text password (will be hashed).
     * @return true if successfully added, false otherwise.
     */
    @Override
    public boolean AddTeacher(String email, String firstName, String lastName, String password) {
        if (this.connection == null) {
            System.err.println("Cannot add teacher: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot add teacher: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in AddTeacher: " + e.getMessage());
            e.printStackTrace();
            return false;
        }


        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for teacher: " + email + ". Teacher not added.");
            return false;
        }

        if (checkEmail(email)) { // checkEmail will use its own connection logic or ensure this.connection is open
            System.err.println("Cannot add teacher: Email '" + email + "' already exists in Teacher table.");
            return false;
        }

        String query = "INSERT INTO Teacher (TeacherEmail, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Database constraint violation: Email '" + email + "' already exists for Teacher.");
            } else {
                System.err.println("Error adding teacher " + email + ": " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the teacher's password.
     *
     * @param email       The teacher's email.
     * @param newPassword The new plain text password.
     * @return true if the password was updated successfully.
     */
    @Override
    public boolean ChangePassword(String email, String newPassword) {
        if (this.connection == null) {
            System.err.println("Cannot change password: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot change password: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in ChangePassword: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for ChangePassword, teacher: " + email);
            return false;
        }

        String query = "UPDATE Teacher SET password = ? WHERE TeacherEmail = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            int linesChanged = statement.executeUpdate();
            return linesChanged == 1;
        } catch (SQLException e) {
            System.err.println("Error changing teacher password for " + email + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the email exists in the Teacher table.
     *
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     */
    @Override
    public boolean checkEmail(String email) {
        if (this.connection == null) {
            System.err.println("Cannot check email: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot check email: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in checkEmail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String query = "SELECT COUNT(1) FROM Teacher WHERE TeacherEmail = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error checking teacher email " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the hashed password for a teacher.
     * Private helper method.
     *
     * @param email The teacher's email.
     * @return The hashed password, or null if not found or an error occurs.
     */
    private String getStoredPasswordHash(String email) {
        if (this.connection == null) {
            System.err.println("Cannot get stored password hash: database connection is not initialized.");
            return null;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot get stored password hash: database connection is closed.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in getStoredPasswordHash: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        String query = "SELECT password FROM Teacher WHERE TeacherEmail = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving password for teacher " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Compares entered password with the stored hash.
     *
     * @param email    The teacher's email.
     * @param password The entered plain text password.
     * @return true if the password matches, false otherwise.
     */
    @Override
    public boolean checkPassword(String email, String password) {
        // getStoredPasswordHash will handle connection checks
        String storedHash = getStoredPasswordHash(email);
        if (storedHash == null) {
            // Error message already printed in getStoredPasswordHash or if email not found
            return false;
        }

        String enteredHash = hashPassword(password);
        if (enteredHash == null) {
            System.err.println("Failed to hash input password for " + email);
            return false;
        }

        return storedHash.equals(enteredHash);
    }

    /**
     * Inserts a new classroom associated with the given teacher.
     * Assumes `TeacherDAO` interface defines this method signature.
     *
     * @param classID The classroom ID (or unique code).
     * @param teacherEmail The teacher's email to associate with the classroom.
     */
    @Override
    public void createClassroom(String classID, String teacherEmail) {
        if (this.connection == null) {
            System.err.println("Cannot create classroom: database connection is not initialized.");
            return;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot create classroom: database connection is closed.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in createClassroom (TeacherDAO): " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String query = "INSERT INTO Classroom (ClassID, TeacherEmail) VALUES (?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, classID);
            statement.setString(2, teacherEmail);
            statement.executeUpdate();
            System.out.println("Classroom " + classID + " created for teacher " + teacherEmail);
        } catch (SQLException e) {
            System.err.println("Error creating classroom " + classID + " for teacher " + teacherEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the teacher's name based on email.
     *
     * @param email The teacher's email.
     * @return A {@link UserDetail} object with first and last name, or null if not found or on error.
     */
    @Override
    public UserDetail getUserNameDetails(String email) {
        if (this.connection == null) {
            System.err.println("Cannot get user name details: database connection is not initialized.");
            return null;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot get user name details: database connection is closed.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in getUserNameDetails: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        String query = "SELECT firstName, lastName FROM Teacher WHERE TeacherEmail = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UserDetail(resultSet.getString("firstName"), resultSet.getString("lastName"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving name for teacher " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
