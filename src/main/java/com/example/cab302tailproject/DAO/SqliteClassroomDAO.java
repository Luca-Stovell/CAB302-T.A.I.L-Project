package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Classroom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteClassroomDAO implements ClassroomDAO {
    private Connection connection;

    /**
     * Constructor for SqliteClassroomDAO.
     * Initializes the database connection.
     * If the connection fails, it throws a RuntimeException.
     */
    public SqliteClassroomDAO() {
        try {
            this.connection = SqliteConnection.getInstance();
            if (this.connection == null || this.connection.isClosed()) {
                // This case should ideally not happen if getInstance throws SQLException on failure
                System.err.println("Failed to establish database connection in SqliteClassroomDAO: getInstance() returned null or closed connection.");
                throw new RuntimeException("Failed to establish database connection in SqliteClassroomDAO.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection in SqliteClassroomDAO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection in SqliteClassroomDAO.", e);
        }
    }

    @Override
    public boolean createClassroom(Classroom classroom) {
        // Ensure connection is valid before proceeding
        if (this.connection == null) {
            System.err.println("Cannot create classroom: database connection is not initialized.");
            return false;
        }
        try {
            // Check if connection is closed before using it
            if (this.connection.isClosed()) {
                System.err.println("Cannot create classroom: database connection is closed.");
                return false;
            }

            String insertSql = "INSERT INTO Classroom (TeacherEmail) VALUES (?)";
            // Use try-with-resources for PreparedStatement and Statement
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, classroom.getTeacher());
                int rows = insertStmt.executeUpdate();

                if (rows == 1) {
                    // Get the last inserted row ID (ClassroomID)
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            classroom.setClassroomID(generatedKeys.getInt(1));
                        } else {
                            System.err.println("Failed to retrieve generated ClassroomID after insert.");
                            // Fallback to last_insert_rowid() if getGeneratedKeys doesn't work as expected
                            try (Statement idStmt = connection.createStatement();
                                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                                if (rs.next()) {
                                    classroom.setClassroomID(rs.getInt(1));
                                } else {
                                    System.err.println("Failed to retrieve generated ClassroomID using last_insert_rowid().");
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating classroom: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Classroom> getClassroomsByTeacherEmail(String email) {
        List<Classroom> result = new ArrayList<>();
        if (this.connection == null) {
            System.err.println("Cannot get classrooms: database connection is not initialized.");
            return result; // Return empty list
        }

        try {
            // Check if connection is closed before using it
            if (this.connection.isClosed()) {
                System.err.println("Cannot get classrooms: database connection is closed.");
                return result;
            }
            String query = "SELECT ClassroomID, TeacherEmail FROM Classroom WHERE TeacherEmail = ?";
            // Use try-with-resources for PreparedStatement
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("ClassroomID");
                    String teacherEmail = rs.getString("TeacherEmail");
                    Classroom classroom = new Classroom(teacherEmail);
                    classroom.setClassroomID(id);
                    result.add(classroom);
                }
            }
        } catch (SQLException e) { // Catch SQLException specifically
            System.err.println("Error getting classrooms by teacher email: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
