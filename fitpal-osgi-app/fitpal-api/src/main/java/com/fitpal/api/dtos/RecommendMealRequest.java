package com.fitpal.api.dtos;

public class RecommendMealRequest {
    private Integer targetCalories;
    private String date; // ISO date format: yyyy-MM-dd

    public RecommendMealRequest() {
    }

    public RecommendMealRequest(Integer targetCalories, String date) {
        this.targetCalories = targetCalories;
        this.date = date;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}