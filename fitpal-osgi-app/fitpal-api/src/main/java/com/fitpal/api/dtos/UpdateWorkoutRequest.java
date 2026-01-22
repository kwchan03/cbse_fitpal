package com.fitpal.api.dtos;

public class UpdateWorkoutRequest {
    private String workoutId;
    private String date;
    private String startTime;
    private String time;
    private Integer sets;
    private Integer reps;

    public String getWorkoutId() { return workoutId; }
    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
}