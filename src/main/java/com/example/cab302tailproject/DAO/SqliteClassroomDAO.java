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
                System.err.println("Failed to establish database connection in SqliteClassroomDAO: getInstance() returned null or closed connection.");
                throw new RuntimeException("Failed to establish database connection in SqliteClassroomDAO.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection in SqliteClassroomDAO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection in SqliteClassroomDAO.", e);
        }
    }

    /**
     * Checks if the current database connection is valid.
     * @throws SQLException if the connection is null or closed.
     */
    private void checkConnection() throws SQLException {
        if (this.connection == null) {
            throw new SQLException("Database connection is not initialized in SqliteClassroomDAO.");
        }
        if (this.connection.isClosed()) {
            throw new SQLException("Database connection is closed in SqliteClassroomDAO.");
        }
    }

    @Override
    public boolean createClassroom(Classroom classroom) {
        try {
            checkConnection(); // Check connection status

            String insertSql = "INSERT INTO Classroom (TeacherEmail) VALUES (?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, classroom.getTeacher());
                int rows = insertStmt.executeUpdate();

                if (rows == 1) {
                    try (Statement idStmt = connection.createStatement();
                         ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                        if (rs.next()) {
                            classroom.setClassroomID(rs.getInt(1));
                            System.out.println("Classroom created with ID: " + classroom.getClassroomID());
                            return true;
                        } else {
                            System.err.println("Failed to retrieve generated ClassroomID using last_insert_rowid().");
                        }
                    }
                } else {
                    System.err.println("Classroom insert affected 0 rows.");
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
        try {
            checkConnection();

            String query = "SELECT ClassroomID, TeacherEmail FROM Classroom WHERE TeacherEmail = ?";
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
        } catch (SQLException e) {
            System.err.println("Error getting classrooms by teacher email: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
