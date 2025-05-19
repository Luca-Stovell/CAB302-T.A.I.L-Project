package com.example.cab302tailproject.model;

import java.sql.Timestamp;
import java.time.Instant;

public class ContentTableData {
    private final Instant lastModified;
    private final int week;
    private final String topic;
    private final String type;
    private final int classroom;
    private final int materialID;

    public ContentTableData(Instant lastModified, int week, String topic, String type, int classroom, int materialID) {
        this.lastModified = lastModified;
        this.week = week;
        this.topic = topic;
        this.type = type;
        this.classroom = classroom;
        this.materialID = materialID;
    }

    public Instant getLastModified() {
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
