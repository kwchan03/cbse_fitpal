package com.fitpal.fitpalspringbootapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exerciselogs")
public class ExerciseLog {

    @Id
    private String id;

    @DBRef
    private User user;

    private LocalDate date;

    // Added in to avoid breaking frontend - can check
    private Integer steps = 0;

    private List<WorkoutEntry> workout = new ArrayList<>();
    private List<CardioEntry> cardio = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutEntry {
        @Field("_id")
        @JsonIgnore
        private ObjectId id;
        
        private String name;
        private String startTime;
        private Integer sets;
        private Integer reps;
        
        @JsonProperty("_id")
        public String get_id() {
            return id == null ? null : id.toHexString();
        }

        @JsonProperty("_id")
        public void set_id(String value) {
            if (value != null && ObjectId.isValid(value)) this.id = new ObjectId(value);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardioEntry {
        @Field("_id")
        @JsonIgnore
        private ObjectId id;
        
        private String name;
        private String startTime;
        private Integer duration;
        private Integer caloriesBurned;

        @JsonProperty("_id")
        public String get_id() {
            return id == null ? null : id.toHexString();
        }

        @JsonProperty("_id")
        public void set_id(String value) {
            if (value != null && ObjectId.isValid(value)) this.id = new ObjectId(value);
        }
    }
}
