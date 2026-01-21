package com.fitpal.api;

public class Badge {

    private String id;
    private String name;
    private String description;
    private double threshold;

    public Badge() {
    }

    public Badge(String id, String name, String description, double threshold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.threshold = threshold;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
