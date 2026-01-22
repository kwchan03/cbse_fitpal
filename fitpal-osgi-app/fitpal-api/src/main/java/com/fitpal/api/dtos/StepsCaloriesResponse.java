package com.fitpal.api.dtos;

public class StepsCaloriesResponse {

    private double calories;

    public StepsCaloriesResponse() {}

    public StepsCaloriesResponse(double calories) {
        this.calories = calories;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }
}
