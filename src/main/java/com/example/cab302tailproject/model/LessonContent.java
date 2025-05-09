package com.example.cab302tailproject.model;

import java.util.Date;

public class LessonContent {
    private int materialID; // Matches materials.materialID
    private String content; // Actual content of the lesson
    private Date lastModifiedDate;

    public LessonContent(int materialID, String content, Date lastModifiedDate) {
        this.materialID = materialID;
        this.content = content;
        this.lastModifiedDate = lastModifiedDate;
    }

    // Getters and Setters
    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
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
