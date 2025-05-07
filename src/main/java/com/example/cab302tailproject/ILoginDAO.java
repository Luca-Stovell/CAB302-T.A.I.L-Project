package com.example.cab302tailproject;


// Refers to login table, so has method for login and registration pages
public interface ILoginDAO {

    /**
     * Checks if an email has been registered to the database
      * @param email The email that is being checked
     * @return true if email was found, false if not
     */
    public boolean CheckEmail(String email);

    /**
     * Retrieves the password of a particular account
     * @param email The email of the account that is being checked
     * @return The specified email's stored password
     */
    public String GetPassword(String email);

    /**
     * Checks an input password against the stored password of an account
     * @param email email of the account being checked
     * @param password the input password
     * @return true if passwords match, false if not
     */
    public boolean checkPassword(String email ,String password);
    /**
     * Registers a new account to the database
     *
     * @param email    account email
     * @param password account password
     * @return true if successful, false if email already exists
     */
    public boolean AddAccount(String email, String firstName, String lastName, String password);

    public boolean AddStudent(String email, String firstName, String lastName, String password);

    public boolean AddTeacher(String email, String firstName, String lastName, String password);

    /**
     * changes the password of an existing account
     * @param email email of the existing account
     * @param newPassword The new password
     * @return true if successful, false if account doesn't exist
     */
    public  boolean ChangePassword(String email, String newPassword);


}
