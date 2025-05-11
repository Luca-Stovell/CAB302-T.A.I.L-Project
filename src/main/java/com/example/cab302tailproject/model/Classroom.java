package com.example.cab302tailproject.model;

import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private int classroomID;
    private String teacher;
    private List<Student> students;
    private static List<Classroom> classrooms = new ArrayList<>();

    /**
     * This is the constructor for the Classroom
     * TODO May have to change implimentation to reflect the students that are loaded yet to figure out.
     * @param teacherEmail
     */
    public Classroom(String teacherEmail) {
        this.teacher = teacherEmail;
        this.students = new ArrayList<>();
        classrooms.add(this);
    }

    public static List<Classroom> getClassrooms() {
        return classrooms;
    }

    public int getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    // Add a student
    public void addStudent(Student student) {
        students.add(student);
    }

    // Remove a student
    public void removeStudent(Student student) {
        students.remove(student);
    }
}
