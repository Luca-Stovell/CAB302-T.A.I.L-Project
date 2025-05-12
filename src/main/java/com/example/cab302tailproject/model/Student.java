package com.example.cab302tailproject.model;

public class Student extends User {
    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    public Student(String firstName, String lastName, String email) {
        this(firstName, lastName, email, null);
    }

}
