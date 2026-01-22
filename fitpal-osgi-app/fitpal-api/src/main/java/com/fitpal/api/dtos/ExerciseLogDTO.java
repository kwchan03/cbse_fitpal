package com.fitpal.api.dtos;

import com.fitpal.api.ExerciseLog;
import com.fitpal.api.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for REST responses - uses String for date instead of LocalDate
 * to avoid Jackson JSR310 serialization issues
 */
public class ExerciseLogDTO {

    private String id;
    private User user;      // keep as User for now (minimal change)
    private String date;    // String instead of LocalDate
    private List<ExerciseLog.WorkoutEntry> workout = new ArrayList<>();
    private List<ExerciseLog.CardioEntry> cardio = new ArrayList<>();

    public ExerciseLogDTO() {}

    public ExerciseLogDTO(String id, User user, String date,
                          List<ExerciseLog.WorkoutEntry> workout,
                          List<ExerciseLog.CardioEntry> cardio) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.workout = workout;
        this.cardio = cardio;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<ExerciseLog.WorkoutEntry> getWorkout() { return workout; }
    public void setWorkout(List<ExerciseLog.WorkoutEntry> workout) { this.workout = workout; }

    public List<ExerciseLog.CardioEntry> getCardio() { return cardio; }
    public void setCardio(List<ExerciseLog.CardioEntry> cardio) { this.cardio = cardio; }

    /**
     * Convert from ExerciseLog entity to DTO
     */
    public static ExerciseLogDTO fromEntity(ExerciseLog log) {
        if (log == null) return null;

        ExerciseLogDTO dto = new ExerciseLogDTO();
        dto.setId(log.getId());
        dto.setUser(log.getUser());
        dto.setDate(log.getDate() != null ? log.getDate().toString() : null);
        dto.setWorkout(log.getWorkout());
        dto.setCardio(log.getCardio());
        return dto;
    }
}
