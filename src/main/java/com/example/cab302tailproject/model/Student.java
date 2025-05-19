package com.example.cab302tailproject.model;

/**
 * Represents a student in the system.
 * Extends the {@link User} class and adds additional fields specific to students,
 * such as a unique student ID, associated teacher email, and classroom ID.
 */
public class Student extends User {

    /** Unique identifier for the student, typically set by the database. */
    private int studentID;

    /** The email of the teacher associated with this student. */
    private String teacherEmail;

    /** The ID of the classroom this student belongs to. */
    private int classroomID;

    /**
     * Constructs a Student with full credentials, including password.
     * Typically used when registering or authenticating a student.
     *
     * @param firstName The first name of the student.
     * @param lastName  The last name of the student.
     * @param email     The student's email address.
     * @param password  The student's password (ideally hashed before storage).
     */
    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    /**
     * Constructs a Student without a password.
     * Useful when retrieving students from the database without exposing sensitive information.
     *
     * @param firstName The first name of the student.
     * @param lastName  The last name of the student.
     * @param email     The student's email address.
     */
    public Student(String firstName, String lastName, String email) {
        this(firstName, lastName, email, null);
    }

    /**
     * Returns the full name of the student.
     *
     * @return A string in the format "FirstName LastName".
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Gets the unique student ID.
     *
     * @return The student's ID.
     */
    public int getStudentID() {
        return studentID;
    }

    /**
     * Sets the student's ID.
     *
     * @param studentID The ID to assign.
     */
    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    /**
     * Gets the email address of the associated teacher.
     *
     * @return The teacher's email.
     */
    public String getTeacherEmail() {
        return teacherEmail;
    }

    /**
     * Sets the email of the teacher associated with this student.
     *
     * @param teacherEmail The teacher's email.
     */
    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    /**
     * Gets the ID of the classroom the student is in.
     *
     * @return The classroom ID.
     */
    public int getClassroomID() {
        return classroomID;
    }

    /**
     * Sets the classroom ID for this student.
     *
     * @param classroomID The ID of the classroom.
     */
    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
    }

    /**
     * Returns a string representation of the student.
     * Used for displaying in UI controls such as {@code ListView}.
     *
     * @return The student's full name.
     */
    @Override
    public String toString() {
        return getFullName();
    }
}
