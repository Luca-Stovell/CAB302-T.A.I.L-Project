package com.example.cab302tailproject.model;

/**
 * A utility class to hold static information about the currently logged-in user.
 * This provides a simple way to access user details across different controllers.
 * Implemented as a singleton.
 */
public class UserSession {

    private static UserSession instance;

    private String firstName;
    private String lastName;
    private String email;
    private String role; // "Teacher" or "Student"

    // Private constructor to prevent direct instantiation
    private UserSession() {}

    /**
     * Gets the singleton instance of UserSession.
     * If an instance doesn't exist, it creates one.
     * @return The singleton UserSession instance.
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Sets the details of the currently logged-in user.
     * This should be called after a successful login.
     *
     * @param firstName The first name of the logged-in user.
     * @param lastName  The last name of the logged-in user.
     * @param email     The email of the logged-in user.
     * @param role      The role of the logged-in user (e.g., "Teacher", "Student").
     */
    public void loginUser(String firstName, String lastName, String email, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        System.out.println("User session started for: " + getFullName() + " as " + role);
    }

    /**
     * Clears the current user session details.
     * This should be called on logout.
     */
    public void logoutUser() {
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.role = null;
        System.out.println("User session ended.");
        // To make it a true reset for next login if desired, though not strictly necessary
        // instance = null;
    }

    /**
     * Gets the first name of the logged-in user.
     * @return The first name, or "Guest" if no user is logged in.
     */
    public String getFirstName() {
        return firstName != null ? firstName : "Guest";
    }

    /**
     * Gets the last name of the logged-in user.
     * @return The last name, or "" if no user is logged in.
     */
    public String getLastName() {
        return lastName != null ? lastName : "";
    }

    /**
     * Gets the full name of the logged-in user.
     * @return The full name (firstName + " " + lastName), or "Guest" if no user is logged in.
     */
    public String getFullName() {
        if (firstName != null && !firstName.isEmpty()) {
            return firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
        }
        return "Guest"; // Default if no user is logged in
    }

    /**
     * Gets the email of the logged-in user.
     * @return The email, or null if no user is logged in.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the role of the logged-in user.
     * @return The role (e.g., "Teacher", "Student"), or "Unknown" if no user is logged in or role not set.
     */
    public String getRole() {
        return role != null ? role : "Unknown";
    }

    /**
     * Checks if a user is currently logged in (i.e., if session data like email and role is present).
     * @return true if user data is present, false otherwise.
     */
    public boolean isLoggedIn() {
        return email != null && !email.isEmpty() && role != null && !role.isEmpty();
    }
}
