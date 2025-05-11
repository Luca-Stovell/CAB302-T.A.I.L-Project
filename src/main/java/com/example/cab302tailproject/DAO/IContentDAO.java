package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Material;
import com.example.cab302tailproject.model.Worksheet;

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
    int addWorksheetContent(Worksheet worksheetContent);

    /**
     * Retrieves the material details based on the given material ID.
     * @param materialID The unique identifier of the material to be retrieved.
     * @return The Material object corresponding to the provided material ID, or null if no such material exists.
     */
    Material getMaterialType(int materialID);

    /**
     * Retrieves the worksheet content associated with the given material ID.
     * @param materialID The unique identifier for the worksheet to be retrieved.
     * @return The Worksheet object containing the requested content, or null if no worksheet is found for the given ID.
     */
    Worksheet getWorksheetContent(int materialID);

}
