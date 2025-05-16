package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.Worksheet;

import java.util.List;

public interface IContentDAO {
    /**
     * Adds a lesson to the database
     * @param lessonContent The generated LessonContent object to add
     * @return materialID of the successful entry, -1 if unsuccessful
     */
    int addLessonContent(Lesson lessonContent);

    /**
     * Retrieves lesson content from the database by its materialID.
     * @param materialID The material identifier of the requested lesson
     * @return the LessonContent object if found, null otherwise
     */
    Lesson getLessonContent(int materialID);

    /**
     * Adds a worksheet to the database
     * @param worksheetContent the content of the worksheet to be added
     * @return materialID of the successful entry, -1 if unsuccessful
     */
    int addWorksheetToDB(Worksheet worksheetContent);

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

    public boolean updateTeacherID(String teacherEmail, int materialID, String type);

    public int getClassroomID(int materialID);

    public List<Integer> getClassroomList(String teacherEmail);

    public int getMaterialByWeek(int weekNumber, String type);

}
