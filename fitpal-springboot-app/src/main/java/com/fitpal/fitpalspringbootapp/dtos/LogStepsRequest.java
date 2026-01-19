package com.fitpal.fitpalspringbootapp.dtos;

public class LogStepsRequest {

    private String userId;

    private String date;

    private int steps;

    public LogStepsRequest() {}

    public LogStepsRequest(String userId, String date, int steps) {
        this.userId = userId;
        this.date = date;
        this.steps = steps;
    }

    // Getters and Setters
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
}