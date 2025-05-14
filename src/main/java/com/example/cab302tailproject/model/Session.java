package com.example.cab302tailproject.model;

/**
 * Manages session-level data for the currently logged-in user.
 * Tracks which teacher or student is currently authenticated.
 * This is a simple static session manager used for storing the logged-in user's email.
 */
public class Session {

    /** The email address of the currently logged-in teacher, if any. */
    private static String loggedInTeacherEmail;

    /** The email address of the currently logged-in student, if any. */
    private static String loggedInStudentEmail;

    /**
     * Sets the currently logged-in teacher's email address.
     *
     * @param email The teacher's email.
     */
    public static void setLoggedInTeacherEmail(String email) {
        loggedInTeacherEmail = email;
    }

    /**
     * Gets the email address of the currently logged-in teacher.
     *
     * @return The teacher's email, or null if no teacher is logged in.
     */
    public static String getLoggedInTeacherEmail() {
        return loggedInTeacherEmail;
    }

    /**
     * Sets the currently logged-in student's email address.
     *
     * @param email The student's email.
     */
    public static void setLoggedInStudentEmail(String email) {
        loggedInStudentEmail = email;
    }

    /**
     * Gets the email address of the currently logged-in student.
     *
     * @return The student's email, or null if no student is logged in.
     */
    public static String getLoggedInStudentEmail() {
        return loggedInStudentEmail;
    }

    /**
     * Clears the session by removing both logged-in teacher and student email values.
     */
    public static void clear() {
        loggedInTeacherEmail = null;
        loggedInStudentEmail = null;
    }
}
