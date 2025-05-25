package com.example.cab302tailproject.model;

import java.time.Instant;

public class Lesson {
    private String topic;   // Topic of lesson
    private String content; // Actual content of the lesson
    private Instant lastModifiedDate;
    private int teacherID;
    private int materialID;

    /**
     * Constructor for creating a new lesson plan (date and materialID is auto-set).
     * @param topic The topic of the lesson
     * @param content The actual content of the lesson
     * @param teacherID The ID of the teacher. Must be non-negative integer.
     */
    public Lesson(String topic, String content,
                  int teacherID) {
        this.topic = topic;
        this.content = content;
        setTeacherID(teacherID);
    }

    /**
     * Constructor for lesson content. Use this constructor to access the date and materialID.
     * @param topic The topic of the lesson
     * @param content The actual content of the lesson
     * @param lastModifiedDate The timestamp of when the lesson was last modified
     * @param teacherID The ID of the teacher. Must be non-negative integer.
     * @param materialID The ID of the material. Must be non-negative integer.
     */
    public Lesson(String topic, String content, Instant lastModifiedDate,
                  int teacherID, int materialID) {
        this.topic = topic;
        this.content = content;
        this.lastModifiedDate = (lastModifiedDate == null) ? Instant.now() : lastModifiedDate;
        setTeacherID(teacherID);
        setMaterialID(materialID);
    }

    // Getters and Setters

    /**
     * Sets the material ID for the lesson. The material ID must be a non-negative integer.
     *
     * @param materialID The ID of the material associated with the lesson. Must be a non-negative integer.
     * @throws IllegalArgumentException if the materialID is negative.
     */
    public void setMaterialID(int materialID) {
        if (materialID < 0) {
            throw new IllegalArgumentException("Material ID must be non-negative.");
        }
        this.materialID = materialID;
    }
    public int getMaterialID() {
        return materialID;
    }

    /**
     * Sets the teacher ID for the lesson. The teacher ID must be non-negative.
     *
     * @param teacherID The ID of the teacher associated with the lesson.
     *                  Must be a non-negative integer.
     * @throws IllegalArgumentException if the teacherID is negative.
     */
    public void setTeacherID(int teacherID) {
        if (teacherID < 0) {
            throw new IllegalArgumentException("Classroom ID cannot be negative");
        }
        this.teacherID = teacherID;
    }
    public int getTeacherID() {
        return teacherID;
    }

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
