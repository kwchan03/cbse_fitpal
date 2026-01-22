package com.fitpal.api;

import org.bson.types.ObjectId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExerciseLog {

    private String id;
    private User user;
    private LocalDate date;
    private List<WorkoutEntry> workout = new ArrayList<>();
    private List<CardioEntry> cardio = new ArrayList<>();

    public static class WorkoutEntry {
        @JsonIgnore
        private ObjectId id;
        private String name;
        private String startTime;
        private Integer sets;
        private Integer reps;

        public WorkoutEntry() {}

        public WorkoutEntry(ObjectId id, String name, String startTime, Integer sets, Integer reps) {
            this.id = id;
            this.name = name;
            this.startTime = startTime;
            this.sets = sets;
            this.reps = reps;
        }

        @JsonProperty("_id")
        public String get_id() {
            return id == null ? null : id.toHexString();
        }

        @JsonProperty("_id")
        public void set_id(String value) {
            if (value != null && ObjectId.isValid(value)) 
                this.id = new ObjectId(value);
        }

        public ObjectId getId() { return id; }
        public void setId(ObjectId id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public Integer getSets() { return sets; }
        public void setSets(Integer sets) { this.sets = sets; }
        public Integer getReps() { return reps; }
        public void setReps(Integer reps) { this.reps = reps; }
    }

    public static class CardioEntry {
        @JsonIgnore
        private ObjectId id;
        private String name;
        private String startTime;
        private Integer duration;
        private Integer caloriesBurned;

        public CardioEntry() {}

        public CardioEntry(ObjectId id, String name, String startTime, Integer duration, Integer caloriesBurned) {
            this.id = id;
            this.name = name;
            this.startTime = startTime;
            this.duration = duration;
            this.caloriesBurned = caloriesBurned;
        }

        @JsonProperty("_id")
        public String get_id() {
            return id == null ? null : id.toHexString();
        }

        @JsonProperty("_id")
        public void set_id(String value) {
            if (value != null && ObjectId.isValid(value)) 
                this.id = new ObjectId(value);
        }

        public ObjectId getId() { return id; }
        public void setId(ObjectId id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public Integer getCaloriesBurned() { return caloriesBurned; }
        public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public List<WorkoutEntry> getWorkout() { return workout; }
    public void setWorkout(List<WorkoutEntry> workout) { this.workout = workout; }
    public List<CardioEntry> getCardio() { return cardio; }
    public void setCardio(List<CardioEntry> cardio) { this.cardio = cardio; }
}
