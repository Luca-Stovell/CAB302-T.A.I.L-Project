package com.example.cab302tailproject.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private Connection connection;

    /**
     * This function initializes the connection to the database which is declared in the SQLiteConnection class.
     */

    public DatabaseInitializer() {
        this.connection = SqliteConnection.getInstance();
    }

    /**
     * This funciton is called upon lauching of the app in the main function and creates all necessary tables which are
     * declared below
     */
    public void initialize(){
        createTeacherTable();
        createStudentTable();
        createClassroomTable();
        createStudentClassroomTable();
        createStudentTeacherTable();
        try {
            Statement createTable = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function creates the teacher table in the SQLite database with TeacherID being the primary key.
     */
    private void createTeacherTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Teacher ("
                        + "TeacherID INTEGER PRIMARY KEY AUTOINCREMENT, /* Possibly redundant, email is unique */"
                        + "TeacherEmail TEXT UNIQUE,"
                        + "firstName TEXT,"
                        + "lastName TEXT,"
                        + "password TEXT"
                        + ")";
        execute(query);
    }
    /**
     * This function creates the Student table in the SQLite database with TeacherID being the primary key.
     */
    private void createStudentTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Student (" +
                        "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "firstName TEXT NOT NULL, " +
                        "lastName TEXT NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "TeacherEmail TEXT, " +
                        "FOREIGN KEY (TeacherEmail) REFERENCES Teacher(TeacherEmail)" +
                        ")";
        execute(query);
    }
    /**
     * This function creates the Classroom table in the SQLite database with TeacherID being the primary key.
     */
    private void createClassroomTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Classroom ("
                        + "ClassroomID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + "TeacherEmail TEXT, "
                        + "FOREIGN KEY (TeacherEmail) REFERENCES Teacher(TeacherEmail)"
                        + ")";
                execute(query);
    }

    private void createStudentClassroomTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS StudentClassroom (" +
                        "StudentID INTEGER NOT NULL, " +
                        "ClassroomID INTEGER NOT NULL, " +
                        "PRIMARY KEY (StudentID, ClassroomID), " +
                        "FOREIGN KEY (StudentID) REFERENCES Student(StudentID), " +
                        "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID)" +
                        ")";
        execute(query);
    }
    private void createStudentTeacherTable() {
        String query = """
        CREATE TABLE IF NOT EXISTS StudentTeacher (
            StudentID INTEGER NOT NULL,
            TeacherID INTEGER NOT NULL,
            PRIMARY KEY (StudentID, TeacherID),
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID),
            FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID)
        );
    """;
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
