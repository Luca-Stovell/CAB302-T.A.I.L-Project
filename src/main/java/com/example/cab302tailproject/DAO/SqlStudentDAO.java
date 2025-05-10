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

                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                Student student = new Student(firstName, lastName, email, null);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Student s : students) {
            System.out.println(s.getFirstName() + " " + s.getLastName() + " | " + s.getEmail());
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
            e.printStackTrace(); // TODO: fix this warning
        }
        return "null"; // not sure if this will cause problems TODO: fix this
    }

    @Override
    public boolean checkPassword(String email ,String password){
        String actualPassword = GetPassword(email);
        String HPassword = hashPassword(password);
        return actualPassword.equals(HPassword);
    }
}

