package com.example.cab302tailproject;

public class User {
    // Define Attributes for a user
    private final String firstName;
    private final String lastName;
    private final String email;
    private String password;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // Getter methods
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

