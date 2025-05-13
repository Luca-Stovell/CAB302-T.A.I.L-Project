package com.example.cab302tailproject.model;


public class Session {
    private static String loggedInTeacherEmail;
    private static String loggedInStudentEmail;

    public static void setLoggedInTeacherEmail(String email) {
        loggedInTeacherEmail = email;
    }

    public static String getLoggedInTeacherEmail() {
        return loggedInTeacherEmail;
    }

    public static void setLoggedInStudentEmail(String email) {
        loggedInStudentEmail = email;
    }

    public static String getLoggedInStudentEmail() {
        return loggedInStudentEmail;
    }

    public static void clear() {
        loggedInTeacherEmail = null;
        loggedInStudentEmail = null;
    }
}

