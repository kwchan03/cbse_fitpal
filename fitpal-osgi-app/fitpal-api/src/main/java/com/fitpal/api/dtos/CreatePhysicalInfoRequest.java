package com.fitpal.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePhysicalInfoRequest {

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("height")
    private Double height;

    @JsonProperty("activityLevel")
    private Double activityLevel;

    @JsonProperty("weightGoal")
    private Integer weightGoal;

    // Constructors
    public CreatePhysicalInfoRequest() {
    }

    public CreatePhysicalInfoRequest(Double weight, Double height, Double activityLevel, Integer weightGoal) {
        this.weight = weight;
        this.height = height;
        this.activityLevel = activityLevel;
        this.weightGoal = weightGoal;
    }

    // Getters & Setters
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(Double activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Integer getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(Integer weightGoal) {
        this.weightGoal = weightGoal;
    }
}
