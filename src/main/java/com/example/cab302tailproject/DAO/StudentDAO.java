package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.UserDetail; // Assuming UserDAO might use this or it's relevant

import java.util.List;

/**
 * Data Access Object interface for Student-related operations.
 * Defines methods for managing students and their associations in the system.
 */
public interface StudentDAO extends UserDAO { // UserDAO interface would define getUserNameDetails and checkPassword if they are common

    /**
     * Adds a new student to the database.
     * The password should be stored securely after being hashed by the implementation.
     *
     * @param email      Student's email address.
     * @param firstName  Student's first name.
     * @param lastName   Student's last name.
     * @param password   Student's plain text password (to be hashed by implementation).
     * @return true if the student was added successfully, false otherwise.
     */
    boolean AddStudent(String email, String firstName, String lastName, String password);

    /**
     * Retrieves all students from the database.
     *
     * @return A list of {@link Student} objects.
     */
    List<Student> getAllStudents();

    /**
     * Checks if a student email already exists in the database.
     *
     * @param email The email address to check.
     * @return true if the email is found, false otherwise.
     */
    // boolean checkEmail(String email); // This is in UserDAO, so remove if UserDAO already declares it

    /**
     * Verifies a student's password by comparing the stored hash with the entered password.
     *
     * @param email    The student's email address.
     * @param password The plain text password to check.
     * @return true if the password matches the stored hash, false otherwise.
     */
    // boolean checkPassword(String email, String password); // This is in UserDAO

    /**
     * Retrieves all students associated with a specific classroom.
     *
     * @param classroomID The ID of the classroom.
     * @return A list of {@link Student} objects in the specified classroom.
     */
    List<Student> getStudentsByClassroomID(int classroomID);

    /**
     * Adds a student to a classroom (many-to-many relationship).
     *
     * @param studentID   The student's unique ID.
     * @param classroomID The classroom's unique ID.
     * @return true if the association was added successfully, false otherwise.
     */
    boolean addStudentToClassroom(int studentID, int classroomID);

    /**
     * Removes a student from a classroom.
     *
     * @param studentID   The student's unique ID.
     * @param classroomID The classroom's unique ID.
     * @return true if the student was successfully removed, false otherwise.
     */
    boolean removeStudentFromClassroom(int studentID, int classroomID);

    /**
     * Resets the password for a specific student identified by their email.
     * The new password should be hashed before storage.
     *
     * @param email       The student's email address.
     * @param newPassword The new plain text password.
     * @return true if the password was successfully reset, false otherwise.
     */
    boolean resetStudentPassword(String email, String newPassword);

    /**
     * Retrieves the ClassroomID for a given student.
     * Assumes a student is primarily associated with one classroom for this context,
     * or returns the first one found if multiple associations exist.
     *
     * @param studentID The ID of the student.
     * @return The ClassroomID, or -1 (or throw an exception) if not found or an error occurs.
     */
    int getClassroomIDForStudent(int studentID); // Added this method signature

    /**
     * Retrieves the StudentID for a given student email.
     *
     * @param email The email of the student.
     * @return The StudentID, or -1 if not found or an error occurs.
     */
    int getStudentIDByEmail(String email); // Added this method signature based on LearningCardController usage
}
