package com.fitpal.fitpalspringbootapp.dtos.exercise;

import lombok.Data;

@Data
public class UpdateTargetsRequest {
    private Integer targetSteps;
    private Integer workoutMinutes;
    private Integer burnedCalories;
}
