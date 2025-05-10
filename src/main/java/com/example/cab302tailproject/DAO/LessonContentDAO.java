package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LessonContent;
import java.sql.*;

// the controllers for the login/register page should use these
public class LessonContentDAO implements ILessonContentDAO {
    private Connection connection;

    public LessonContentDAO() {
        connection = SqliteConnection.getInstance();
        createMaterialTable();
        createTable();
        try {
            // Example: Replace these values with your actual database credentials
            String url = "jdbc:sqlite:Tail.db"; // Change your URL and DB name here


            // Initialize the database connection
            connection = DriverManager.getConnection(url);

            System.out.println("Database connection established.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }

    }

    private void createMaterialTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS material ("
                        + "materialID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "materialType TEXT NOT NULL"
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS lessonContent ("
                        + "lessonID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "materialID INTEGER NOT NULL, "
                        + "lessonContent TEXT, "
                        + "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID)"
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an entry to the material table and retrieves the auto-generated ID. Required for adding to child tables.
     * @param materialType The type of material to be added.
     * @return The auto-generated materialID, or -1 if the operation fails.
     */
    public int addMaterial(String materialType) {
        String sqlInsert = "INSERT INTO material (materialType) VALUES (?)";
        String sqlLastInsertId = "SELECT last_insert_rowid()";

        try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
            statement.setString(1, materialType);
            statement.executeUpdate();

            // Retrieve the last inserted ID
            try (PreparedStatement stmt = connection.prepareStatement(sqlLastInsertId);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // The generated materialID
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Error case, failed to insert
    }


    /**
     * Adds lesson content to the database
     * @param content The generated text from a lesson plan
     * @return True if lesson is successfully added to the lessonContent table
     */
    public boolean addLessonContent(LessonContent content) {
        // Ensure the materialID exists by inserting into the material table if necessary
        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial("lesson"); // Change "DefaultType" as needed
            if (generatedMaterialID == -1) {
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID); // Update the materialID in the content object
        }

        String sql = "INSERT INTO lessonContent (materialID, lessonContent, lastModifiedDate) " +
                "VALUES (?, ?, ?)";
        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }

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
