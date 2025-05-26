package com.example.cab302tailproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LibraryItemTest {
    private static final int id = 1;
    private static final int teacherId = 1;
    private static final String storedName = "file";
    private static final String originalName = "OllamaSetup.exe";
    private static final long size = 1026310144;
    private static final LocalDateTime uploadedAt = LocalDateTime.now();

    private LibraryItem l;

    @BeforeEach
    void setUp() {
        l = new LibraryItem(id, teacherId, storedName, originalName, size, uploadedAt);
    }

    @Test
    void getId() {
        assertEquals(id, l.getId());
    }

    @Test
    void getTeacherId() {
        assertEquals(teacherId, l.getTeacherId());
    }

    @Test
    void getStoredName() {
        assertEquals(storedName, l.getStoredName());
    }

    @Test
    void getOriginalName() {
        assertEquals(originalName, l.getOriginalName());
    }

    @Test
    void getSize() {
        assertEquals(size, l.getSize());
    }

    @Test
    void getUploadedAt() {
        assertEquals(uploadedAt, l.getUploadedAt());
    }

    @Test
    void testToString() {
        assertEquals("OllamaSetup.exe  (1002256 KB)", l.toString());
    }
}