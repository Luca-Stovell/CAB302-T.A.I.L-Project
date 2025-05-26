package com.example.cab302tailproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailTest {
    private static final String firstName = "John";
    private static final String lastName = "Smith";

    private UserDetail u;

    @BeforeEach
    void setUp() {
        u = new UserDetail(firstName, lastName);
    }

    @Test
    void firstName() {
        assertEquals(firstName, u.firstName());
    }

    @Test
    void lastName() {
        assertEquals(lastName, u.lastName());
    }
}