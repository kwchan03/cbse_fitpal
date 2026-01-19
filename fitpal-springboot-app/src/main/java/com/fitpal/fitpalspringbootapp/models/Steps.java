package com.fitpal.fitpalspringbootapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "steps")
public class Steps {

    @Id
    private String id;

    private String userId;

    private String date;

    private int steps;

    // Constructors
    public Steps() {}

    public Steps(String userId, String date, int steps) {
        this.userId = userId;
        this.date = date;
        this.steps = steps;
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
}
