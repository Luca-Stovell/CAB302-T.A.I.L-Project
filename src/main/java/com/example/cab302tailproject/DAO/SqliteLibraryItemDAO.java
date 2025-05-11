package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LibraryItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqliteLibraryItemDAO implements LibraryItemDAO {

    private Connection conn() throws SQLException {
        return SqliteConnection.getInstance();
    }

    @Override
    public void add(LibraryItem item) throws SQLException {
        String sql = """
            INSERT INTO library_item
              (teacher_id, stored_name, original_name, size, uploaded_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt   (1, item.getTeacherId());
            ps.setString(2, item.getStoredName());
            ps.setString(3, item.getOriginalName());
            ps.setLong  (4, item.getSize());
            ps.setString(5, item.getUploadedAt().toString());
            ps.executeUpdate();
        }
    }

    @Override
    public List<LibraryItem> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT * FROM library_item WHERE teacher_id = ? ORDER BY uploaded_at DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            List<LibraryItem> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new LibraryItem(
                        rs.getInt("id"),
                        rs.getInt("teacher_id"),
                        rs.getString("stored_name"),
                        rs.getString("original_name"),
                        rs.getLong("size"),
                        LocalDateTime.parse(rs.getString("uploaded_at"))
                ));
            }
            return list;
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM library_item WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
