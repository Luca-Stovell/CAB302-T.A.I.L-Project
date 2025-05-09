package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LessonContent;
import java.sql.*;

// the controllers for the login/register page should use these
public class LessonContentDAO implements ILessonContentDAO {
    private Connection connection;

    public LessonContentDAO() {
        connection = SqliteConnection.getInstance();
        createTable();
    }

    private void createTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS lessonContent ("
                        + "materialID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "lessonContent TEXT, "
                        + "lastModifiedDate TIMESTAMP"
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID)"
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds lesson content to the database
     * @param content The generated text from a lesson plan
     * @return True if lesson is successfully added to the lessonContent table
     */
    public boolean addLessonContent(LessonContent content) {
        String sql = "INSERT INTO lessonContent (materialID, lessonContent, lastModifiedDate) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, content.getMaterialID());
            statement.setString(2, content.getContent());
            statement.setTimestamp(3, new Timestamp(content.getLastModifiedDate().getTime()));
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LessonContent getLessonContent(int materialID) {
        String sql = "SELECT * FROM lessonContent WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new LessonContent(
                        rs.getInt("materialID"),
                        rs.getString("lessonContent"),
                        rs.getTimestamp("lastModifiedDate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
