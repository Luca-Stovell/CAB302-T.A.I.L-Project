package com.example.cab302tailproject.model;

import java.time.Instant;

public class Worksheet {
    private String topic;   // Topic of worksheet
    private String content; // Actual content of the worksheet
    private Instant lastModifiedDate;
    private int teacherID;
    private int materialID;

    /**
     * Constructor for creating a new lesson plan (date and materialID is auto-set).
     * @param topic The topic of the lesson
     * @param content The actual content of the lesson
     * @param teacherID The ID of the teacher
     */
    public Worksheet(String topic, String content,
                  int teacherID) {
        this.topic = topic;
        this.content = content;
        this.teacherID = teacherID;
    }

    public Worksheet(String topic, String content, Instant lastModifiedDate,
                         int teacherID, int materialID) {
        this.topic = topic;
        this.content = content;
        this.lastModifiedDate = (lastModifiedDate == null) ? Instant.now() : lastModifiedDate;;
        this.teacherID = teacherID;
        this.materialID = materialID;
    }

    // Getters and Setters
    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public int getTeacherID() {
        return teacherID;
    }
    public void setTeacherID(int teacherID) {
        this.teacherID = teacherID;
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