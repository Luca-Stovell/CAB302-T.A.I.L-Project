package com.example.cab302tailproject.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a classroom in the system.
 * Each classroom is associated with a teacher and a list of students.
 * Classrooms are also tracked statically in a shared list.
 */
public class Classroom {

    /** Unique identifier for the classroom (set by database or manually). */
    private int classroomID;

    /** Email of the teacher associated with this classroom. */
    private String teacher;

    /** List of students enrolled in this classroom. */
    private List<Student> students;

    /** Static list to track all Classroom instances created in memory. */
    private static List<Classroom> classrooms = new ArrayList<>();

    /**
     * Constructs a new Classroom associated with the specified teacher's email.
     * Automatically adds this classroom to the static list of all classrooms.
     *
     * @param teacherEmail The email of the teacher who owns this classroom.
     */
    public Classroom(String teacherEmail) {
        this.teacher = teacherEmail;
        this.students = new ArrayList<>();
        classrooms.add(this);
    }

    /**
     * Gets the static list of all Classroom instances.
     *
     * @return A list of all classrooms.
     */
    public static List<Classroom> getClassrooms() {
        return classrooms;
    }

    /**
     * Gets the unique ID of this classroom.
     *
     * @return The classroom ID.
     */
    public int getClassroomID() {
        return classroomID;
    }

    /**
     * Sets the unique ID of this classroom.
     *
     * @param classroomID The ID to assign.
     */
    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
    }

    /**
     * Gets the email of the teacher assigned to this classroom.
     *
     * @return The teacher's email.
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * Sets the email of the teacher assigned to this classroom.
     *
     * @param teacher The teacher's email.
     */
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    /**
     * Gets the list of students enrolled in this classroom.
     *
     * @return A list of {@link Student} objects.
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Adds a student to this classroom.
     *
     * @param student The student to add.
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /**
     * Removes a student from this classroom.
     *
     * @param student The student to remove.
     */
    public void removeStudent(Student student) {
        students.remove(student);
    }

    /**
     * Returns a string representation of this classroom.
     * Primarily used for UI display purposes.
     *
     * @return A string like "Classroom 101".
     */
    @Override
    public String toString() {
        return "Classroom " + classroomID;
    }
}
