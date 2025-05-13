package com.example.cab302tailproject.model;

public class Student extends User {

    private int studentID;
    private String teacherEmail;
    private int classroomID;

    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    public Student(String firstName, String lastName, String email) {
        this(firstName, lastName, email, null);
    }

    // Getters and Setters
    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public int getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
    }
    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

}

