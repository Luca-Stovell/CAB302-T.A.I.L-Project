package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContentDAO implements IContentDAO {
    //<editor-fold desc="Initialisation">
    // private Connection connection; // Member variable for connection, can be removed if all methods fetch their own

    /**
     * Constructor for the ContentDAO class.
     * Initializes the database connection using a singleton approach provided by the
     * SqliteConnection class. Additionally, it creates the necessary database tables
     * ("material", "lesson", "worksheet", "learningCard", and "StudentCardResponse")
     * if they do not already exist.
     */
    public ContentDAO() {
        createMaterialTable();
        createLessonTable();
        createWorksheetTable();
        createLearningCardTable();
        createStudentCardResponseTable();
    }
    //</editor-fold>

    //<editor-fold desc="Table creation">
    private void createMaterialTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS material ("
                        + "materialID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "materialType TEXT NOT NULL, "
                        + "week INTEGER, "
                        + "ClassroomID INTEGER, "
                        + "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID)"
                        + ")";
        try (Connection conn = SqliteConnection.getInstance();
             Statement statement = conn.createStatement()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Error creating material table: Database connection is closed or null.");
                return;
            }
            statement.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating material table: " + e.getMessage());
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
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID), "
                        + "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID)"
                        + ")";
        try (Connection conn = SqliteConnection.getInstance();
             Statement statement = conn.createStatement()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Error creating lesson table: Database connection is closed or null.");
                return;
            }
            statement.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating lesson table: " + e.getMessage());
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
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID), "
                        + "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID)"
                        + ")";
        try (Connection conn = SqliteConnection.getInstance();
             Statement statement = conn.createStatement()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Error creating worksheet table: Database connection is closed or null.");
                return;
            }
            statement.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating worksheet table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createLearningCardTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS learningCard ("
                        + "learningCardID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "learningCardTopic TEXT, "
                        + "learningCardContent TEXT, "
                        + "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "TeacherID INTEGER, "
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID), "
                        + "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID)"
                        + ")";
        try (Connection conn = SqliteConnection.getInstance();
             Statement statement = conn.createStatement()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Error creating learningCard table: Database connection is closed or null.");
                return;
            }
            statement.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating learningCard table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createStudentCardResponseTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS StudentCardResponse ("
                        + "ResponseID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "StudentID INTEGER NOT NULL, "
                        + "MaterialID INTEGER NOT NULL, "
                        + "CardQuestion TEXT NOT NULL, "
                        + "IsCorrect BOOLEAN NOT NULL, "
                        + "ClassroomID INTEGER, "
                        + "FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE, "
                        + "FOREIGN KEY (MaterialID) REFERENCES Material(MaterialID) ON DELETE CASCADE, "
                        + "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID) ON DELETE SET NULL"
                        + ")";
        try (Connection conn = SqliteConnection.getInstance();
             Statement statement = conn.createStatement()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Error creating StudentCardResponse table: Database connection is closed or null.");
                return;
            }
            statement.execute(query);
        } catch (SQLException e) {
            System.err.println("Error creating StudentCardResponse table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Adding new materials">
    public int addMaterial(String materialType) {
        String sqlInsert = "INSERT INTO material (materialType) VALUES (?)";
        String sqlLastInsertId = "SELECT last_insert_rowid()";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error adding material: Database connection is closed or null.");
                return -1;
            }
            try (PreparedStatement statement = conn.prepareStatement(sqlInsert)) {
                statement.setString(1, materialType);
                statement.executeUpdate();

                try (Statement idStatement = conn.createStatement();
                     ResultSet rs = idStatement.executeQuery(sqlLastInsertId)) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding material: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public int addContent(Material content, String tableName) {
        String sql = String.format("INSERT INTO %s (%sTopic, %sContent, TeacherID, materialID) " +
                "VALUES (?, ?, ?, ?)", tableName, tableName, tableName);
        String sqlRetrieveDate = String.format("SELECT lastModifiedDate FROM %s WHERE materialID = ? ORDER BY lastModifiedDate DESC LIMIT 1", tableName);
        String sqlUpdateDate = String.format("UPDATE %s SET lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') " +
                "WHERE materialID = ?", tableName);

        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial(tableName);
            if (generatedMaterialID == -1) {
                System.err.println("Failed to create a material entry in the material table for type: " + tableName);
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID);
        }
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error adding content to " + tableName + ": Database connection is closed or null.");
                throw new IllegalStateException("Database connection is not active.");
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, content.getTopic());
                statement.setString(2, content.getContent());
                statement.setInt(3, content.getTeacherID());
                statement.setInt(4, content.getMaterialID());
                statement.executeUpdate();

                try (PreparedStatement statement_updateTimeZone = conn.prepareStatement(sqlUpdateDate)) {
                    statement_updateTimeZone.setInt(1, content.getMaterialID());
                    statement_updateTimeZone.executeUpdate();
                }

                try (PreparedStatement statement_retrieveDate = conn.prepareStatement(sqlRetrieveDate)) {
                    statement_retrieveDate.setInt(1, content.getMaterialID());
                    ResultSet rs = statement_retrieveDate.executeQuery();
                    if (rs.next()) {
                        Timestamp lastModified = rs.getTimestamp("lastModifiedDate");
                        if (lastModified != null) {
                            content.setLastModifiedDate(lastModified.toInstant());
                        }
                    }
                }
                return content.getMaterialID();
            }
        } catch (SQLException e) {
            System.err.println("Error adding content to " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public int addLearningCardToDB(LearningCardCreator content) {
        String sql = "INSERT INTO learningCard (learningCardTopic, learningCardContent, " +
                " materialID) " +
                "VALUES (?, ?, ?)";

        if (content.getMaterialID() <= 0) {
            int generatedMaterialID = addMaterial("learningCard");
            if (generatedMaterialID == -1) {
                throw new IllegalStateException("Failed to create a material entry in the material table.");
            }
            content.setMaterialID(generatedMaterialID);
        }

        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error adding learning card to DB: Database connection is closed or null.");
                throw new IllegalStateException("Database connection is not active.");
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, content.getTopic());
                statement.setString(2, content.getContent());
                statement.setInt(3, content.getMaterialID());
                statement.executeUpdate();
                return content.getMaterialID();
            }
        } catch (SQLException e) {
            System.err.println("Error adding learning card to DB: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addStudentCardResponse(int studentID, int materialID, String cardQuestion, boolean isCorrect, int classroomID) {
        String sql = "INSERT INTO StudentCardResponse (StudentID, MaterialID, CardQuestion, IsCorrect, ClassroomID) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error adding student card response: Database connection is closed or null.");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentID);
                pstmt.setInt(2, materialID);
                pstmt.setString(3, cardQuestion);
                pstmt.setBoolean(4, isCorrect);
                if (classroomID <= 0) {
                    pstmt.setNull(5, Types.INTEGER);
                } else {
                    pstmt.setInt(5, classroomID);
                }
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding student card response: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public boolean setContent(int materialID, String newContent, String newTopic, String tableName) {
        String sqlQuery = String.format("UPDATE %s SET %sContent = ?, %sTopic = ?, lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') WHERE materialID = ?", tableName, tableName, tableName);
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error setting content for " + tableName + ": Database connection is closed or null.");
                return false;
            }
            try (PreparedStatement updateStatement = conn.prepareStatement(sqlQuery)) {
                updateStatement.setString(1, newContent);
                updateStatement.setString(2, newTopic);
                updateStatement.setInt(3, materialID);
                int rowsAffected = updateStatement.executeUpdate();
                return rowsAffected > 0;
            }
        }
        catch (SQLException e) {
            System.err.println("Error setting content for " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateClassroomID(int classroomID, int materialID) {
        String sql = "UPDATE material SET ClassroomID = ? WHERE materialID = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error updating classroom ID: Database connection is closed or null.");
                throw new IllegalStateException("Database connection is not active.");
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, classroomID);
                statement.setInt(2, materialID);
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating classroom ID: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateWeek(int week, int materialID) {
        String sql = "UPDATE material SET week = ? WHERE materialID = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error updating week: Database connection is closed or null.");
                throw new IllegalStateException("Database connection is not active.");
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, week);
                statement.setInt(2, materialID);
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating week: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    public Material getMaterialContent(int materialID, String tableName) {
        String sql = String.format("SELECT * FROM %s WHERE materialID = ?", tableName);
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting material content from " + tableName + ": Database connection is closed or null.");
                return null;
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, materialID);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    return new Material(
                            rs.getString(String.format("%sTopic", tableName)),
                            rs.getString(String.format("%sContent", tableName)),
                            rs.getInt("teacherID"),
                            tableName,
                            materialID,
                            getClassroomID(materialID),
                            getWeek(materialID),
                            rs.getTimestamp("lastModifiedDate") != null
                                    ? rs.getTimestamp("lastModifiedDate").toInstant()
                                    : null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting material content from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getWeek(int materialID) {
        String sql = "SELECT week FROM material WHERE materialID = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting week: Database connection is closed or null.");
                return -1;
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, materialID);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    return rs.getInt("week");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting week: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public int getClassroomID(int materialID) {
        String sql = "SELECT ClassroomID FROM material WHERE materialID = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting classroom ID: Database connection is closed or null.");
                return -1;
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, materialID);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    return rs.getInt("ClassroomID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting classroom ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public int getTeacherID(String teacherEmail) {
        String findTeacherQuery = "SELECT TeacherID FROM Teacher WHERE TeacherEmail = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting teacher ID: Database connection is closed or null.");
                return -1;
            }
            try (PreparedStatement statement = conn.prepareStatement(findTeacherQuery)) {
                statement.setString(1, teacherEmail);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    return rs.getInt("TeacherID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting teacher ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Integer> getClassroomList(String teacherEmail) {
        String findClassroomsQuery = "SELECT ClassroomID FROM Classroom WHERE TeacherEmail = ?";
        List<Integer> classroomList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting classroom list: Database connection is closed or null.");
                return classroomList;
            }
            try (PreparedStatement statement = conn.prepareStatement(findClassroomsQuery)) {
                statement.setString(1, teacherEmail);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    int classroomID = rs.getInt("ClassroomID");
                    classroomList.add(classroomID);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting classroom list: " + e.getMessage());
            e.printStackTrace();
        }
        return classroomList;
    }

    public Timestamp getLastModifiedDate(int materialID, String tableName) {
        String sql = String.format("SELECT lastModifiedDate FROM %s WHERE materialID = ?", tableName);
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting last modified date from " + tableName + ": Database connection is closed or null.");
                return null;
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, materialID);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    return rs.getTimestamp("lastModifiedDate");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting last modified date from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getMaterialByWeekAndClassroom(int weekNumber, String type, int classroomID) {
        String sql = "SELECT materialID FROM material WHERE week = ? AND materialType = ? AND classroomID = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting material by week and classroom: Database connection is closed or null.");
                return -1;
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, weekNumber);
                statement.setString(2, type);
                statement.setInt(3, classroomID);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return result.getInt("materialID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting material by week and classroom: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public ObservableList<ContentTableData> fetchContentTableData(String teacherEmail) {
        String materialQuery = "SELECT * FROM material WHERE ClassroomID = ?";
        ObservableList<ContentTableData> data = FXCollections.observableArrayList();
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error fetching content table data: Database connection is closed or null for teacher " + teacherEmail);
                return data;
            }

            List<Integer> classrooms = getClassroomList(teacherEmail);

            for (int classroomID : classrooms) {
                if (conn == null || conn.isClosed()) {
                    System.err.println("Error fetching content table data: Connection became invalid during loop for classroom " + classroomID);
                    conn = SqliteConnection.getInstance();
                    if (conn == null || conn.isClosed()) continue;
                }
                try (PreparedStatement materialStatement = conn.prepareStatement(materialQuery)) {
                    materialStatement.setInt(1, classroomID);
                    ResultSet resultSet = materialStatement.executeQuery();

                    while (resultSet.next()) {
                        int week = resultSet.getInt("week");
                        String type = resultSet.getString("materialType");
                        int materialID = resultSet.getInt("materialID");
                        Material materialDetails = getMaterialContent(materialID, type);
                        String topic = (materialDetails != null) ? materialDetails.getTopic() : "N/A";
                        Timestamp lastModified = getLastModifiedDate(materialID, type);

                        data.add(new ContentTableData(lastModified, week, topic, type, classroomID, materialID));
                    }
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Error fetching content table data: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
    //</editor-fold>

    //<editor-fold desc="Deletion">
    public boolean deleteContent(int materialID, String tableName) {
        String materialDeleteQuery = "DELETE FROM material WHERE materialID = ?";
        String sql = String.format("DELETE FROM %s WHERE materialID = ?", tableName);
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error deleting content from " + tableName + ": Database connection is closed or null.");
                return false;
            }
            try (PreparedStatement deleteItemStatement = conn.prepareStatement(sql)) {
                deleteItemStatement.setInt(1, materialID);
                int rowsAffected = deleteItemStatement.executeUpdate();

                if (rowsAffected > 0) {
                    try (PreparedStatement deleteMaterialStatement = conn.prepareStatement(materialDeleteQuery)) {
                        deleteMaterialStatement.setInt(1, materialID);
                        int rowsAffected2 = deleteMaterialStatement.executeUpdate();

                        if (rowsAffected2 > 0) {
                            return true;
                        } else {
                            System.err.println("Content from " + tableName + " deleted, but failed to delete corresponding entry from material table for ID: " + materialID);
                            return false;
                        }
                    }
                } else {
                    System.err.println("Failed to delete content from " + tableName + " for material with ID: " + materialID + ". No rows affected.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting content from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    public String getLearningCardContent(int learningCardID) {
        String sql = "SELECT learningCardContent FROM learningCard WHERE learningCardID = ?";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting learning card content: Database connection is closed or null.");
                return null;
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, learningCardID);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    return rs.getString("learningCardContent");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting learning card content: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public  ObservableList<LearningCardCreator> getAllCards(){
        String sql = "SELECT learningCardID, learningCardTopic FROM learningCard";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting all cards: Database connection is closed or null.");
                return FXCollections.observableArrayList();
            }
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                ResultSet rs = statement.executeQuery();
                ObservableList<LearningCardCreator> cards = FXCollections.observableArrayList();
                while (rs.next()) {
                    String topic = rs.getString("learningCardTopic");
                    int ID = rs.getInt("learningCardID");
                    cards.add(new LearningCardCreator(topic,ID));
                }
                return cards;
            }
        } catch (SQLException e) {
            System.err.println("Error getting all cards: " + e.getMessage());
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    // --- New DAO Methods for TeacherAnalyticsController ---

    /**
     * Retrieves a list of distinct week numbers for which "learningCard" type materials exist
     * for a given classroom.
     * @param classroomId The ID of the classroom.
     * @return A list of distinct week numbers.
     */
    public List<Integer> getDistinctWeeksForClassroomLearningCards(int classroomId) {
        List<Integer> weeks = new ArrayList<>();
        String sql = "SELECT DISTINCT week FROM material WHERE ClassroomID = ? AND materialType = 'learningCard' AND week IS NOT NULL ORDER BY week ASC";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting distinct weeks: Database connection is closed or null.");
                return weeks;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, classroomId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    weeks.add(rs.getInt("week"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching distinct weeks for learning cards: " + e.getMessage());
            e.printStackTrace();
        }
        return weeks;
    }

    /**
     * Retrieves student's learning card responses for a specific classroom and week.
     * This method joins StudentCardResponse with Material to filter by week.
     * @param studentId The ID of the student.
     * @param classroomId The ID of the classroom.
     * @param week The specific week.
     * @return A list of StudentCardResponse objects.
     */
    public List<StudentCardResponse> getStudentCardResponsesForWeek(int studentId, int classroomId, int week) {
        List<StudentCardResponse> responses = new ArrayList<>();
        // We need to join StudentCardResponse with Material table to filter by week
        // and ensure the MaterialID in StudentCardResponse corresponds to a 'learningCard' type.
        String sql = "SELECT scr.ResponseID, scr.StudentID, scr.MaterialID, scr.CardQuestion, scr.IsCorrect, scr.ClassroomID " +
                "FROM StudentCardResponse scr " +
                "JOIN material m ON scr.MaterialID = m.materialID " +
                "WHERE scr.StudentID = ? AND scr.ClassroomID = ? AND m.week = ? AND m.materialType = 'learningCard'";
        Connection conn = null;
        try {
            conn = SqliteConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Error getting student card responses for week: Database connection is closed or null.");
                return responses;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, classroomId);
                pstmt.setInt(3, week);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    StudentCardResponse response = new StudentCardResponse(
                            rs.getInt("ResponseID"),
                            rs.getInt("StudentID"),
                            rs.getInt("MaterialID"),
                            rs.getString("CardQuestion"),
                            rs.getBoolean("IsCorrect"),
                            rs.getInt("ClassroomID")
                            // If your StudentCardResponse model doesn't have ResponseTimestamp, this is fine.
                    );
                    responses.add(response);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student card responses for week " + week + ": " + e.getMessage());
            e.printStackTrace();
        }
        return responses;
    }
}
