package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Student;

import java.sql.PreparedStatement;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;



public class SqlStudentDAO implements StudentDAO {

    private final Connection connection = SqliteConnection.getInstance();

    public static String hashPassword(String password) {
        // TODO implement hashing
        return password;
    }
    @Override
    public boolean AddStudent(String email, String firstName, String lastName, String password){
        String hashedPassword = hashPassword(password);
        try {
            String query = "INSERT INTO Student (email, firstName, lastName, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM Student";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("StudentID"); //
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String password = rs.getString("password");

                Student student = new Student(firstName, lastName, email, password);
                student.setStudentID(id);

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }


    @Override
    public boolean checkEmail(String email) {
        try {
            String query =  "SELECT COUNT(1) FROM Student WHERE email = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                int result = resultSet.getInt("COUNT(1)");
                return result == 1;
            }

        } catch (Exception e) {
            e.printStackTrace(); // TODO: fix this warning
        }
        return false;
    }

    public String GetPassword(String email) {
        try {
            String query =  "SELECT password FROM Student WHERE email = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getString("password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null"; // not sure if this will cause problems TODO: fix this
    }

    @Override
    public boolean checkPassword(String email ,String password){
        String actualPassword = GetPassword(email);
        String HPassword = hashPassword(password);
        return actualPassword.equals(HPassword);
    }

    @Override
    public boolean addStudentToClassroom(int studentID, int classroomID) {
        String query = "INSERT OR IGNORE INTO StudentClassroom (StudentID, ClassroomID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public List<Student> getStudentsByClassroomID(int classroomID) {
        List<Student> students = new ArrayList<>();
        String query = """
        SELECT s.* FROM Student s
        JOIN StudentClassroom sc ON s.StudentID = sc.StudentID
        WHERE sc.ClassroomID = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, classroomID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("StudentID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String password = rs.getString("password");

                Student student = new Student(firstName, lastName, email, password);
                student.setStudentID(id);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public boolean removeStudentFromClassroom(int studentID, int classroomID) {
        String query = "DELETE FROM StudentClassroom WHERE StudentID = ? AND ClassroomID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classroomID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

