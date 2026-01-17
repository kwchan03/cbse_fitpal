package com.fitpal.fitpalspringbootapp.dtos.exercise;

import lombok.Data;

@Data
public class UpdateWorkoutRequest {
    private String workoutId;
    private String date;
    private String startTime;
    private String time;
    private Integer sets;
    private Integer reps;
}
