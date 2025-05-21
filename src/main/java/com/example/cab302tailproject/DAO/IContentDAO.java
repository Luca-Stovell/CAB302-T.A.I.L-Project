package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.*;
import javafx.collections.ObservableList;

import java.util.List;

public interface IContentDAO {

    /**
     * Retrieves lesson content from the database by its materialID.
     * @param materialID The material identifier of the requested lesson
     * @return the LessonContent object if found, null otherwise
     */
    Lesson getLessonContent(int materialID);

    /**
     * Retrieves the material type details based on the provided material ID.
     * @param materialID The unique identifier of the material to be retrieved.
     * @return The Material object containing the material type and ID, or null if no material is found for the given ID or an error occurs.
     */
    Material getMaterialType(int materialID);

    /**
     * Retrieves the content of a worksheet from the database based on the provided material ID.
     * @param materialID The unique identifier of the material (worksheet) to be retrieved.
     * @return A {@link Worksheet} object containing all attributes if found;
     * otherwise, returns null if no worksheet is found or an error occurs.
     */
    Worksheet getWorksheetContent(int materialID);

    /**
     * Updates the content of the specified material in the database, based on the given material ID.
     * This method accepts either worksheets or lessons and executes the appropriate update query.
     * @param materialID The unique identifier of the existing material to be updated.
     * @param newContent The new content to be set for the specified material.
     * @return true if the content was successfully updated, false otherwise.
     */
    boolean setContent(int materialID, String newContent);

    /**
     * Updates the content and topic of a material in the database based on the provided material ID.
     * This method distinguishes between lessons and worksheets and performs the update operation accordingly.
     *
     * @param materialID The unique identifier of the material to be updated.
     * @param newContent The new content to set for the specified material.
     * @param newTopic The new topic to set for the specified material.
     * @return true if the update operation is successful; false otherwise.
     */
    boolean setContent(int materialID, String newContent, String newTopic);

    /**
     * Deletes the content associated with a specified material ID from the database.
     * This method identifies the material type (lesson or worksheet) and performs the
     * necessary deletions in the corresponding table before removing the material record.
     *
     * @param materialID The unique identifier of the material to be deleted.
     * @return true if the material and its associated content were successfully deleted; false otherwise.
     */
    boolean deleteContent(int materialID);

    /**
     * Updates the ClassroomID for a specific material in the database identified by its materialID.
     *
     * @param classroomID The new ClassroomID to be set for the specified material.
     * @param materialID The unique identifier of the material whose ClassroomID is to be updated.
     * @return true if the ClassroomID was successfully updated; false if the operation fails
     *         or an SQLException is encountered.
     * @throws IllegalStateException if the database connection is not active.
     */
    public boolean updateClassroomID(int classroomID, int materialID);

    /**
     * Updates the "week" field of a material in the database based on the provided material ID.
     *
     * @param week The new week value to set for the specified material.
     * @param materialID The unique identifier of the material whose week value is to be updated.
     * @return true if the "week" field was successfully updated; false otherwise.
     * @throws IllegalStateException if the database connection is not active.
     */
    public boolean updateWeek(int week, int materialID);

    /**
     * Retrieves the week number associated with a specific material from the database.
     *
     * @param materialID The unique identifier of the material whose week value is to be retrieved.
     * @return The week value associated with the specified material ID, or -1 if an error occurs
     *         or no record is found.
     */
    public int getWeek(int materialID);

    /**
     * Retrieves the classroom ID associated with the given material ID.
     *
     * @param materialID the ID of the material for which the classroom ID is to be retrieved
     * @return the classroom ID if found in the database, or -1 if no matching record is found or an error occurs
     */
    public int getClassroomID(int materialID);

    /**
     * Retrieves a list of classroom IDs associated with the given teacher's email.
     *
     * @param teacherEmail the email address of the teacher whose classrooms are to be retrieved
     * @return a list of integers representing the classroom IDs associated with the teacher
     */
    public List<Integer> getClassroomList(String teacherEmail);

    /**
     * Retrieves the ID of the material associated with the specified week number and material type.
     *
     * @param weekNumber the week number for which the material is requested
     * @param type the type of material to be retrieved
     * @return the ID of the material if found; returns -1 if no material is found or an error occurs
     */
    public int getMaterialByWeekAndClassroom(int weekNumber, String type, int classroomID);

    /**
     * Fetches content table data for the classrooms associated with a given teacher's email.
     * The method retrieves materials from the database and populates an observable list
     * with data for each material, including week, topic, type, classroom ID, material ID,
     * and last modified date.
     *
     * @param teacherEmail The email address of the teacher whose classroom materials are to be fetched.
     * @return An ObservableList containing ContentTableData objects representing the material details for the teacher's classrooms.
     */
    public ObservableList<ContentTableData> fetchContentTableData(String teacherEmail);


    /**
     * Retrieves the teacher's unique identifier (TeacherID) based on their email address.
     *
     * @param teacherEmail the email address of the teacher whose ID is to be retrieved
     * @return the TeacherID associated with the provided email address, or -1 if no match is found or an error occurs
     */
    public int getTeacherID(String teacherEmail);

    /**
     * Adds a new worksheet entry to the database, including its attributes.
     * Assumes the materialID is not already set, and generates a new materialID
     * by adding an entry to the material table.
     *
     * @param content      The content object (Material) containing the details to be added,
     *                     including topic, content, teacher ID, classroom ID.
     * @param tableName    The name of the database table to insert the data ("lesson", "worksheet", or "learningCard").
     * @return The material ID of the newly added content if the operation is successful. Returns -1 if an error occurs.
     * @throws IllegalStateException if the database connection is inactive or if adding a material entry fails.
     */
    public int addContent(Material content, String tableName);

}
