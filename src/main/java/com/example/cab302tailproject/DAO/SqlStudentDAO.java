package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.UserDetail;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * SQLite implementation of the {@link StudentDAO} interface.
 * Handles database operations for the Student table.
 *
 * @author
 * @version 1.3
 */
public class SqlStudentDAO implements StudentDAO {

    private final Connection connection;

    /**
     * Constructor initializes the database connection.
     * Table creation is expected to be handled by {@link DatabaseInitializer}.
     */
    public SqlStudentDAO() {
        this.connection = SqliteConnection.getInstance();
    }

    /**
     * Hashes a plain text password using SHA-256 and encodes it to Base64.
     * @param password The plain text password.
     * @return The Base64 encoded SHA-256 hash of the password, or null if hashing fails.
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
    public boolean AddStudent(String email, String firstName, String lastName, String password) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for student: " + email + ". Student not added.");
            return false;
        }
        if (checkEmail(email)) {
            System.err.println("Cannot add student: Email '" + email + "' already exists in Student table.");
            return false;
        }
        String query = "INSERT INTO Student (email, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Database constraint violation: Email '" + email + "' already exists for Student.");
            } else {
                System.err.println("Error adding student " + email + ": " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT StudentID, firstName, lastName, email, password FROM Student ORDER BY lastName, firstName";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                student.setStudentID(rs.getInt("StudentID"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all students: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public boolean checkEmail(String email) {
        String query = "SELECT COUNT(1) FROM Student WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error checking student email " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String getStoredPasswordHash(String email) {
        String query = "SELECT password FROM Student WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            System.err.println("Error getting student password hash for " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean checkPassword(String email, String password) {
        String storedHash = getStoredPasswordHash(email);
        if (storedHash == null) {
            System.err.println("No stored hash found for student email: " + email);
            return false;
        }
        String enteredHash = hashPassword(password);
        if (enteredHash == null) {
            System.err.println("Hashing of entered password failed for student email: " + email);
            return false;
        }
        return storedHash.equals(enteredHash);
    }

    @Override
    public boolean addStudentToClassroom(int studentID, int classroomID) {
        String query = "INSERT OR IGNORE INTO StudentClassroom (StudentID, ClassroomID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Student> getStudentsByClassroomID(int classroomID) {
        List<Student> students = new ArrayList<>();
        String query = """
            SELECT s.* FROM Student s
            JOIN StudentClassroom sc ON s.StudentID = sc.StudentID
            WHERE sc.ClassroomID = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, classroomID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                student.setStudentID(rs.getInt("StudentID"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public boolean removeStudentFromClassroom(int studentID, int classroomID) {
        String query = "DELETE FROM StudentClassroom WHERE StudentID = ? AND ClassroomID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserDetail getUserNameDetails(String email) {
        String query = "SELECT firstName, lastName FROM Student WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UserDetail(resultSet.getString("firstName"), resultSet.getString("lastName"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student name details for " + email + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Resets the password for a student identified by email.
     * The new password is hashed before being stored in the database.
     * @param email The email of the student whose password is to be reset.
     * @param newPassword The new plain text password.
     * @return true if the password was successfully updated, false otherwise.
     */
    @Override
    public boolean resetStudentPassword(String email, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed. Cannot reset password for student " + email);
            return false;
        }
        String query = "UPDATE Student SET password = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            System.err.println("Error resetting password for student " + email + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
