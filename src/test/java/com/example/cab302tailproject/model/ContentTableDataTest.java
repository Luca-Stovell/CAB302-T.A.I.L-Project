package com.example.cab302tailproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ContentTableDataTest {

    private static final Timestamp lastModified = new Timestamp(1); // one millisecond after the 1970-1-1
    private static final int week = 13;
    private static final String topic = "Unit testing";
    private static final String type = "worksheet";
    private static final int classroom = 1;
    private static final int materialID = 1;

    private ContentTableData data;

    @BeforeEach
    void setUp() {
        data =  new ContentTableData(lastModified, week, topic, type, classroom, materialID);
    }

    @Test
    void getLastModified() {
        assertEquals(lastModified, data.getLastModified());
    }

    @Test
    void getWeek() {
        assertEquals(week, data.getWeek());
    }

    @Test
    void getTopic() {
        assertEquals(topic, data.getTopic());
    }

    @Test
    void getType() {
        assertEquals(type, data.getType());
    }

    @Test
    void getClassroom() {
        assertEquals(classroom, data.getClassroom());
    }

    @Test
    void getMaterialID() {
        assertEquals(materialID, data.getMaterialID());
    }
}