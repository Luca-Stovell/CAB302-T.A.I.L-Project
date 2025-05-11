package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LessonContent;
import com.example.cab302tailproject.model.Worksheet;

import java.sql.*;

// the controllers for the login/register page should use these
public class ContentDAO implements IContentDAO {
    private Connection connection;

    public ContentDAO() {
        connection = SqliteConnection.getInstance();
        createMaterialTable();
        createLessonTable();
        createWorksheetTable();
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


    private void createLessonTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS lesson ("
                        + "lessonID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "lessonTopic TEXT, "
                        + "lessonContent TEXT, "
                        + "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "TeacherID INTEGER, "
                        + "ClassroomID INTEGER, "
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID), "
                        + "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID), "
                        + "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID)"
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createWorksheetTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS worksheet ("
                        + "worksheetID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "worksheetTopic TEXT, "
                        + "worksheetContent TEXT, "
                        + "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "TeacherID INTEGER, "
                        + "ClassroomID INTEGER, "
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID)"
                        + "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID), "
                        + "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID)"
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
     * Add the content of a new lesson plan to the database
     * @param content The generated text from a lesson plan
     * @return True if lesson is successfully added to the lesson table
     */
    public boolean addLessonContent(LessonContent content) {
        // Ensure the materialID exists by inserting into the material table if necessary
        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial("lesson");
            if (generatedMaterialID == -1) {
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID); // Update the materialID in the content object
        }

        String sql = "INSERT INTO lesson (lessonTopic, lessonContent, lastModifiedDate," +
                "TeacherID, ClassroomID, materialID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, content.getTopic());
            statement.setString(2, content.getContent());
            statement.setTimestamp(3, new Timestamp(content.getLastModifiedDate().getTime()));
            statement.setInt(4, content.getTeacherID());
            statement.setInt(5, content.getClassroomID());
            statement.setInt(6, content.getMaterialID());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds the content of a new worksheet to the database
     * @param content The generated text of a worksheet
     * @return True if lesson is successfully added to the worksheet table
     */
    public boolean addWorksheetContent(Worksheet content) {
        // Ensure the materialID exists by inserting into the material table if necessary
        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial("worksheet");
            if (generatedMaterialID == -1) {
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID); // Update the materialID in the content object
        }

        String sql = "INSERT INTO worksheet (worksheetTopic, worksheetContent, lastModifiedDate," +
                "TeacherID, ClassroomID, materialID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, content.getTopic());
            statement.setString(2, content.getContent());
            statement.setTimestamp(3, new Timestamp(content.getLastModifiedDate().getTime()));
            statement.setInt(4, content.getTeacherID());
            statement.setInt(5, content.getClassroomID());
            statement.setInt(6, content.getMaterialID());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the content of a lesson from the database
     * @param materialID The material identifier of the requested lesson
     * @return the LessonContent object being requested if it exists, otherwise returns null
     */
    public LessonContent getLessonContent(int materialID) {
        String sql = "SELECT * FROM lessonContent WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new LessonContent(
                        rs.getString("topic"),
                        rs.getString("lessonContent"),
                        rs.getTimestamp("lastModifiedDate"),
                        rs.getInt("teacherID"),
                        rs.getInt("classroomID"),
                        rs.getInt("materialID")
                        );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
