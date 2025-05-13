package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.UserDetail; // Import UserDetail

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
        if (hashedPassword == null) {
            System.err.println("Password hashing failed for teacher: " + email + ". Teacher not added.");
            return false;
        }
        // Check if email already exists in the Teacher table
        if (checkEmail(email)) { // This calls the corrected checkEmail below
            System.err.println("Cannot add teacher: Email '" + email + "' already exists in Teacher table.");
            return false;
        }

        // Use 'email' as the column name, consistent with createTeacherTable
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
        // Use 'email' as the column name
        String query = "UPDATE Teacher SET password = ? WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        // Use 'email' as the column name
        String query = "SELECT COUNT(1) FROM Teacher WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
     * This is an internal helper method.
     * @param email The teacher's email.
     * @return The hashed password, or null if not found or an error occurs.
     */
    private String getStoredPasswordHash(String email) {
        // Use 'email' as the column name
        String query = "SELECT password FROM Teacher WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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

    @Override
    public boolean checkPassword(String email, String password) {
        String storedHash = getStoredPasswordHash(email);
        if (storedHash == null) {
            System.err.println("No stored hash found for teacher email: " + email + " (or email does not exist).");
            return false;
        }
        String enteredHash = hashPassword(password);
        if (enteredHash == null) {
            System.err.println("Hashing of entered password failed for teacher email: " + email);
            return false;
        }
        return storedHash.equals(enteredHash);
    }

    /**
     * Creates a classroom entry. Note: This method is not part of the TeacherDAO interface.
     * The Classroom table schema in DatabaseInitializer uses ClassroomID and TeacherID (INTEGER).
     * This method uses ClassID (String) and TeacherEmail (String) for Classroom table.
     * This will likely cause issues if the table schema is different.
     * For consistency, Classroom table should ideally use TeacherID (INTEGER) as foreign key.
     *
     * @param classID A string identifier for the class.
     * @param teacherEmail The email of the teacher for this class.
     */
    // This method is not in the TeacherDAO interface. If it's needed, it should be added or be a private helper.
    // Also, the query uses "TeacherEmail" for the column, ensure this matches your Classroom table schema.
    // Your DatabaseInitializer creates Classroom with "TeacherID INTEGER" as FK.
    // This method would need to get TeacherID from TeacherEmail first to insert correctly.
    public void createClassroom(String classID, String teacherEmail) {
        // To correctly insert into Classroom table (as per DatabaseInitializer schema),
        // you'd first need to get TeacherID from the Teacher table using teacherEmail.
        // Then insert (ClassroomID, TeacherID_fetched) into Classroom.
        // The current query "INSERT INTO Classroom (ClassID, TeacherEmail) VALUES (?, ?)"
        // assumes Classroom table has ClassID and TeacherEmail columns.
        // Your DatabaseInitializer has ClassroomID (PK) and TeacherID (FK to Teacher.TeacherID).

        System.out.println("Attempting to create classroom. ClassID: " + classID + ", TeacherEmail: " + teacherEmail);
        System.err.println("WARNING: createClassroom method in SqliteTeacherDAO might be inconsistent with Classroom table schema (TeacherID vs TeacherEmail).");

        // Example of how it *should* work if Classroom table expects TeacherID (INTEGER FK):
        /*
        Integer teacherId = null;
        String getTeacherIdQuery = "SELECT TeacherID FROM Teacher WHERE email = ?";
        try (PreparedStatement idStmt = connection.prepareStatement(getTeacherIdQuery)) {
            idStmt.setString(1, teacherEmail);
            ResultSet rs = idStmt.executeQuery();
            if (rs.next()) {
                teacherId = rs.getInt("TeacherID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Could not fetch TeacherID for email: " + teacherEmail);
            return;
        }

        if (teacherId == null) {
            System.err.println("No teacher found with email: " + teacherEmail + ". Cannot create classroom.");
            return;
        }

        String query = "INSERT INTO Classroom (TeacherID) VALUES (?)"; // Assuming ClassroomID is autoincrement
                                                                      // and you only need to link TeacherID.
                                                                      // If ClassID is a separate textual ID you want to store,
                                                                      // your Classroom table needs a ClassID TEXT column.
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // statement.setString(1, classID); // If you have a ClassID TEXT column
            statement.setInt(1, teacherId); // Assuming Classroom table has TeacherID as INTEGER FK
            statement.executeUpdate();
            System.out.println("Classroom entry potentially created/updated for TeacherID: " + teacherId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating classroom entry: " + e.getMessage());
        }
        */
        // For now, commenting out the original problematic implementation to avoid further errors.
        // If you need this functionality, the Classroom table schema and this method need to be aligned.
        /*
        try {
            String query = "INSERT INTO Classroom (ClassID, TeacherEmail) VALUES (?, ?)"; // This query is likely wrong based on your DatabaseInitializer
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, classID);
            statement.setString(2, teacherEmail);
            int rowsInserted = statement.executeUpdate();
        } catch (Exception e) { // Should be SQLException
            e.printStackTrace();
        }
        */
    }

    @Override
    public UserDetail getUserNameDetails(String email) {
        // Use 'email' as the column name
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
}
