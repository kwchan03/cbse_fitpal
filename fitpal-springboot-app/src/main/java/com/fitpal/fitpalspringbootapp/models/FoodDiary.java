package com.fitpal.fitpalspringbootapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fooddiaries")
public class FoodDiary {

    @Id
    private String id;

    @DBRef
    private User user;

    private LocalDate date;

    private List<Meal> meals = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meal {
        private Integer mealId;
        private String foodName;
        private String imageUrl;
        private Integer calories;
        private Integer protein;
        private Integer fat;
        private Integer carbs;
        private String mealType; // breakfast, lunch, dinner
    }
}