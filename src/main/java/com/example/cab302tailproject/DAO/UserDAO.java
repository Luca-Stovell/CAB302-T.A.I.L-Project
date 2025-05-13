package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.UserDetail;

/**
 * Base Data Access Object interface for user operations.
 * Defines common methods for checking email, password, and retrieving basic user details.
 */
public interface UserDAO {
    /**
     * Checks if an email is registered in the specific user table (Student or Teacher).
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     */
    boolean checkEmail(String email);

    /**
     * Checks if the provided plain text password matches the stored hashed password for the given email.
     * @param email The user's email.
     * @param password The plain text password to check.
     * @return true if the password matches, false otherwise.
     */
    boolean checkPassword(String email, String password);

    /**
     * Retrieves the first and last name of a user based on their email.
     * @param email The email of the user.
     * @return A {@link UserDetail} object containing the first and last name, or null if the user is not found or an error occurs.
     */
    UserDetail getUserNameDetails(String email);
}
