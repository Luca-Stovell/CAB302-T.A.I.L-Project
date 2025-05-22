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
 * Handles database operations for the Student table and StudentClassroom join table.
 *
 */
public class SqlStudentDAO implements StudentDAO {

    private Connection connection;

    /**
     * Constructor initializes the database connection.
     * Table creation is expected to be handled by {@link DatabaseInitializer}.
     * Handles potential SQLException during connection acquisition.
     */
    public SqlStudentDAO() {
        try {
            this.connection = SqliteConnection.getInstance();
            if (this.connection == null || this.connection.isClosed()) {
                System.err.println("Failed to establish database connection in SqlStudentDAO: getInstance() returned null or closed connection.");
                throw new RuntimeException("Failed to establish database connection in SqlStudentDAO.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection in SqlStudentDAO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection in SqlStudentDAO.", e);
        }
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

    public int getStudentIDByEmail(String email) {
        if (this.connection == null) {
            System.err.println("Cannot get StudentID by email: database connection is not initialized.");
            return -1;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot get StudentID by email: database connection is closed.");
                return -1; // Or throw an exception
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in getStudentIDByEmail: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        String query = "SELECT StudentID FROM Student WHERE email = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("StudentID");
            }
        } catch (SQLException e) {
            System.err.println("Error getting StudentID by email: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public int getClassroomIDForStudent(int studentID) {
        if (this.connection == null) {
            System.err.println("Cannot get ClassroomID for student: database connection is not initialized.");
            return -1;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot get ClassroomID for student: database connection is closed.");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in getClassroomIDForStudent: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        String query = "SELECT ClassroomID FROM StudentClassroom WHERE StudentID = ? LIMIT 1";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setInt(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ClassroomID");
            }
        } catch (SQLException e) {
            System.err.println("Error getting ClassroomID for student " + studentID + ": " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    @Override
    public boolean AddStudent(String email, String firstName, String lastName, String password) {
        if (this.connection == null) {
            System.err.println("Cannot add student: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot add student: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in AddStudent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for student: " + email + ". Student not added.");
            return false;
        }
        if (checkEmail(email)) { // This method also needs connection checks
            System.err.println("Cannot add student: Email '" + email + "' already exists in Student table.");
            return false;
        }
        String query = "INSERT INTO Student (email, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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
        if (this.connection == null) {
            System.err.println("Cannot get all students: database connection is not initialized.");
            return students;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot get all students: database connection is closed.");
                return students;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in getAllStudents: " + e.getMessage());
            e.printStackTrace();
            return students;
        }

        String query = "SELECT StudentID, firstName, lastName, email, password FROM Student ORDER BY lastName, firstName";
        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Student student = new com.example.cab302tailproject.model.Student(
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
            System.err.println("Error checking connection status in checkEmail (StudentDAO): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        String query = "SELECT COUNT(1) FROM Student WHERE email = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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
            System.err.println("Error checking connection status in getStoredPasswordHash (StudentDAO): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        String query = "SELECT password FROM Student WHERE email = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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
        String storedHash = getStoredPasswordHash(email); // This method now checks connection
        if (storedHash == null) {
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
            System.err.println("Error checking connection status in getUserNameDetails (StudentDAO): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        String query = "SELECT firstName, lastName FROM Student WHERE email = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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

    @Override
    public boolean resetStudentPassword(String email, String newPassword) {
        if (this.connection == null) {
            System.err.println("Cannot reset student password: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot reset student password: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in resetStudentPassword: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed. Cannot reset password for student " + email);
            return false;
        }
        String query = "UPDATE Student SET password = ? WHERE email = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
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

    @Override
    public boolean addStudentToClassroom(int studentID, int classroomID) {
        if (this.connection == null) {
            System.err.println("Cannot add student to classroom: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot add student to classroom: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in addStudentToClassroom: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        String query = "INSERT OR IGNORE INTO StudentClassroom (StudentID, ClassroomID) VALUES (?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding student " + studentID + " to classroom " + classroomID + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Student> getStudentsByClassroomID(int classroomID) {
        List<Student> students = new ArrayList<>();
        if (this.connection == null) {
            System.err.println("Cannot get students by classroom ID: database connection is not initialized.");
            return students;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot get students by classroom ID: database connection is closed.");
                return students;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in getStudentsByClassroomID: " + e.getMessage());
            e.printStackTrace();
            return students;
        }
        String query = """
            SELECT s.StudentID, s.firstName, s.lastName, s.email, s.password FROM Student s
            JOIN StudentClassroom sc ON s.StudentID = sc.StudentID
            WHERE sc.ClassroomID = ?
            """;
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setInt(1, classroomID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new com.example.cab302tailproject.model.Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                student.setStudentID(rs.getInt("StudentID"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students for classroom ID " + classroomID + ": " + e.getMessage());
            e.printStackTrace();
        }
        // System.out.println("Fetched " + students.size() + " students for classroom ID: " + classroomID); // Removed for brevity
        return students;
    }

    @Override
    public boolean removeStudentFromClassroom(int studentID, int classroomID) {
        if (this.connection == null) {
            System.err.println("Cannot remove student from classroom: database connection is not initialized.");
            return false;
        }
        try {
            if (this.connection.isClosed()) {
                System.err.println("Cannot remove student from classroom: database connection is closed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status in removeStudentFromClassroom: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        String query = "DELETE FROM StudentClassroom WHERE StudentID = ? AND ClassroomID = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing student " + studentID + " from classroom " + classroomID + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
