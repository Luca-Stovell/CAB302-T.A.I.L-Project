package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Classroom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteClassroomDAO implements ClassroomDAO {
    private final Connection connection = SqliteConnection.getInstance();

    @Override
    public boolean createClassroom(Classroom classroom) {
        try {
            // Insert the classroom
            String insertSql = "INSERT INTO Classroom (TeacherEmail) VALUES (?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setString(1, classroom.getTeacher());
            int rows = insertStmt.executeUpdate();

            if (rows == 1) {
                // Now get the last inserted row ID
                Statement idStmt = connection.createStatement();
                ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()");
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    classroom.setClassroomID(generatedId); // ✅ Set the ID
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Classroom> getClassroomsByTeacherEmail(String email) {
        List<Classroom> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM Classroom WHERE TeacherEmail = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ClassroomID");
                String teacherEmail = rs.getString("TeacherEmail");
                Classroom classroom = new Classroom(teacherEmail);
                classroom.setClassroomID(id);
                result.add(classroom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
