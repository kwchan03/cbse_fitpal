package com.fitpal.fitpalspringbootapp.dtos.spoonacular;

import lombok.Data;
import java.util.List;

@Data
public class MealPlanResponse {
    private List<Meal> meals;
    private Nutrients nutrients;

    @Data
    public static class Meal {
        private Integer id;
        private String title;
        private Integer readyInMinutes;
        private Integer servings;
        private String sourceUrl;
    }

    @Data
    public static class Nutrients {
        private Double calories;
        private Double protein;
        private Double fat;
        private Double carbohydrates;
    }
}