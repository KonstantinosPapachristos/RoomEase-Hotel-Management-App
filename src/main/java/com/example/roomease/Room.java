package com.example.roomease;

public class Room {
    private final String roomNumber;
    private final String roomType;
    private boolean isAvailable;
    private boolean isOccupied;
    private String amenities;
    private String issues;
    private String status;

    // Constructor with all properties
    public Room(String roomNumber, String roomType, boolean isAvailable, boolean isOccupied, String amenities, String issues, String status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.isAvailable = isAvailable;
        this.isOccupied = isOccupied;
        this.amenities = amenities;
        this.issues = issues;
        this.status = status;
    }

    // Getters and Setters for each property
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getIssues() {
        return issues;
    }

    public void setIssues(String issues) {
        this.issues = issues;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ") - " + (isAvailable ? "Available" : "Occupied") +
                " | Amenities: " + amenities + " | Issues: " + issues + " | Status: " + status;
    }
}
