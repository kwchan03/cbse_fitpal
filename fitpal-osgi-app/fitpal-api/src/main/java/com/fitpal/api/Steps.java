package com.fitpal.api;

public class Steps {

    private String id;

    private String userId;

    private String date;

    private int steps;

    private double distance;

    private double calories;

    // Constructors
    public Steps() {}

    public Steps(String userId, String date, int steps) {
        this.userId = userId;
        this.date = date;
        this.steps = steps;
    }

    public Steps(String userId, String date, int steps, double distance, double calories) {
        this.userId = userId;
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.calories = calories;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }
}
