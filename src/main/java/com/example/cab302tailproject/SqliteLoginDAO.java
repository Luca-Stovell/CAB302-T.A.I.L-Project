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
        String query =
                  "CREATE TABLE IF NOT EXISTS user ("
                + "userID INTEGER PRIMARY KEY AUTOINCREMENT, /* Possibly redundant, email is unique */"
                + "userName TEXT UNIQUE,"
                + "password TEXT,"
                + "role INTEGER /* substitute for enum, could also use TEXT. Also TODO decide if this actually exists*/"
                + ");";

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
            String query =  "SELECT COUNT(1) AS check" +
                            "FROM user" +
                            "WHERE userName = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                int result = resultSet.getInt("check");
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
                String query =  "SELECT password" +
                        "FROM user" +
                        "WHERE userName = ?;";
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
    public boolean AddAccount(String email, String password, int role) {
        try {
            String query = "INSERT INTO user (userName, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setInt(3, role);
            statement.executeUpdate();
            return true;
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
        try {
            String query  = "UPDATE user SET password = ? WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newPassword);
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
}
