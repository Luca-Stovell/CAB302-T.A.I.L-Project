package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Teacher;
import com.example.cab302tailproject.model.UserDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SQLite implementation of the {@link TeacherDAO} interface.
 * Handles database operations for the Teacher table.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.2
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
     * Hashes a plain text password using SHA-256 and encodes it to Base64.
     * IMPORTANT: For production systems, use a stronger, salted hashing algorithm like BCrypt or Argon2.
     * @param password The plain text password.
     * @return The Base64 encoded SHA-256 hash of the password, or null if hashing fails or password is empty.
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

    @Override
    public boolean AddTeacher(String email, String firstName, String lastName, String password) {
        String hashedPassword = hashPassword(password);
        try {
            String query = "INSERT INTO Teacher (TeacherEmail, firstName, lastName, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for teacher: " + email + ". Teacher not added.");
            return false;
        }
        // Check if email already exists in the Teacher table
        if (checkEmail(email)) {
            System.err.println("Cannot add teacher: Email '" + email + "' already exists in Teacher table.");
            return false;
        }
        String query = "INSERT INTO Teacher (email, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Database constraint violation: Email '" + email + "' already exists for Teacher.");
            } else {
                System.err.println("Error adding teacher " + email + ": " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean ChangePassword(String email, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for ChangePassword, teacher: " + email);
            return false;
        }
        String query = "UPDATE Teacher SET password = ? WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
        try {
            String query  = "UPDATE Teacher SET password = ? WHERE TeacherEmail = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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

    @Override
    public boolean checkEmail(String email) {
        String query = "SELECT COUNT(1) FROM Teacher WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
        try {
            String query =  "SELECT COUNT(1) FROM Teacher WHERE TeacherEmail = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
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
     * Retrieves the stored hashed password for a teacher.
     * @param email The teacher's email.
     * @return The hashed password, or null if not found or an error occurs.
     */
    private String getStoredPasswordHash(String email) {
        String query = "SELECT password FROM Teacher WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            public String GetPassword (String email){
                try {
                    String query = "SELECT password FROM Teacher WHERE TeacherEmail = ?;";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, email);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getString("password");
                    }
                } catch (SQLException e) {
                    System.err.println("Error getting teacher password hash for " + email + ": " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
            }

            @Override
            public boolean checkPassword (String email, String password){
                String storedHash = getStoredPasswordHash(email);
                if (storedHash == null) {
                    System.err.println("No stored hash found for teacher email: " + email);
                    return false; // Email not found or error
                }
                String enteredHash = hashPassword(password);
                if (enteredHash == null) {
                    System.err.println("Hashing of entered password failed for teacher email: " + email);
                    return false; // Hashing of entered password failed
                }
                return storedHash.equals(enteredHash);
            }

            @Override
            public UserDetail getUserNameDetails (String email){
                String query = "SELECT firstName, lastName FROM Teacher WHERE email = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, email);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return new UserDetail(resultSet.getString("firstName"), resultSet.getString("lastName"));
                    }
                } catch (SQLException e) {
                    System.err.println("Error retrieving teacher name details for " + email + ": " + e.getMessage());
                    e.printStackTrace();
                }
                return null; // User not found or error
            }

            @Override
            public void createClassroom (String ClassID, String Teacher){
                try {
                    String query = "INSERT INTO Classroom (ClassID, TeacherEmail) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, ClassID);
                    statement.setString(2, Teacher);
                    int rowsInserted = statement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }