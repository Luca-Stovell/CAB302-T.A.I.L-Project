package com.example.cab302tailproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final String firstName = "John";
    private static final String lastName = "Smith";
    private static final String email = "js@gmail.com";
    private static final String password = "19513fdc9da4fb72a4a05eb66917548d3c90ff94d5419e1f2363eea89dfee1dd";

    User u;

    @BeforeEach
    void setup(){
        u = new User(firstName, lastName, email, password);
    }

    @Test
    void getFirstName() {
        assertEquals(firstName,u.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals(lastName,u.getLastName());
    }

    @Test
    void getEmail() {
        assertEquals(email,u.getEmail());
    }

    @Test
    void getPassword() {
        assertEquals(password,u.getPassword());
    }
}