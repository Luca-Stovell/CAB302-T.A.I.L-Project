package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;

import java.util.List;

public interface StudentDAO extends UserDAO {

    public boolean AddStudent(String email, String firstName, String lastName, String password);

    public List<Student> getAllStudents();

    boolean checkEmail(String email);
    /**
     * Checks an input password against the stored password of an account
     * @param email email of the account being checked
     * @param password the input password
     * @return true if passwords match, false if not
     */
    public boolean checkPassword(String email ,String password);

}
