package com.example.cab302tailproject.model;

import java.time.Instant;
import java.util.List;

public class Material {
    private int materialID;
    private int ClassroomID;
    private String materialType;
    private int week;
    private static final List<String> VALID_MATERIAL_TYPES = List.of("lesson", "worksheet", "learningCard");
    private String topic;
    private Instant lastModifiedDate;
    private String content;
    private int teacherID;

    /**
     * Constructs a new Material object with the specified material ID and material type.
     *
     * @param materialID The unique identifier for the material. Must be non-negative integer.
     * @param materialType The type of the material. Must be "lesson", "worksheet", or "learningCard".
     */
    public Material(int materialID, String materialType) {
        setMaterialID(materialID);
        setMaterialType(materialType);
    }

    /**
     * Constructs a new Material object with the specified topic, content, teacher ID, and material type.
     *
     * @param topic The topic associated with the material.
     * @param content The actual content of the material.
     * @param teacherID The ID of the teacher who provided the material. Must be non-negative integer.
     * @param materialType The type of the material. Must be "lesson", "worksheet", or "learningCard".
     */
    public Material(String topic, String content, int teacherID, String materialType) {
        this.topic = topic;
        this.content = content;
        setTeacherID(teacherID);
        setMaterialType(materialType);
    }

    /**
     * Constructs a new Material object with the specified topic, content, teacher ID, material type,
     * material ID, classroom ID, and week.
     *
     * @param topic The topic associated with the material.
     * @param content The actual content of the material.
     * @param teacherID The ID of the teacher who provided the material. Must be non-negative integer.
     * @param materialType The type of the material. Must be "lesson", "worksheet", or "learningCard".
     * @param materialID The unique identifier for the material. Must be non-negative integer.
     * @param classroomID The ID of the classroom associated with the material. Must be non-negative integer.
     * @param week The week number corresponding to the material's relevance or assigned time frame. Must be non-negative integer.
     */
    public Material(String topic, String content, int teacherID, String materialType, int materialID, int classroomID, int week, Instant lastModifiedDate) {
        this.topic = topic;
        this.content = content;
        setTeacherID(teacherID);
        setMaterialType(materialType);
        setMaterialID(materialID);
        setClassroomID(classroomID);
        setWeek(week);
        this.lastModifiedDate = lastModifiedDate;
    }

    // Getters and setters
    public int getMaterialID() { return materialID; }
    /**
     * Sets the material ID for the material. The material ID must be a non-negative integer.
     *
     * @param materialID The unique identifier for the material. Must be a non-negative integer.
     * @throws IllegalArgumentException if the materialID is negative.
     */
    public void setMaterialID(int materialID) {
        if (materialID < 0) {
            throw new IllegalArgumentException("Material ID must be non-negative.");
        }
        this.materialID = materialID;
    }

    public int getClassroomID() { return ClassroomID; }
    /**
     * Sets the ID of the classroom associated with the material.
     * The classroom ID must be a non-negative integer.
     *
     * @param classroomID The ID of the classroom. Must be a non-negative integer.
     * @throws IllegalArgumentException if the classroomID is negative.
     */
    public void setClassroomID(int classroomID) {
        if (classroomID < 0) {
            throw new IllegalArgumentException("Classroom ID cannot be negative");
        }
        this.ClassroomID = classroomID;
    }

    public String getMaterialType() { return materialType; }
    /**
     * Sets the material type for the material.
     *
     * @param materialType The type of the material to set. Must be "lesson", "worksheet", or "learningCard".
     * @throws IllegalArgumentException if the provided material type is not valid.
     */
    public void setMaterialType(String materialType) {
        if (!VALID_MATERIAL_TYPES.contains(materialType)) {
            throw new IllegalArgumentException("Invalid material type. Allowed values are: " + VALID_MATERIAL_TYPES);
        }
        this.materialType = materialType;
    }

    public int getWeek() { return week; }
    /**
     * Sets the week number associated with the material. The provided week value
     * must be a non-negative integer.
     *
     * @param week The week number corresponding to the material's relevance or assigned time frame.
     *             Must be a non-negative integer.
     * @throws IllegalArgumentException if the provided week value is negative.
     */
    public void setWeek(int week) {
        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }
        this.week = week;
    }

    // Extended getters and setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setLastModifiedDate(Instant lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
    public Instant getLastModifiedDate() { return lastModifiedDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getTeacherID() { return teacherID; }
    /**
     * Sets the ID of the teacher associated with the material.
     * The teacher ID must be a non-negative integer.
     *
     * @param teacherID The ID of the teacher. Must be a non-negative integer.
     * @throws IllegalArgumentException if the teacherID is negative.
     */
    public void setTeacherID(int teacherID) {
        if (teacherID < 0) {
            throw new IllegalArgumentException("Classroom ID cannot be negative");
        }
        this.teacherID = teacherID;
    }
}
