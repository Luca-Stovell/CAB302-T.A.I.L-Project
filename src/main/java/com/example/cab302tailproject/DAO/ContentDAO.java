package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LearningCardCreator;
import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.Worksheet;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// the controllers for the login/register page should use these
public class ContentDAO implements IContentDAO {
    //<editor-fold desc="Initialisation">
    private Connection connection;

    /**
     * Constructor for the ContentDAO class.
     * Initializes the database connection using a singleton approach provided by the
     * SqliteConnection class. Additionally, it creates the necessary database tables
     * ("material", "lesson", and "worksheet") if they do not already exist.
     *
     * If the connection establishment process fails, this constructor throws a RuntimeException
     * with the relevant SQLException details.
     *
     * The database file used by this connection is "Tail.db", and it is assumed to
     * reside in the application's working directory. Modify the JDBC URL if required
     * to match your configuration.
     */
    public ContentDAO() {
        connection = SqliteConnection.getInstance();
        createMaterialTable();
        createLessonTable();
        createWorksheetTable();
        createLearningCardTable();
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
    //</editor-fold>

    //<editor-fold desc="Table creation">
    /**
     * Creates the "material" table in the database if it does not already exist.
     * The table includes the following columns:
     *
     * - materialID: An INTEGER that serves as the primary key and is auto-incremented.
     * - materialType: A TEXT field that cannot be null, specifying the type of material.
     *
     * If the table creation operation encounters an exception, the error is caught,
     * and the stack trace is printed.
     *
     * This method performs the table creation operation using a SQL query executed
     * through a Statement object. The database connection used for executing the
     * query is assumed to be active and valid.
     */
    private void createMaterialTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS material ("
                        + "materialID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "materialType TEXT NOT NULL, "
                        + "week INTEGER, "
                        + "ClassroomID INTEGER, "
                        + "FOREIGN KEY (ClassroomID) REFERENCES Classroom(ClassroomID)"
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates the "lesson" table in the database if it does not already exist.
     * The table includes columns for lesson ID, topic, content, last modified date, teacher ID,
     * classroom ID, and material ID.
     *
     * The lessonID column serves as the primary key and is auto-incremented.
     * The materialID column is mandatory and acts as a foreign key referencing the material table.
     * The TeacherID and ClassroomID columns also feature foreign key constraints to their respective tables.
     *
     * A default timestamp is applied to the lastModifiedDate column to capture the last update time automatically.
     *
     * If the table creation query encounters an exception, the stack trace is printed.
     */
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
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the "worksheet" table in the database if it does not already exist.
     * The table contains columns for worksheet ID, topic, content, last modified date, teacher ID,
     * classroom ID, and material ID. It also establishes foreign key constraints between the worksheet
     * table and the material, teacher, and classroom tables.
     *
     * The worksheetID column is set as the primary key and auto-incremented.
     * The materialID column is mandatory and serves as a foreign key reference to the material table.
     *
     * A default timestamp is set for the lastModifiedDate column to capture the time of the last update.
     *
     * If the table creation query fails, an exception is caught, and the stack trace is printed.
     */
    private void createWorksheetTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS worksheet ("
                        + "worksheetID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "worksheetTopic TEXT, "
                        + "worksheetContent TEXT, "
                        + "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "TeacherID INTEGER, "
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID)"
                        + "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID)"
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the lesson card table, which contains information about learning cards.
     * <p>Includes: learningCardID, learningCardTopic, learningCardContent, materialID</p>
     * materialID is a foreign key, that references the material table.
     */
    // Some of the fields are disabled, add them back in if needed
    private void createLearningCardTable() {
        String query =
                "CREATE TABLE IF NOT EXISTS learningCard ("
                        + "learningCardID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "learningCardTopic TEXT, " // same as parent lesson?
                        + "learningCardContent TEXT, "
                        //+ "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " // probably unnecessary
                        //+ "TeacherID INTEGER, " // consider replacing this with parent lesson (assuming card sets are generated from lessons
                        + "materialID INTEGER NOT NULL, "
                        + "FOREIGN KEY (materialID) REFERENCES material(materialID)"
                        //+ "FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID), "
                        + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Adding new materials">
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
     * Adds a new lesson entry to the database, including its attributes.
     * If the material ID is not already set in the provided lesson object, this method
     * generates a new material ID by adding an entry to the material table.
     *
     * @param content The Lesson object containing the details of the lesson to be added,
     *                including topic, content, teacher ID, classroom ID, and optionally material ID.
     * @return The material ID of the newly added lesson if the operation is successful.
     *         Returns -1 if an error occurs.
     * @throws IllegalStateException if the database connection is inactive or if adding
     *                                a material entry fails.
     */
    public int addLessonContent(Lesson content) {
        String sql = "INSERT INTO lesson (lessonTopic, lessonContent, " +
                "TeacherID, materialID) " +
                "VALUES (?, ?, ?, ?)";
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
            statement.setInt(4, content.getMaterialID());
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
     * Adds a new worksheet entry to the database, including its attributes.
     * If the materialID is not already set, this method generates a new materialID
     * by adding an entry to the material table.
     *
     * @param content The Worksheet object containing the details of the worksheet to
     *                be added, including topic, content, teacher ID, classroom ID, and optionally materialID.
     * @return The materialID of the newly added worksheet if the operation is successful. Returns -1 if an error occurs.
     * @throws IllegalStateException if the database connection is inactive or if adding a material entry fails.
     */
    public int addWorksheetToDB(Worksheet content) {
        String sql = "INSERT INTO worksheet (worksheetTopic, worksheetContent, " +
                "TeacherID, materialID) " +
                "VALUES (?, ?, ?, ?)";
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
            statement.setInt(4, content.getMaterialID());
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
    //</editor-fold>

    //<editor-fold desc="Setters">

    //<editor-fold desc="Lesson and worksheet setters">
    /**
     * Updates the content of the specified material in the database, based on the given material ID.
     * This method accepts either worksheets or lessons and executes the appropriate update query.
     * @param materialID The unique identifier of the existing material to be updated. Must already exist as "worksheet" or "lesson".
     * @param newContent The new content to be set for the specified material.
     * @return true if the content was successfully updated, false otherwise.
     */
    public boolean setContent(int materialID, String newContent) {
        String lessonUpdateQuery = "UPDATE lesson SET lessonContent = ?, lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') WHERE materialID = ?";
        String worksheetUpdateQuery = "UPDATE worksheet SET worksheetContent = ?, lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') WHERE materialID = ?";

        try {
            Material material = getMaterialType(materialID);
            String materialType = material.getMaterialType();
            String updateQuery = null;
            if (material == null) {
                System.err.println("No material found with ID: " + materialID);
                return false;
            }

            if ("lesson".equalsIgnoreCase(materialType)) {
                updateQuery = lessonUpdateQuery;
            } else if ("worksheet".equalsIgnoreCase(materialType)) {
                updateQuery = worksheetUpdateQuery;
            } else {
                System.err.println("Invalid material type: " + materialType);
                return false;
            }

            // Execute the update query
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, newContent);
                updateStatement.setInt(2, materialID);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    return true;
                } else {
                    System.err.println("Failed to update content for material with ID: " + materialID);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the content and topic of a material in the database based on the provided material ID.
     * This method distinguishes between lessons and worksheets and performs the update operation accordingly.
     *
     * @param materialID The unique identifier of the material to be updated.
     * @param newContent The new content to set for the specified material.
     * @param newTopic The new topic to set for the specified material.
     * @return true if the update operation is successful; false otherwise.
     */
    public boolean setContent(int materialID, String newContent, String newTopic) {
        String lessonUpdateQuery = "UPDATE lesson SET lessonContent = ?, lessonTopic = ?, lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') WHERE materialID = ?";
        String worksheetUpdateQuery = "UPDATE worksheet SET worksheetContent = ?, worksheetTopic = ?, lastModifiedDate = DATETIME(CURRENT_TIMESTAMP, '+10 hours') WHERE materialID = ?";

        try {
            Material material = getMaterialType(materialID);
            String materialType = material.getMaterialType();
            String updateQuery = null;

            if (material == null) {
                System.err.println("No material found with ID: " + materialID);
                return false;
            }

            if ("lesson".equalsIgnoreCase(materialType)) {
                updateQuery = lessonUpdateQuery;
            } else if ("worksheet".equalsIgnoreCase(materialType)) {
                updateQuery = worksheetUpdateQuery;
            } else {
                System.err.println("Invalid material type: " + materialType);
                return false;
            }
            // Execute the update query
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, newContent);
                updateStatement.setString(2, newTopic);
                updateStatement.setInt(3, materialID);
                int rowsAffected = updateStatement.executeUpdate();

                // Check if the update was successful
                if (rowsAffected > 0) {
                    return true;
                } else {
                    System.err.println("Failed to update content/topic for material with ID: " + materialID);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //</editor-fold>

    /**
     * Updates the ClassroomID for a specific material in the database identified by its materialID.
     *
     * @param classroomID The new ClassroomID to be set for the specified material.
     * @param materialID The unique identifier of the material whose ClassroomID is to be updated.
     * @return true if the ClassroomID was successfully updated; false if the operation fails
     *         or an SQLException is encountered.
     * @throws IllegalStateException if the database connection is not active.
     */
    public boolean updateClassroomID(int classroomID, int materialID) {
        String sql = "UPDATE material SET ClassroomID = ? WHERE materialID = ?";
        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classroomID);
            statement.setInt(2, materialID);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the "week" field of a material in the database based on the provided material ID.
     *
     * @param week The new week value to set for the specified material.
     * @param materialID The unique identifier of the material whose week value is to be updated.
     * @return true if the "week" field was successfully updated; false otherwise.
     * @throws IllegalStateException if the database connection is not active.
     */
    public boolean updateWeek(int week, int materialID) {
        String sql = "UPDATE material SET week = ? WHERE materialID = ?";
        if (connection == null) {
            throw new IllegalStateException("Database connection is not active.");
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, week);
            statement.setInt(2, materialID);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Updates the teacher ID for a specific material given the teacher's email, material ID, and type of material.
     * The method validates the input type to ensure it matches known material types (e.g., "lesson" or "worksheet").
     * It queries the database to find the teacher ID associated with the provided email and updates the corresponding
     * material record with the found teacher ID.
     *
     * @param teacherEmail the email address of the teacher whose ID needs to be retrieved and updated.
     * @param materialID the ID of the material (e.g., lesson or worksheet) to be updated.
     * @param type the type of material ("lesson" or "worksheet") to specify the appropriate database table for the update.
     * @return true if the update was successful and a record was modified, false otherwise.
     * @throws IllegalArgumentException if the provided type is invalid or not recognized.
     */
    public boolean updateTeacherID(String teacherEmail, int materialID, String type){
        // Accept different ways of saying the same thing       // TODO: Change method to not need type, let it find type itself
        if (type.equals("Lesson Plan")) {
            type = "lesson";
        }
        if (type.equals("Worksheet")) {
            type = "worksheet";
        }
        // Validate input type to prevent SQL injection or errors
        if (!type.equalsIgnoreCase("worksheet") && !type.equalsIgnoreCase("lesson")) {
            System.out.println("Type is " + type + ".");
            throw new IllegalArgumentException("Invalid table type specified. Must be 'worksheet' or 'lesson'.");
        }

        String findTeacherQuery = "SELECT TeacherID FROM Teacher WHERE TeacherEmail = ?";
        String sqlUpdate = "UPDATE " + type + " SET TeacherID = ? WHERE materialID = ?";


        try (PreparedStatement statement = connection.prepareStatement(findTeacherQuery)) {
            statement.setString(1, teacherEmail);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int teacherID = rs.getInt("TeacherID");

                try (PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)) {
                    updateStatement.setInt(1, teacherID);
                    updateStatement.setInt(2, materialID);

                    int rowsUpdated = updateStatement.executeUpdate();
                    return rowsUpdated > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">

    //<editor-fold desc="Content Getters">
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
                        rs.getInt("materialID")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

        /**
         * Retrieves the content of a worksheet from the database based on the provided material ID.
         * @param materialID The unique identifier of the material (worksheet) to be retrieved.
         * @return A {@link Worksheet} object containing all attributes if found;
         * otherwise, returns null if no worksheet is found or an error occurs.
         */
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
                            rs.getInt("materialID")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        //</editor-fold>

    /**
     * Retrieves the material type details based on the provided material ID.
     * @param materialID The unique identifier of the material to be retrieved.
     * @return The Material object containing the material type and ID, or null if no material is found for the given ID or an error occurs.
     */
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

    /**
     * Retrieves the week number associated with a specific material from the database.
     *
     * @param materialID The unique identifier of the material whose week value is to be retrieved.
     * @return The week value associated with the specified material ID, or -1 if an error occurs
     *         or no record is found.
     */
    public int getWeek(int materialID) {
        String sql = "SELECT * FROM material WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("week");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves the classroom ID associated with the given material ID.
     *
     * @param materialID the ID of the material for which the classroom ID is to be retrieved
     * @return the classroom ID if found in the database, or -1 if no matching record is found or an error occurs
     */
    public int getClassroomID(int materialID) {
        String sql = "SELECT * FROM material WHERE materialID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, materialID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("classroomID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves a list of classroom IDs associated with the given teacher's email.
     *
     * @param teacherEmail the email address of the teacher whose classrooms are to be retrieved
     * @return a list of integers representing the classroom IDs associated with the teacher
     */
    public List<Integer> getClassroomList(String teacherEmail) {
        String findClassroomsQuery = "SELECT ClassroomID FROM Classroom WHERE TeacherEmail = ?";
        List<Integer> classroomList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(findClassroomsQuery)) {
            statement.setString(1, teacherEmail);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int classroomID = rs.getInt("ClassroomID");
                classroomList.add(classroomID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classroomList;
    }

    /**
     * Retrieves the ID of the material associated with the specified week number and material type.
     *
     * @param weekNumber the week number for which the material is requested
     * @param type the type of material to be retrieved
     * @return the ID of the material if found; returns -1 if no material is found or an error occurs
     */
    public int getMaterialByWeek(int weekNumber, String type) { // TODO: Add classroomID as parameter
        String sql = "SELECT materialID FROM material WHERE week = ? AND materialType = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, weekNumber);
            statement.setString(2, type);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                int materialID = result.getInt("materialID");
                return materialID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //</editor-fold>

    public int addLearningCardToDB(LearningCardCreator content) {
        String sql = "INSERT INTO learningCard (learningCardTopic, learningCardContent, " +
                " materialID) " +
                "VALUES (?, ?, ?)";

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
            statement.setInt(3, content.getMaterialID());
            statement.executeUpdate();


            return content.getMaterialID();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //<editor-fold desc="Deletion">
    /**
     * Deletes the content associated with a specified material ID from the database.
     * This method identifies the material type (lesson or worksheet) and performs the
     * necessary deletions in the corresponding table before removing the material record.
     *
     * @param materialID The unique identifier of the material to be deleted.
     * @return true if the material and its associated content were successfully deleted; false otherwise.
     */
    public boolean deleteContent(int materialID){
        String lessonUpdateQuery = "DELETE FROM lesson WHERE materialID = ?";
        String worksheetUpdateQuery = "DELETE FROM worksheet WHERE materialID = ?";
        String materialDeleteQuery = "DELETE FROM material WHERE materialID = ?";

        try {
            Material material = getMaterialType(materialID);
            String materialType = material.getMaterialType();
            String updateQuery = null;
            if (material == null) {
                System.err.println("No material found with ID: " + materialID);
                return false;
            }

            if ("lesson".equalsIgnoreCase(materialType)) {
                updateQuery = lessonUpdateQuery;
            } else if ("worksheet".equalsIgnoreCase(materialType)) {
                updateQuery = worksheetUpdateQuery;
            } else {
                System.err.println("Invalid material type: " + materialType);
                return false;
            }

            // Execute the update query
            try (PreparedStatement deleteItemStatement = connection.prepareStatement(updateQuery)) {
                deleteItemStatement.setInt(1, materialID);
                int rowsAffected = deleteItemStatement.executeUpdate();

                if (rowsAffected > 0) {
                    try (PreparedStatement deleteMaterialStatement = connection.prepareStatement(materialDeleteQuery)) {
                        deleteMaterialStatement.setInt(1, materialID);
                        int rowsAffected2 = deleteMaterialStatement.executeUpdate();

                        if (rowsAffected2 > 0){
                            return true;
                        }
                    }
                } else {
                    System.err.println("Failed to update content for material with ID: " + materialID);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    /**
     * Gets the contents of a stored learning card by its ID
     * @param learningCardID ID of the learning card
     * @return String containing learning card content as it is stored in the database
     */
    public String getLearningCardContent(int learningCardID) {
        String sql = "SELECT learningCardContent FROM learningCard WHERE learningCardID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, learningCardID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getString("learningCardContent");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
