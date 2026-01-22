package com.fitpal.api.dtos;

public class UpdateTargetsRequest {
    private Integer targetSteps;
    private Integer workoutMinutes;
    private Integer burnedCalories;

    public Integer getTargetSteps() { return targetSteps; }
    public void setTargetSteps(Integer targetSteps) { this.targetSteps = targetSteps; }
    public Integer getWorkoutMinutes() { return workoutMinutes; }
    public void setWorkoutMinutes(Integer workoutMinutes) { this.workoutMinutes = workoutMinutes; }
    public Integer getBurnedCalories() { return burnedCalories; }
    public void setBurnedCalories(Integer burnedCalories) { this.burnedCalories = burnedCalories; }
}