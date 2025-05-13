package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;
import java.util.List;

public interface StudentDAO extends UserDAO {

    boolean AddStudent(String email, String firstName, String lastName, String password);

    List<Student> getAllStudents();

    boolean checkEmail(String email);

    boolean checkPassword(String email, String password);

    List<Student> getStudentsByClassroomID(int classroomID);

    boolean addStudentToClassroom(int studentID, int classroomID);

    boolean removeStudentFromClassroom(int studentID, int classroomID);

}
