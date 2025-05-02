package com.example.cab302tailproject.model;

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
    // Getter methods
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void login(){

    }
    public void logout(){

    }
}

