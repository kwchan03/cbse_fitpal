package com.fitpal.api.dtos;

public class CalorieSummaryResponse {
    private Integer totalCalories;

    public CalorieSummaryResponse() {
    }

    public CalorieSummaryResponse(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Integer getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }
}