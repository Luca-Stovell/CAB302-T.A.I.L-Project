package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import java.util.List;
/**
 * Data Access Object interface for handling user login and registration,
 * differentiating between Students and Teachers.
 */
public interface ILoginDAO {

    /**
     * Checks if an email exists in the Student table.
     * @param email The email to check.
     * @return true if the email exists for a student, false otherwise.
     */
    boolean checkStudentEmailExists(String email);

    /**
     * Checks if an email exists in the Teacher table.
     * @param email The email to check.
     * @return true if the email exists for a teacher, false otherwise.
     */
    boolean checkTeacherEmailExists(String email);

    /**
     * Validates login credentials against the Student table.
     * @param email The student's email.
     * @param password The entered password (unhashed).
     * @return true if the email and hashed password match a record in the Student table, false otherwise.
     */
    boolean checkStudentLogin(String email, String password);

    /**
     * Validates login credentials against the Teacher table.
     * @param email The teacher's email.
     * @param password The entered password (unhashed).
     * @return true if the email and hashed password match a record in the Teacher table, false otherwise.
     */
    boolean checkTeacherLogin(String email, String password);

    /**
     * Adds a new student record to the database.
     * Assumes email uniqueness is handled or checked beforehand.
     * @param email Student's email (should be unique).
     * @param firstName Student's first name.
     * @param lastName Student's last name.
     * @param password Student's password (will be hashed before storing).
     * @return true if the student was added successfully, false otherwise.
     */
    boolean addStudent(String email, String firstName, String lastName, String password);

    /**
     * Adds a new teacher record to the database.
     * Assumes email uniqueness is handled or checked beforehand.
     * @param email Teacher's email (should be unique).
     * @param firstName Teacher's first name.
     * @param lastName Teacher's last name.
     * @param password Teacher's password (will be hashed before storing).
     * @return true if the teacher was added successfully, false otherwise.
     */
    boolean addTeacher(String email, String firstName, String lastName, String password);

    /**
     * Retrieves a list of all students, potentially just their names or full Student objects.
     * For simplicity here, let's return Student objects.
     * @return A List of Student objects, or an empty list if none found or an error occurs.
     */
    List<Student> getAllStudents();

    /**
     * Retrieves detailed information for a specific student based on their email.
     * Email is generally a better unique identifier than name.
     * @param email The email of the student to retrieve details for.
     * @return A Student object containing the details, or null if not found or an error occurs.
     */
    Student getStudentDetailsByEmail(String email);

    /**
     * Resets the password for a specific student identified by email.
     * The new password should be hashed before being stored.
     * @param email The email of the student whose password needs resetting.
     * @param newPassword The new plain text password.
     * @return true if the password was successfully reset, false otherwise.
     */
    boolean resetStudentPassword(String email, String newPassword);

    // Optional: Keep ChangePassword if needed, but it might need modification
    // to specify whether to change student or teacher password.
    // boolean ChangePassword(String email, String newPassword, String userType);
}
