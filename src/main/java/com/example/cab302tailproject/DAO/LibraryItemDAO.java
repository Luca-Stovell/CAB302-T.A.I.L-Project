package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LibraryItem;
import java.sql.SQLException;
import java.util.List;

public interface LibraryItemDAO {
    void add(LibraryItem item) throws SQLException;
    List<LibraryItem> findByTeacher(int teacherId) throws SQLException;
    void delete(int id) throws SQLException;
}
