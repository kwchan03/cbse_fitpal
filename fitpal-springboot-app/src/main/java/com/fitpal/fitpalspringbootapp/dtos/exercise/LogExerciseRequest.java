package com.fitpal.fitpalspringbootapp.dtos.exercise;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class LogExerciseRequest {
    private LocalDate date;
    private Integer steps;
    private List<WorkoutItem> workout = new ArrayList<>();
    private List<CardioItem> cardio = new ArrayList<>();

    @Data
    public static class WorkoutItem {
        private String name;
        private String startTime;
        private String time; 
        private Integer sets;
        private Integer reps;
    }

    @Data
    public static class CardioItem {
        private String name;
        private String startTime;
        private String time; 
        private Integer duration;
    }
}
