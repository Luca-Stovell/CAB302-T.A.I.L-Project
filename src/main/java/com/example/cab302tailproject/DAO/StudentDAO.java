package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import java.util.List;

public interface StudentDAO extends UserDAO {

    /**
     * Adds a new student to the database.
     * @param email Student's email.
     * @param firstName Student's first name.
     * @param lastName Student's last name.
     * @param password Student's plain text password (to be hashed by implementation).
     * @return true if the student was added successfully, false otherwise.
     */
    boolean AddStudent(String email, String firstName, String lastName, String password);
    /**
     * Retrieves a list of all students.
     * @return A list of {@link Student} objects.
     */
    List<Student> getAllStudents(); // For analytics page, if needed by other parts

    boolean checkEmail(String email);
    /**
     * Checks an input password against the stored password of an account
     * @param email email of the account being checked
     * @param password the input password
     * @return true if passwords match, false if not
     */
    public boolean checkPassword(String email ,String password);

}
