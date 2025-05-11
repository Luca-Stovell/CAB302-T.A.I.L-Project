package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Classroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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


}
