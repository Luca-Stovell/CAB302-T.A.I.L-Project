package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
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
            try (PreparedStatement statement_retrieveID = connection.prepareStatement(sqlLastInsertId);
                 ResultSet rs = statement_retrieveID.executeQuery()) {
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
     * @return materialID of the successful entry to lesson table, -1 if unsuccessful
     */
    public int addLessonContent(Lesson content) {
        String sql = "INSERT INTO lesson (lessonTopic, lessonContent, " +
                "TeacherID, ClassroomID, materialID) " +
                "VALUES (?, ?, ?, ?, ?)";
        String sqlRetrieveDate = "SELECT lastModifiedDate FROM lesson WHERE rowid = last_insert_rowid()";
        String sqlUpdateDate = "UPDATE lesson SET lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') " +
                "WHERE rowid = last_insert_rowid()";


        // Ensure the materialID exists by inserting into the material table if necessary
        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial("lesson");
            if (generatedMaterialID == -1) {
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID); // Update the materialID in the content object
        }


        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, content.getTopic());
            statement.setString(2, content.getContent());
            statement.setInt(3, content.getTeacherID());
            statement.setInt(4, content.getClassroomID());
            statement.setInt(5, content.getMaterialID());
            statement.executeUpdate();

            // Update the lastModifiedDate with '+10 hours' offset
            try (PreparedStatement statement_updateTimeZone = connection.prepareStatement(sqlUpdateDate)) {
                statement_updateTimeZone.executeUpdate();
            }


            // Retrieve generated timestamp
            try (PreparedStatement statement_retrieveDate = connection.prepareStatement(sqlRetrieveDate);
                 ResultSet rs = statement_retrieveDate.executeQuery()) {
                     if (rs.next()) {
                         Timestamp lastModified = rs.getTimestamp("lastModifiedDate");
                         if (lastModified != null) {
                             content.setLastModifiedDate(lastModified.toInstant());
                         }
                     }
            }
            return content.getMaterialID();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Adds the content of a new worksheet to the database
     * @param content The generated text of a worksheet
     * @return materialID of the successful entry to worksheet table, -1 if unsuccessful
     */
    public int addWorksheetContent(Worksheet content) {
        String sql = "INSERT INTO worksheet (worksheetTopic, worksheetContent, " +
                "TeacherID, ClassroomID, materialID) " +
                "VALUES (?, ?, ?, ?, ?)";
        String sqlRetrieveDate = "SELECT lastModifiedDate FROM worksheet WHERE rowid = last_insert_rowid()";
        String sqlUpdateDate = "UPDATE worksheet SET lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') " +
                "WHERE rowid = last_insert_rowid()";

        // Ensure the materialID exists by inserting into the material table if necessary
        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial("worksheet");
            if (generatedMaterialID == -1) {
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID); // Update the materialID in the content object
        }


        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, content.getTopic());
            statement.setString(2, content.getContent());
            statement.setInt(3, content.getTeacherID());
            statement.setInt(4, content.getClassroomID());
            statement.setInt(5, content.getMaterialID());
            statement.executeUpdate();

            // Update the lastModifiedDate with '+10 hours' offset
            try (PreparedStatement statement_updateTimeZone = connection.prepareStatement(sqlUpdateDate)) {
                statement_updateTimeZone.executeUpdate();
            }

            // Retrieve generated timestamp
            try (PreparedStatement statement_retrieveDate = connection.prepareStatement(sqlRetrieveDate);
                 ResultSet rs = statement_retrieveDate.executeQuery()) {
                if (rs.next()) {
                    Timestamp lastModified = rs.getTimestamp("lastModifiedDate");
                    if (lastModified != null) {
                        content.setLastModifiedDate(lastModified.toInstant());
                    }
                }
            }
            return content.getMaterialID();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Retrieves the content of a lesson from the database
     * @param materialID The material identifier of the requested lesson
     * @return the LessonContent object being requested if it exists, otherwise returns null
     */
    public Lesson getLessonContent(int materialID) {
        String sql = "SELECT * FROM lesson WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new Lesson(
                        rs.getString("lessonTopic"),
                        rs.getString("lessonContent"),
                        rs.getTimestamp("lastModifiedDate") != null
                                ? rs.getTimestamp("lastModifiedDate").toInstant()
                                : null ,    // for null case of timestamp
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

    public Material getMaterialType(int materialID) {
        String sql = "SELECT materialType FROM material WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new Material(materialID,
                        result.getString("materialType"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Worksheet getWorksheetContent(int materialID) {
        String sql = "SELECT * FROM worksheet WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new Worksheet(
                        rs.getString("worksheetTopic"),
                        rs.getString("worksheetContent"),
                        rs.getTimestamp("lastModifiedDate") != null
                                ? rs.getTimestamp("lastModifiedDate").toInstant()
                                : null ,    // for null case of timestamp
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
