package com.example.cab302tailproject.model;

import java.util.List;

public class Teacher extends User {

    private List<Student> students;

    public Teacher(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    public void login(){

    }

}
