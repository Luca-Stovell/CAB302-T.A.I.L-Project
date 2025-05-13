package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import java.util.List;

/**
 * Data Access Object interface for Student specific operations.
 * Extends the base UserDAO for common user functionalities.
 */
public interface StudentDAO extends UserDAO {

    /**
     * Adds a new student to the database.
     * The implementation should handle password hashing.
     * @param email Student's email (should be unique).
     * @param firstName Student's first name.
     * @param lastName Student's last name.
     * @param password Student's plain text password.
     * @return true if the student was added successfully, false otherwise (e.g., email already exists).
     */
    boolean AddStudent(String email, String firstName, String lastName, String password);

    /**
     * Retrieves a list of all students from the database.
     * @return A list of {@link Student} objects; an empty list if no students are found.
     */
    List<Student> getAllStudents();

    /**
     * Resets the password for a specific student identified by their email.
     * The new password provided should be plain text and will be hashed by the implementation before storage.
     * @param email The email of the student whose password needs resetting.
     * @param newPassword The new plain text password.
     * @return true if the password was successfully reset, false otherwise (e.g., student not found).
     */
    boolean resetStudentPassword(String email, String newPassword);

    // Methods checkEmail(String email), checkPassword(String email, String password),
    // and getUserNameDetails(String email) are inherited from UserDAO.
    // No need to redeclare them here.
}
