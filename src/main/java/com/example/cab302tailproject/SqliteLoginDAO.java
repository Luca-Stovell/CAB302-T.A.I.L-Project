package com.example.cab302tailproject;

import java.sql.*;
// TODO: test all of these methods Manually
// the controllers for the login/register page should use these
public class SqliteLoginDAO implements ILoginDAO{
    private Connection connection;

    public SqliteLoginDAO() {
        connection = SqliteConnection.getInstance();
        createTable();

    }
    private void createTable() {
            createTeacherTable();
            createStudentTable();
        try {
            Statement createTable = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTeacherTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Teacher ("
                        + "TeacherID INTEGER PRIMARY KEY AUTOINCREMENT, /* Possibly redundant, email is unique */"
                        + "email TEXT UNIQUE,"
                        + "firstName TEXT,"
                        + "lastName TEXT,"
                        + "password TEXT"
                        + ")";
        try {
            Statement createTable = connection.createStatement();
            createTable.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void createStudentTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Student ("
                        + "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "email TEXT UNIQUE NOT NULL, "
                        + "firstName TEXT NOT NULL, "
                        + "lastName TEXT NOT NULL, "
                        + "password TEXT NOT NULL, "
                        + "teacherID INTEGER, "
                        + "FOREIGN KEY (teacherID) REFERENCES Teacher(TeacherID)"
                        + ")";
        try {
            Statement createTable = connection.createStatement();
            createTable.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean CheckEmail(String email) {
        try {
            String query =  "SELECT COUNT(1) FROM Teacher WHERE email = ?;";
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

    // Remember to never run this when the email isn't in the database
    // It'll probably just return nothing, but it isn't designed to be able to do that
    @Override
    public String GetPassword(String email) {
            try {
                String query =  "SELECT password FROM Teacher WHERE email = ?;";
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
    public boolean AddAccount(String email, String firstName, String lastName, String password) {
        try {
            AddStudent(email, firstName, lastName, password);
            AddTeacher(email, firstName, lastName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
    public boolean AddTeacher(String email, String firstName, String lastName, String password){
        String hashedPassword = hashPassword(password);
        try {
            String query = "INSERT INTO Teacher (email, firstName, lastName, password) VALUES (?, ?, ?, ?)";
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
            String query  = "UPDATE Teacher SET password = ? WHERE email = ?";
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
    private static String hashPassword(String password){
        // TODO implement hashing
        return password;
    }
}
