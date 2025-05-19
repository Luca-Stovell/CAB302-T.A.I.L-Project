package com.example.cab302tailproject.model;

public class Material {
    private int materialID;
    private int ClassroomID;
    private String materialType;
    private int week;

    public Material(int materialID, String materialType) {
        this.materialID = materialID;
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
}
