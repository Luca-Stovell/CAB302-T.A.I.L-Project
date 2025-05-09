package com.example.cab302tailproject.model;

import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private int classroomID;
    private Teacher teacher;
    private List<Student> students;

    /**
     * Constructor for a classroom.
     * @param classroomID The Primary Key in the database
     * @param teacher is the teacher which is assigned to the classroom.
     */
    public Classroom(int classroomID, Teacher teacher) {
        this.classroomID = classroomID;
        this.teacher = teacher;
        this.students = new ArrayList<>();
    }


    public int getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
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

    // Optional: display classroom info
    @Override
    public String toString() {
        return "Classroom ID: " + classroomID + ", Teacher: " + teacher.getName() + ", Students: " + students.size();
    }
}
