package com.example.cab302tailproject.model;

import java.sql.Timestamp;

/**
 * Represents a data structure for managing content table information,
 * specifically for the "All content" teacher view.
 * This class is designed to populate a TableView with content (or "material")
 * information. The class is immutable.
 */
@SuppressWarnings("ClassCanBeRecord")
public class ContentTableData {
    private final Timestamp lastModified;
    private final int week;
    private final String topic;
    private final String type;
    private final int classroom;
    private final int materialID;

    /**
     * Constructs a new instance of the ContentTableData class, representing details
     * related to content resources. This object is intended
     *
     * @param lastModified The timestamp of the last modification of the content.
     * @param week The week associated with the content.
     * @param topic The topic or subject of the content.
     * @param type The type of the content (e.g., lecture, assignment, etc.).
     * @param classroom The classroom associated with the content (represented by an integer).
     * @param materialID The unique identifier for the material associated with the content.
     */
    public ContentTableData(Timestamp lastModified, int week, String topic, String type, int classroom, int materialID) {
        this.lastModified = lastModified;
        this.week = week;
        this.topic = topic;
        this.type = type;
        this.classroom = classroom;
        this.materialID = materialID;
    }

    // ---- All the following is necessary to populate the TableView that this class is used for ---- //

    public Timestamp getLastModified() {
        return lastModified;
    }

    public int getWeek() {
        return week;
    }

    public String getTopic() {
        return topic;
    }

    public String getType() {
        return type;
    }

    public int getClassroom() {
        return classroom;
    }

    public int getMaterialID() {
        return materialID;
    }

}
