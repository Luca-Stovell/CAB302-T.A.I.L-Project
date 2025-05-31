package com.example.cab302tailproject.model;

import java.time.Instant;

public class Worksheet extends Material {
    /**
     * Constructor for creating a new worksheet with default material ID and lastModifiedDate.
     * @param topic The topic of the worksheet.
     * @param content The content of the worksheet.
     * @param teacherID The ID of the teacher. Must be a non-negative integer.
     */
    public Worksheet(String topic, String content, int teacherID) {
        super(topic, content, teacherID, "worksheet");
    }

    /**
     * Full-argument constructor for creating a worksheet with a specific material ID.
     * @param topic The topic of the worksheet.
     * @param content The content of the worksheet.
     * @param lastModifiedDate The date when the worksheet was last modified. If null, the current date and time will be used.
     * @param teacherID The ID of the teacher. Must be a non-negative integer.
     * @param materialID The unique ID of the material. Must be a non-negative integer.
     */
    public Worksheet(String topic, String content, Instant lastModifiedDate, int teacherID, int materialID) {
        super(topic, content, teacherID, "worksheet", materialID, 0, 0, lastModifiedDate);
    }
}