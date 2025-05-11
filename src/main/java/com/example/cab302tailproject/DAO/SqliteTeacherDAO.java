package com.example.cab302tailproject.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class SqliteTeacherDAO implements TeacherDAO {
    private final Connection connection = SqliteConnection.getInstance();

    public static String hashPassword(String password) {
        // TODO implement hashing
        return password;
    }

    @Override
    public boolean AddTeacher(String email, String firstName, String lastName, String password){
        String hashedPassword = hashPassword(password);
        try {
            String query = "INSERT INTO Teacher (TeacherEmail, firstName, lastName, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, hashedPassword);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Changes an account's password.
     * Remember to hash the password before calling this function
     * @param email email of the existing account
     * @param newPassword The new password (hashed)
     * @return true is successful, false if not
     */

    @Override
    public boolean ChangePassword(String email, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        try {
            String query  = "UPDATE Teacher SET password = ? WHERE TeacherEmail = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            int linesChanged = statement.executeUpdate();
            if (linesChanged == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean checkEmail(String email) {
        try {
            String query =  "SELECT COUNT(1) FROM Teacher WHERE TeacherEmail = ?;";
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
            String query =  "SELECT password FROM Teacher WHERE TeacherEmail = ?;";
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

    public boolean checkPassword(String email ,String password){
        String actualPassword = GetPassword(email);
        String HPassword = hashPassword(password);
        return actualPassword.equals(HPassword);
    }

    @Override
    public void createClassroom(String ClassID, String Teacher) {
        try {
            String query = "INSERT INTO Classroom (ClassID, TeacherEmail) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ClassID);
            statement.setString(2, Teacher);
            int rowsInserted = statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
