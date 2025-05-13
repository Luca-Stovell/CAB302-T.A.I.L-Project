package com.example.cab302tailproject.model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {

    private List<Student> students;
    private List<Classroom> classrooms; // ✅ New field
    private Teacher teacher;

    public Teacher(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        this.students = new ArrayList<>();
        this.classrooms = new ArrayList<>();
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }


    // ✅ Add a classroom to this teacher
    public void addClassroom(Classroom classroom) {
        classrooms.add(classroom);
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    // Optional: add methods to manage students too
    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}

