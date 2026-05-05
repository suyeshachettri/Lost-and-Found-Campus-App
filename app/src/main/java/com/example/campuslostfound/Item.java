package com.example.campuslostfound;

public class Item {

    public String id;
    public String name;
    public String location;
    public String description;
    public String type;
    public String image;
    public String status;
    public String userId;

    // REQUIRED empty constructor
    public Item() {
    }

    // Getters and setters (IMPORTANT)

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public com.google.firebase.Timestamp timestamp;

    public com.google.firebase.Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(com.google.firebase.Timestamp timestamp) { this.timestamp = timestamp; }
}