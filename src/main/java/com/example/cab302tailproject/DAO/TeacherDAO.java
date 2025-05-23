package com.example.cab302tailproject.DAO;

import java.util.List;

public interface TeacherDAO extends UserDAO {
    /**
     * Adds a new teacher to the database.
     * @param email Teacher's email.
     * @param firstName Teacher's first name.
     * @param lastName Teacher's last name.
     * @param password Teacher's plain text password (to be hashed by implementation).
     * @return true if the teacher was added successfully, false otherwise.
     */
    boolean AddTeacher(String email, String firstName, String lastName, String password);

    /**
     * Checks if an email has been registered to the database
     * @param email The email that is being checked
     * @return true if email was found, false if not
     */
    boolean checkEmail(String email);
    /**
     * Changes the password for an existing teacher account.
     * @param email The email of the teacher.
     * @param newPassword The new plain text password (to be hashed by implementation).
     * @return true if the password was changed successfully, false otherwise.
     */
    boolean ChangePassword(String email, String newPassword);

    /**
     * Checks an input password against the stored password of an account
     * @param email email of the account being checked
     * @param password the input password
     * @return true if passwords match, false if not
     */
    boolean checkPassword(String email ,String password);

    /**
     * This creates a new classroom instance in the database.
     *
     * @param ClassID is the primary key in the database.
     * @param Teacher is the teacher associated with the class.
     */
    void createClassroom(String ClassID, String Teacher);

    /**
     * Retrieves the teacher's unique identifier (TeacherID) based on their email address.
     *
     * @param teacherEmail the email address of the teacher whose ID is to be retrieved
     * @return the TeacherID associated with the provided email address, or -1 if no match is found or an error occurs
     */
    int getTeacherID(String teacherEmail);

    /**
     * Retrieves a list of classroom IDs associated with the given teacher's email.
     *
     * @param teacherEmail the email address of the teacher whose classrooms are to be retrieved
     * @return a list of integers representing the classroom IDs associated with the teacher
     */
    List<Integer> getClassroomList(String teacherEmail);

}
