package com.fitpal.api.dtos;

public class LogStepsRequest {

    private String date;
    private int steps;

    public LogStepsRequest() {}

    public LogStepsRequest(String date, int steps) {
        this.date = date;
        this.steps = steps;
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
