package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student; // Import Student model

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList; // Import ArrayList
import java.util.Base64;
import java.util.List; // Import List

/**
 * SQLite implementation of the ILoginDAO interface.
 * Handles database operations for Student and Teacher tables related to login, registration,
 * and student data management for analytics.
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
        // Ensure enteredHash is not null before comparing
        return enteredHash != null && storedHash.equals(enteredHash); // Compare hashes
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
     * Retrieves a list of all students from the database.
     * @return A List of Student objects. Returns an empty list if no students are found or an error occurs.
     */
    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT StudentID, firstName, lastName, email FROM Student ORDER BY lastName, firstName"; // Order for consistency
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("StudentID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Retrieves details for a specific student by their email.
     * @param email The email of the student.
     * @return A Student object, or null if not found or an error occurs.
     */
    @Override
    public Student getStudentDetailsByEmail(String email) {
        String query = "SELECT StudentID, firstName, lastName, email FROM Student WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("StudentID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student details for email " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Not found or error
    }

    /**
     * Updates the password for a student identified by email. Hashes the new password.
     * @param email The email of the student.
     * @param newPassword The new plain text password.
     * @return true if the password was updated successfully, false otherwise.
     */
    @Override
    public boolean resetStudentPassword(String email, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed. Cannot reset password.");
            return false;
        }
        String query = "UPDATE Student SET password = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1; // Check if exactly one row was updated
        } catch (SQLException e) {
            System.err.println("Error resetting password for student " + email + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String hashPassword(String password) {
        if (password == null) return null;
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
}
