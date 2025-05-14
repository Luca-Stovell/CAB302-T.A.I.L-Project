package com.example.cab302tailproject.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a teacher in the system.
 * A teacher extends the {@link User} class and is associated with multiple classrooms and students.
 */
public class Teacher extends User {

    private List<Student> students;
    private List<Classroom> classrooms;
    private Teacher teacher; // Possibly used for representing mentor or linked teacher, if needed

    /**
     * Constructs a new Teacher with the given user details.
     *
     * @param firstName The teacher's first name.
     * @param lastName  The teacher's last name.
     * @param email     The teacher's email address.
     * @param password  The teacher's password (should be hashed/stored securely elsewhere).
     */
    public Teacher(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        this.students = new ArrayList<>();
        this.classrooms = new ArrayList<>();
    }

    /**
     * Gets the associated teacher reference (if applicable).
     * Could be used for representing mentorship or hierarchical roles.
     *
     * @return The associated {@code Teacher}, or null if not set.
     */
    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * Sets an associated teacher reference.
     *
     * @param teacher The {@code Teacher} to associate with this teacher.
     */
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    /**
     * Adds a classroom to this teacher's list of classrooms.
     *
     * @param classroom The {@link Classroom} to add.
     */
    public void addClassroom(Classroom classroom) {
        classrooms.add(classroom);
    }

    /**
     * Returns the list of classrooms associated with this teacher.
     *
     * @return A list of {@link Classroom} objects.
     */
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    /**
     * Replaces the list of classrooms associated with this teacher.
     *
     * @param classrooms A new list of {@link Classroom} objects.
     */
    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    /**
     * Returns the list of students associated with this teacher.
     *
     * @return A list of {@link Student} objects.
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Replaces the list of students associated with this teacher.
     *
     * @param students A new list of {@link Student} objects.
     */
    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
