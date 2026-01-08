package com.fitpal.fitpalspringbootapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String firstName = "";

    private String lastName = "";

    private String profilePictureUrl;

    private String gender;

    private LocalDate dob;

    private Double weight; // in kilograms

    private Double height;

    private Integer activityLevel;

    private Integer weightGoal; // -500, 0, or 500

    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean deactivated = false;

    private Double dailyTargetCalorie;

    private Integer dailyTargetSteps;

    private Double dailyTargetActivity;

    private List<FavouriteFood> favouriteFood = new ArrayList<>();

    // Inner class for embedded favourite food documents
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavouriteFood {
        private Integer mealId;
        private String foodName;
        private String imageUrl;
    }
}