package com.example.cab302tailproject.model;

import java.time.Instant;

public class Material {
    private int materialID;
    private int ClassroomID;
    private String materialType;    // TODO: make it enumerator
    private int week;

    // Fields relevant to other classes:
    private String topic;
    private Instant lastModifiedDate;
    private String content;
    private int teacherID;

    /**
     * Constructs a new Material object with the specified material ID and material type.
     *
     * @param materialID The unique identifier for the material.
     * @param materialType The type or category of the material (e.g., lesson, worksheet, flashcards).
     */
    public Material(int materialID, String materialType) {
        this.materialID = materialID;
        this.materialType = materialType;
    }

    /**
     * Constructs a new Material object with the specified topic, content, teacher ID, and material type.
     *
     * @param topic The topic associated with the material.
     * @param content The actual content of the material.
     * @param teacherID The ID of the teacher who provided the material.
     * @param materialType The type or category of the material (e.g., lesson, worksheet, flashcards).
     */
    public Material(String topic, String content, int teacherID, String materialType) {
        this.topic = topic;
        this.content = content;
        this.teacherID = teacherID;
        this.materialType = materialType;
    }

    // Getters and setters
    public int getMaterialID() { return materialID; }
    public void setMaterialID(int materialID) { this.materialID = materialID; }
    public int getClassroomID() { return ClassroomID; }
    public void setClassroomID(int classroomID) { this.ClassroomID = classroomID; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public int getWeek() { return week; }
    public void setWeek(int week) { this.week = week; }

    // Extended getters and setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setLastModifiedDate(Instant lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getTeacherID() { return teacherID; }
}
