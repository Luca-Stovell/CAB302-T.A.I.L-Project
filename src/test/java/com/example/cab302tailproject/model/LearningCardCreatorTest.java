package com.example.cab302tailproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LearningCardCreatorTest {

    private static final String topic = "JUnit 5";
    private static final String content = "test\n\ndata";
    private static final int materialID = 1;

    private LearningCardCreator c;
    @BeforeEach
    void setup(){
        c = new LearningCardCreator(topic, content, materialID);
    }

    @Test
    void testTopicContentConstructor(){
        LearningCardCreator c2 = new LearningCardCreator(topic,content);
        assertEquals(topic, c2.getTopic());
        assertEquals(content, c2.getContent());
        // checks for default int (0)
        assertEquals(0, c2.getMaterialID());
    }
    @Test
    void testTopicMaterialIDConstructor(){
        LearningCardCreator c3 = new LearningCardCreator(topic,materialID);
        assertEquals(topic, c3.getTopic());
        assertNull(c3.getContent());
        assertEquals(materialID, c3.getMaterialID());
    }
    @Test
    void getMaterialID() {
        assertEquals(materialID, c.getMaterialID());
    }

    @Test
    void setMaterialID() {
        c.setMaterialID(1);
        assertEquals(1, c.getMaterialID());
    }

    @Test
    void getTopic() {
        assertEquals(topic, c.getTopic());
    }

    @Test
    void getContent() {
        assertEquals(content, c.getContent());
    }

    @Test
    void testToString() {
        assertEquals(topic, c.toString());
    }
}