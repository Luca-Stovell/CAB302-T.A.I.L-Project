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
 * @version 1.3
 */
public class SqliteTeacherDAO implements TeacherDAO {

    private final Connection connection;

    /**
     * Constructor initializes the database connection.
     * Table creation is expected to be handled by {@link DatabaseInitializer}.
     */
    public SqliteTeacherDAO() {
        this.connection = SqliteConnection.getInstance();
    }

    /**
     * Hashes a plain text password using SHA-256 and encodes it in Base64.
     * @param password The plain text password.
     * @return The hashed password string or null if hashing fails.
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
            System.err.println("Password hashing algorithm not found: " + e.getMessage());
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
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for teacher: " + email);
            return false;
        }

        if (checkEmail(email)) {
            System.err.println("Cannot add teacher: Email '" + email + "' already exists.");
            return false;
        }

        String query = "INSERT INTO Teacher (TeacherEmail, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Duplicate teacher email detected: " + email);
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
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for " + email);
            return false;
        }

        String query = "UPDATE Teacher SET password = ? WHERE TeacherEmail = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error updating password for teacher " + email + ": " + e.getMessage());
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
        String query = "SELECT COUNT(1) FROM Teacher WHERE TeacherEmail = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) == 1;
        } catch (SQLException e) {
            System.err.println("Error checking email for teacher " + email + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the hashed password for a teacher.
     *
     * @param email The teacher's email.
     * @return The hashed password, or null if not found.
     */
    private String getStoredPasswordHash(String email) {
        String query = "SELECT password FROM Teacher WHERE TeacherEmail = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        String storedHash = getStoredPasswordHash(email);
        if (storedHash == null) {
            System.err.println("No stored password found for teacher " + email);
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
     *
     * @param classID The classroom ID.
     * @param teacher The teacher's email.
     */
    @Override
    public void createClassroom(String classID, String teacher) {
        String query = "INSERT INTO Classroom (ClassID, TeacherEmail) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, classID);
            statement.setString(2, teacher);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating classroom " + classID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the teacher's name based on email.
     *
     * @param email The teacher's email.
     * @return A {@link UserDetail} object with first and last name, or null if not found.
     */
    @Override
    public UserDetail getUserNameDetails(String email) {
        String query = "SELECT firstName, lastName FROM Teacher WHERE TeacherEmail = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
