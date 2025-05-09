package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.LessonContent;

public interface ILessonContentDAO {
    /**
     * Adds a lesson's content to the database
     * @param lessonContent The generated LessonContent object to add
     * @return true if the content is successfully added, false if not
     */
    boolean addLessonContent(LessonContent lessonContent);

    /**
     * Retrieves lesson content from the database by its materialID.
     * @param materialID The material identifier of the requested lesson
     * @return the LessonContent object if found, null otherwise
     */
    LessonContent getLessonContent(int materialID);

}
