package com.example.cab302tailproject.model;

public class Material {
    private int materialID;
    private String title;
    private int ownerID;
    private String materialType;

    public Material(int materialID, String title, int ownerID, String materialType) {
        this.materialID = materialID;
        this.title = title;
        this.ownerID = ownerID;
        this.materialType = materialType;
    }

    // Getters and setters
    public int getMaterialID() { return materialID; }
    public void setMaterialID(int materialID) { this.materialID = materialID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getOwnerID() { return ownerID; }
    public void setOwnerID(int ownerID) { this.ownerID = ownerID; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
}
