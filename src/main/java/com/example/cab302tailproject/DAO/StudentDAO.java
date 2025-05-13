package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;

import java.util.List;

public interface StudentDAO extends UserDAO {

    boolean AddStudent(String email, String firstName, String lastName, String password);

    List<Student> getAllStudents();
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
    List<Student> getAllStudents(); // For analytics page, if needed by other parts

    boolean checkEmail(String email);

    boolean checkPassword(String email, String password);

    List<Student> getStudentsByClassroomID(int classroomID);

    boolean addStudentToClassroom(int studentID, int classroomID);

    boolean removeStudentFromClassroom(int studentID, int classroomID);

    /**
     * Resets the password for a specific student identified by their email.
     * The new password provided should be plain text and will be hashed by this method before storage.
     * @param email The email of the student whose password needs resetting.
     * @param newPassword The new plain text password.
     * @return true if the password was successfully reset, false otherwise.
     */
    boolean resetStudentPassword(String email, String newPassword);

}
