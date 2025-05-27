package com.example.cab302tailproject.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private static final String firstName = "John";
    private static final String lastName = "Smith";
    private static final String email = "js@gmail.com";
    // Role isn't actually validated
    private static final String role = "Teacher"; // "Teacher" or "Student"

    private static final String fullName = "John Smith";

    private static final String guestfirstName = "Guest";
    private static final String guestlastName = "";
    private static final String guestrole = "Unknown"; // "Teacher" or "Student"

    UserSession s;

    @BeforeEach
    void setUp() {
        s = UserSession.getInstance();
        s.loginUser(firstName, lastName, email, role);
    }


    @Test
    void logoutUser() {
        // Also tests getters when everything is null
        s.logoutUser();
        assertEquals(guestfirstName, s.getFirstName());
        assertEquals(guestlastName, s.getLastName());
        assertEquals(guestrole, s.getRole());
        assertNull(s.getEmail());
    }

    @Test
    void getFirstName() {
        assertEquals(firstName, s.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals(lastName, s.getLastName());
    }

    @Test
    void getFullName() {
        assertEquals(fullName, s.getFullName());
    }

    @Test
    void getFullNameWhenLoggedOut() {
        s.logoutUser();
        assertEquals(guestfirstName, s.getFullName());
    }

    @Test
    void getEmail() {
        assertEquals(email, s.getEmail());
    }

    @Test
    void getRole() {
        assertEquals(role, s.getRole());
    }

    @Test
    void isLoggedIn() {
        assertTrue(s.isLoggedIn());
    }

    @Test
    void isNotLoggedIn() {
        s.logoutUser();
        assertFalse(s.isLoggedIn());
    }

    @Test
    void setLoggedInTeacherEmail() {
        UserSession.setLoggedInTeacherEmail(email);
        assertEquals(email, UserSession.getLoggedInTeacherEmail());
    }

    @Test
    void setLoggedInStudentEmail() {
        UserSession.setLoggedInStudentEmail(email);
        assertEquals(email, UserSession.getLoggedInStudentEmail());
    }

    @Test
    void clear() {
        UserSession.clear();
        assertNull(UserSession.getLoggedInTeacherEmail());
        assertNull(UserSession.getLoggedInStudentEmail());
    }
}