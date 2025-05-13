package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import java.util.List;

/**
 * Data Access Object interface for Student-specific operations.
 * Extends the base {@link UserDAO} which provides common user methods
 * like checkEmail, checkPassword, and getUserNameDetails.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.3
 */
public interface StudentDAO extends UserDAO {

    /**
     * Adds a new student to the database.
     * @param email Student's email.
     * @param firstName Student's first name.
     * @param lastName Student's last name.
     * @param password Student's plain text password (to be hashed by implementation).
     * @return true if the student was added successfully, false otherwise.
     */
    boolean AddStudent(String email, String firstName, String lastName, String password);

    /**
     * Retrieves a list of all students.
     * @return A list of {@link Student} objects.
     */
    List<Student> getAllStudents();

    /**
     * Retrieves a list of students belonging to a specific classroom.
     * @param classroomID The ID of the classroom.
     * @return A list of {@link Student} objects in that classroom.
     */
    List<Student> getStudentsByClassroomID(int classroomID);

    /**
     * Adds an existing student to an existing classroom.
     * This typically involves creating a record in a linking table (e.g., StudentClassroom).
     * @param studentID The ID of the student.
     * @param classroomID The ID of the classroom.
     * @return true if the student was successfully added to the classroom, false otherwise.
     */
    boolean addStudentToClassroom(int studentID, int classroomID);

    /**
     * Removes a student from a classroom.
     * This typically involves deleting a record from a linking table.
     * @param studentID The ID of the student.
     * @param classroomID The ID of the classroom.
     * @return true if the student was successfully removed from the classroom, false otherwise.
     */
    boolean removeStudentFromClassroom(int studentID, int classroomID);

    /**
     * Resets the password for a specific student identified by their email.
     * The new password provided should be plain text and will be hashed by this method before storage.
     * @param email The email of the student whose password needs resetting.
     * @param newPassword The new plain text password.
     * @return true if the password was successfully reset, false otherwise.
     */
    boolean resetStudentPassword(String email, String newPassword);

    // checkEmail(String email) is inherited from UserDAO
    // checkPassword(String email, String password) is inherited from UserDAO
    // getUserNameDetails(String email) is inherited from UserDAO
}
