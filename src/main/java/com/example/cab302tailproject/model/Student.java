package com.example.cab302tailproject.model;

/**
 * Represents a Student user, holding basic information retrieved from the database.
 * This is a simple Plain Old Java Object (POJO) or data class.
 */
public class Student {
    private final int studentID;
    private final String firstName;
    private final String lastName;
    private final String email;


    /**
     * Constructs a new Student object.
     *
     * @param studentID The unique ID of the student.
     * @param firstName The first name of the student.
     * @param lastName  The last name of the student.
     * @param email     The email address of the student.
     */
    public Student(int studentID, String firstName, String lastName, String email) {
        this.studentID = studentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // --- Getters ---

    public int getStudentID() {
        return studentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Returns the full name of the student.
     * @return The concatenated first and last name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Provides a string representation of the Student, typically used for display in lists.
     * @return The student's full name.
     */
    @Override
    public String toString() {
        return getFullName(); // Display full name in ListView
    }

}
