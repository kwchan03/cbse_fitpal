package com.fitpal.api.dtos;

public class WeeklySummaryDto {
    private Integer averageSteps;
    private Integer averageMinutes;
    private Integer averageCalories;

    public WeeklySummaryDto() {}

    public WeeklySummaryDto(Integer averageSteps, Integer averageMinutes, Integer averageCalories) {
        this.averageSteps = averageSteps;
        this.averageMinutes = averageMinutes;
        this.averageCalories = averageCalories;
    }

    public Integer getAverageSteps() { return averageSteps; }
    public void setAverageSteps(Integer averageSteps) { this.averageSteps = averageSteps; }
    public Integer getAverageMinutes() { return averageMinutes; }
    public void setAverageMinutes(Integer averageMinutes) { this.averageMinutes = averageMinutes; }
    public Integer getAverageCalories() { return averageCalories; }
    public void setAverageCalories(Integer averageCalories) { this.averageCalories = averageCalories; }
}