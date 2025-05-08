package com.example.cab302tailproject.DAO;

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

    // Optional: Keep ChangePassword if needed, but it might need modification
    // to specify whether to change student or teacher password.
    // boolean ChangePassword(String email, String newPassword, String userType);
}
