package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Lesson;
import com.example.cab302tailproject.model.Worksheet;

public interface IContentDAO {
    /**
     * Adds a lesson to the database
     * @param lessonContent The generated LessonContent object to add
     * @return true if the content is successfully added, false if not
     */
    boolean addLessonContent(Lesson lessonContent);

    /**
     * Retrieves lesson content from the database by its materialID.
     * @param materialID The material identifier of the requested lesson
     * @return the LessonContent object if found, null otherwise
     */
    Lesson getLessonContent(int materialID);

    /**
     * Adds a worksheet to the database
     * @param worksheetContent the content of the worksheet to be added
     * @return true if successfully added to database, false if not
     */
    boolean addWorksheetContent(Worksheet worksheetContent);

}
