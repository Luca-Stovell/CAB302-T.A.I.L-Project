package com.example.cab302tailproject.model;

public class Student extends User {
    /**
     * Constructor for creating a Student object with all details.
     * @param firstName The first name of the student.
     * @param lastName The last name of the student.
     * @param email The email address of the student.
     * @param password The password for the student's account.
     */
    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    /**
     * Constructor for creating a Student object without a password (e.g., when retrieving from DB without exposing password).
     * @param firstName The first name of the student.
     * @param lastName The last name of the student.
     * @param email The email address of the student.
     */
    public Student(String firstName, String lastName, String email) {
        this(firstName, lastName, email, null); // Calls the other constructor with a null password
    }

    /**
     * Returns the full name of the student.
     * This method assumes that the parent User class has getFirstName() and getLastName() methods.
     * @return A string representing the student's full name (e.g., "John Doe").
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Provides a string representation of the Student object, typically used for display in UI controls like ListView.
     * This implementation returns the student's full name.
     * @return The full name of the student.
     */
    @Override
    public String toString() {
        return getFullName(); // ListView will use this to display student names
    }
}
