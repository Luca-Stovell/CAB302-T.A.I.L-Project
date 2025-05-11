package com.example.cab302tailproject.model;

import java.util.Date;

public class Worksheet {
    private String topic;   // Topic of lesson
    private String content; // Actual content of the lesson
    private Date lastModifiedDate;
    private int teacherID;
    private int classroomID;
    private int materialID; // Matches materials.materialID


    public Worksheet(String topic, String content, Date lastModifiedDate,
                         int teacherID, int classroomID, int materialID) {
        this.topic = topic;
        this.content = content;
        this.lastModifiedDate = lastModifiedDate;
        this.teacherID = teacherID;
        this.classroomID = classroomID;
        this.materialID = materialID;
    }

    // Getters and Setters
    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public int getClassroomID() {
        return classroomID;
    }
    public void setClassroomID(int classroomID) {
        this.classroomID = classroomID;
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

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}