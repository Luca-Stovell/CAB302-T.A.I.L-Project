package com.example.cab302tailproject.model;

/**
 * Represents a generic user of the system.
 * This base class is extended by more specific user types such as {@link Student} and {@link Teacher}.
 */
public class User {

    // User attributes
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;

    /**
     * Constructs a new User with the provided details.
     *
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param email     The user's email address.
     * @param password  The user's password (ideally hashed before storage).
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the user's email address.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's password.
     * Note: In production, passwords should always be stored and returned as hashes.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The new email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's password.
     * Note: This should be a hashed password in real-world applications.
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
