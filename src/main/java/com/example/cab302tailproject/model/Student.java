package com.example.cab302tailproject.model;

/**
 * Represents a Student user, inheriting from the User class.
 * This class can have student-specific properties and methods.
 *
 * @author Your Name/TAIL Project Team
 * @version 1.1
 */
public class Student extends User {

    // Optional: Add student-specific fields here, e.g., studentID if not in User
    // private int studentID; // If you want a separate ID from a potential User ID

    /**
     * Constructs a new Student object with all details including password.
     *
     * @param firstName The first name of the student.
     * @param lastName  The last name of the student.
     * @param email     The email address of the student.
     * @param password  The password for the student's account.
     */
    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password); // Calls the constructor of the User class
    }

    /**
     * Constructs a new Student object without a password, potentially for display purposes
     * or when password is not immediately required.
     *
     * @param firstName The first name of the student.
     * @param lastName  The last name of the student.
     * @param email     The email address of the student.
     */
    public Student(String firstName, String lastName, String email) {
        this(firstName, lastName, email, null); // Calls the other constructor with password as null
    }

    // Optional: If you added a studentID field in this class
    // public int getStudentID() { return studentID; }
    // public void setStudentID(int studentID) { this.studentID = studentID; }


    /**
     * Returns the full name of the student by concatenating the first and last names.
     * This method relies on `getFirstName()` and `getLastName()` being available,
     * which they are if inherited from a User class that has them.
     *
     * @return The full name of the student (e.g., "Max Power").
     * Returns an empty string or partial name if first/last names are null/empty.
     */
    public String getFullName() {
        String fName = getFirstName(); // Inherited from User
        String lName = getLastName();  // Inherited from User

        if (fName != null && !fName.isEmpty() && lName != null && !lName.isEmpty()) {
            return fName + " " + lName;
        } else if (fName != null && !fName.isEmpty()) {
            return fName;
        } else if (lName != null && !lName.isEmpty()) {
            return lName;
        }
        return ""; // Or handle as appropriate if names are missing
    }

    /**
     * Provides a string representation of the Student, typically used for display in lists.
     * By default, this will display the student's full name.
     *
     * @return The student's full name.
     */
    @Override
    public String toString() {
        return getFullName(); // ListView will use this by default
    }
}
