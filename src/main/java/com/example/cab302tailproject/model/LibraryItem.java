package com.example.cab302tailproject.model;

import java.time.LocalDateTime;

public class LibraryItem {
    private int id;
    private int teacherId;
    private String storedName;
    private String originalName;
    private long size;
    private LocalDateTime uploadedAt;


    public LibraryItem(int teacherId, String storedName,
                       String originalName, long size, LocalDateTime uploadedAt) {
        this.teacherId   = teacherId;
        this.storedName  = storedName;
        this.originalName = originalName;
        this.size        = size;
        this.uploadedAt  = uploadedAt;
    }
    public LibraryItem(int id, int teacherId, String storedName,
                       String originalName, long size, LocalDateTime uploadedAt) {
        this(teacherId, storedName, originalName, size, uploadedAt);
        this.id = id;
    }


    public int getId()             { return id; }
    public int getTeacherId()      { return teacherId; }
    public String getStoredName()  { return storedName; }
    public String getOriginalName(){ return originalName; }
    public long getSize()          { return size; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setId(int id)                 { this.id = id; }
    public void setTeacherId(int teacherId)   { this.teacherId = teacherId; }
    public void setStoredName(String name)    { this.storedName = name; }
    public void setOriginalName(String name)  { this.originalName = name; }
    public void setSize(long size)            { this.size = size; }
    public void setUploadedAt(LocalDateTime t){ this.uploadedAt = t; }

    @Override public String toString() {
        return originalName + "  (" + size/1024 + " KB)";
    }
}
