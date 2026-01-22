package com.fitpal.api.dtos;

import java.util.ArrayList;
import java.util.List;

public class LogExerciseRequest {
    private String date;
    private Integer steps;
    private List<WorkoutItem> workout = new ArrayList<>();
    private List<CardioItem> cardio = new ArrayList<>();

    public static class WorkoutItem {
        private String name;
        private String startTime;
        private String time;
        private Integer sets;
        private Integer reps;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public Integer getSets() { return sets; }
        public void setSets(Integer sets) { this.sets = sets; }
        public Integer getReps() { return reps; }
        public void setReps(Integer reps) { this.reps = reps; }
    }

    public static class CardioItem {
        private String name;
        private String startTime;
        private String time;
        private Integer duration;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps; }
    public List<WorkoutItem> getWorkout() { return workout; }
    public void setWorkout(List<WorkoutItem> workout) { this.workout = workout; }
    public List<CardioItem> getCardio() { return cardio; }
    public void setCardio(List<CardioItem> cardio) { this.cardio = cardio; }
}
