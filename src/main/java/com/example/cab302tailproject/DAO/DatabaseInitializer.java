package com.example.cab302tailproject.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private Connection connection;

    public DatabaseInitializer() {
        this.connection = SqliteConnection.getInstance();
    }

    public void initialize(){
        createTeacherTable();
        createStudentTable();
        createClassroomTable();
        try {
            Statement createTable = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void createTeacherTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Teacher ("
                        + "TeacherEmail TEXT PRIMARY KEY UNIQUE NOT NULL,"
                        + "firstName TEXT,"
                        + "lastName TEXT,"
                        + "password TEXT"
                        + ")";
        execute(query);
    }
    private void createStudentTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Student ("
                        + "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "email TEXT UNIQUE NOT NULL, "
                        + "firstName TEXT NOT NULL, "
                        + "lastName TEXT NOT NULL, "
                        + "password TEXT NOT NULL, "
                        + "TeacherEmail TEXT, "
                        + "FOREIGN KEY (TeacherEmail) REFERENCES Teacher(TeacherEmail)"
                        + ")";
        execute(query);
    }
    private void createClassroomTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Classroom ("
                        + "ClassroomID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + "TeacherEmail TEXT NOT NULL, "
                        + "FOREIGN KEY (TeacherEmail) REFERENCES Teacher(TeacherEmail)"
                        + ")";
                execute(query);
    }

    /**
     *Executes a given SQL query using the current database connection.
     * @param query the SQL statement to be executed
     *  */
    private void execute(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
