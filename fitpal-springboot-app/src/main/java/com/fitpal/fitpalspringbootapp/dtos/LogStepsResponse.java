package com.fitpal.fitpalspringbootapp.dtos;

public class LogStepsResponse {

    private String id;

    private String userId;

    private String date;

    private int steps;

    public LogStepsResponse() {}

    public LogStepsResponse(String id, String userId, String date, int steps) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.steps = steps;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }
}