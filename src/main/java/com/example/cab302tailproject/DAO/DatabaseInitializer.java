package com.example.cab302tailproject.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private Connection connection;

    /**
     * This function initializes the connection to the database which is declared in the SQLiteConnection class.
     * Handles potential SQLException during connection acquisition.
     */
    public DatabaseInitializer() {
        try {
            this.connection = SqliteConnection.getInstance();
            if (this.connection == null) {
                System.err.println("Failed to establish database connection: getInstance() returned null.");
                throw new RuntimeException("Failed to establish database connection: getInstance() returned null.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection.", e);
        }
    }

    /**
     * This function is called upon launching of the app in the main function and creates all necessary tables which are
     * declared below.
     */
    public void initialize() {
        if (this.connection == null) {
            System.err.println("Cannot initialize tables: database connection was not established.");
            return;
        }
        createTeacherTable();
        createStudentTable();
        createClassroomTable();
        createLibraryItemTable();
        createStudentClassroomTable();
        createStudentTeacherTable();
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
     * This function creates the Student table in the SQLite database with StudentID being the primary key.
     */
    private void createStudentTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Student (" +
                        "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "firstName TEXT NOT NULL, " +
                        "lastName TEXT NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "TeacherEmail TEXT, " + // This implies a student is linked to one teacher directly
                        "FOREIGN KEY (TeacherEmail) REFERENCES Teacher(TeacherEmail)" +
                        ")";
        execute(query);
    }

    /**
     * This function creates the Classroom table in the SQLite database with ClassroomID being the primary key.
     */
    private void createClassroomTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS Classroom ("
                        + "ClassroomID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + "TeacherEmail TEXT, " // This links a classroom to one teacher
                        + "FOREIGN KEY (TeacherEmail) REFERENCES Teacher(TeacherEmail)"
                        + ")";
        execute(query);
    }

    private void createLibraryItemTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS library_item (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                teacher_id    INTEGER,
                stored_name   TEXT NOT NULL,
                original_name TEXT NOT NULL,
                size          INTEGER,
                uploaded_at   TEXT,
                FOREIGN KEY (teacher_id) REFERENCES Teacher(TeacherID)
            );
            """;
        execute(sql);
    }

    private void createStudentClassroomTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS StudentClassroom (" +
                        "StudentID INTEGER NOT NULL, " +
                        "ClassroomID INTEGER NOT NULL, " +
                        "PRIMARY KEY (StudentID, ClassroomID), " +
                        "FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE, " +
                        "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID) ON DELETE CASCADE" +
                        ")";
        execute(query);
    }

    private void createStudentTeacherTable() {
        String query = """
        CREATE TABLE IF NOT EXISTS StudentTeacher (
            StudentID INTEGER NOT NULL,
            TeacherID INTEGER NOT NULL,
            PRIMARY KEY (StudentID, TeacherID),
            FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
            FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID) ON DELETE CASCADE
        );
    """;
        execute(query);
    }


    /**
     *Executes a given SQL query using the current database connection.
     * @param query the SQL statement to be executed
     * */
    private void execute(String query) {

        if (this.connection == null) {
            System.err.println("Cannot execute query, database connection is not initialized: " + query);
            return;
        }
        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            System.err.println("Error executing query [" + query + "]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
