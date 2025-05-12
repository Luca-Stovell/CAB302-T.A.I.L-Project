package com.example.cab302tailproject.DAO;

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
    public boolean checkEmail(String email);
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
    public boolean checkPassword(String email ,String password);




}
