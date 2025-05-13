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
 * @author Your Name/TAIL Project Team
 * @version 1.5
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
        // Assuming Student table does not have ClassroomID directly, it's managed by StudentClassroom
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
        // Updated to fetch password for consistency with Student model instantiation,
        // but be mindful of security implications of loading all passwords.
        // Ensure your Student model has a constructor Student(firstName, lastName, email, password)
        // and a setStudentID(int id) method.
        String query = "SELECT StudentID, firstName, lastName, email, password FROM Student ORDER BY lastName, firstName";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Assuming Student model constructor: Student(firstName, lastName, email, password)
                // and a setter: setStudentID(int)
                Student student = new com.example.cab302tailproject.model.Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("password") // Password is now fetched
                );
                student.setStudentID(rs.getInt("StudentID")); // Assuming Student class has setStudentID
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all students: " + e.getMessage());
            e.printStackTrace();
        }
        // Optional: Logging retrieved students. Consider removing or using a logger for production.
        // Avoid logging passwords.
        for (Student s : students) {
            System.out.println(s.getFirstName() + " " + s.getLastName() + " | " + s.getEmail());
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

    /**
     * Adds a student to a classroom by creating an entry in the StudentClassroom join table.
     * Uses "INSERT OR IGNORE" to prevent errors if the student is already in the classroom.
     * @param studentID The ID of the student.
     * @param classroomID The ID of the classroom.
     * @return true if the student was added or already existed, false on database error.
     */
    @Override
    public boolean addStudentToClassroom(int studentID, int classroomID) {
        String query = "INSERT OR IGNORE INTO StudentClassroom (StudentID, ClassroomID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            // executeUpdate() for INSERT OR IGNORE might return 0 if the row already exists and was ignored,
            // or 1 if a new row was inserted. We consider both as success for "adding".
            // If you need to distinguish, you might need a SELECT first or handle the return value differently.
            stmt.executeUpdate();
            return true; // Assuming success if no exception, as IGNORE handles duplicates.
        } catch (SQLException e) {
            System.err.println("Error adding student " + studentID + " to classroom " + classroomID + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a list of students associated with a specific classroom ID.
     * Assumes a StudentClassroom join table and that the Student model has a constructor
     * Student(firstName, lastName, email, password) and a setStudentID(int id) method.
     * @param classroomID The ID of the classroom.
     * @return A list of {@link Student} objects. Returns an empty list if no students are found or an error occurs.
     */
    @Override
    public List<Student> getStudentsByClassroomID(int classroomID) {
        List<Student> students = new ArrayList<>();
        // This query selects all columns from the Student table (s.*)
        // by joining Student with StudentClassroom.
        String query = """
            SELECT s.StudentID, s.firstName, s.lastName, s.email, s.password FROM Student s
            JOIN StudentClassroom sc ON s.StudentID = sc.StudentID
            WHERE sc.ClassroomID = ?
            """;
        // Note: The original user query was "SELECT s.* ...". Explicitly listing columns
        // (StudentID, firstName, lastName, email, password) is generally safer.
        // If "s.*" is preferred, ensure Student table structure matches expected fields.

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, classroomID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Assuming Student model constructor: Student(firstName, lastName, email, password)
                // and a setter: setStudentID(int)
                Student student = new com.example.cab302tailproject.model.Student(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("password") // Password is fetched
                );
                student.setStudentID(rs.getInt("StudentID")); // Set the student's ID
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students for classroom ID " + classroomID + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Fetched " + students.size() + " students for classroom ID: " + classroomID);
        return students;
    }

    /**
     * Removes a student from a classroom by deleting the entry from the StudentClassroom join table.
     * @param studentID The ID of the student.
     * @param classroomID The ID of the classroom.
     * @return true if the student was successfully removed (at least one row affected), false otherwise or on error.
     */
    @Override
    public boolean removeStudentFromClassroom(int studentID, int classroomID) {
        String query = "DELETE FROM StudentClassroom WHERE StudentID = ? AND ClassroomID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            return stmt.executeUpdate() > 0; // True if one or more rows were deleted
        } catch (SQLException e) {
            System.err.println("Error removing student " + studentID + " from classroom " + classroomID + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
