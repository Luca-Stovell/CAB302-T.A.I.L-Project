package com.example.cab302tailproject.DAO;

public interface TeacherDAO extends UserDAO {
    public boolean AddTeacher(String email, String firstName, String lastName, String password);

    /**
     * Checks if an email has been registered to the database
     * @param email The email that is being checked
     * @return true if email was found, false if not
     */
    public boolean checkEmail(String email);
    /**
     * changes the password of an existing account
     * @param email email of the existing account
     * @param newPassword The new password
     * @return true if successful, false if account doesn't exist
     */
    public  boolean ChangePassword(String email, String newPassword);


    /**
     * Checks an input password against the stored password of an account
     * @param email email of the account being checked
     * @param password the input password
     * @return true if passwords match, false if not
     */
    public boolean checkPassword(String email ,String password);




}
