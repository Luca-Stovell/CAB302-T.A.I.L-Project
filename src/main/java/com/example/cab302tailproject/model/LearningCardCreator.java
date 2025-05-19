package com.example.cab302tailproject.model;

import java.time.Instant;
// many problems with this, but used for testing right now
public class LearningCardCreator {
    private String topic;   // Topic of worksheet
    private String content; // Actual content of the worksheet
    //private Instant lastModifiedDate;
    //private int teacherID;
    private int materialID;

    /**
     * Constructor used when learning cards are added to the database
     * @param topic String describing what the learning card is about
     * @param content The contents of the card deck
     */
    public LearningCardCreator(String topic, String content) {
        this.topic = topic;
        this.content = content;

    }

    public LearningCardCreator(String topic, String content,  int materialID) {
        this.topic = topic;
        this.content = content;
        this.materialID = materialID;
    }

    /**
     * Constructor that doesn't accept content, for use when getting a list of materials from the database
     * @param topic String describing what the learning card is about
     * @param materialID ID assigned to the learning card by SQLite autoincrement
     */
    public LearningCardCreator(String topic, int materialID) {
        this.topic = topic;
        this.content = null;
        this.materialID = materialID;
    }

    // Getters and Setters
    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
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

    @Override
    public String toString() {
        return topic;
    }

}