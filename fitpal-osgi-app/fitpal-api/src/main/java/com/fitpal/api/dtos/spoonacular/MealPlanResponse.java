package com.fitpal.api.dtos.spoonacular;

import java.util.List;

public class MealPlanResponse {
    private List<Meal> meals;
    private Nutrients nutrients;

    public MealPlanResponse() {
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public Nutrients getNutrients() {
        return nutrients;
    }

    public void setNutrients(Nutrients nutrients) {
        this.nutrients = nutrients;
    }

    // Nested Meal class
    public static class Meal {
        private Integer id;
        private String title;
        private Integer readyInMinutes;
        private Integer servings;
        private String sourceUrl;

        public Meal() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getReadyInMinutes() {
            return readyInMinutes;
        }

        public void setReadyInMinutes(Integer readyInMinutes) {
            this.readyInMinutes = readyInMinutes;
        }

        public Integer getServings() {
            return servings;
        }

        public void setServings(Integer servings) {
            this.servings = servings;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public void setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
        }
    }

    // Nested Nutrients class
    public static class Nutrients {
        private Double calories;
        private Double protein;
        private Double fat;
        private Double carbohydrates;

        public Nutrients() {
        }

        public Double getCalories() {
            return calories;
        }

        public void setCalories(Double calories) {
            this.calories = calories;
        }

        public Double getProtein() {
            return protein;
        }

        public void setProtein(Double protein) {
            this.protein = protein;
        }

        public Double getFat() {
            return fat;
        }

        public void setFat(Double fat) {
            this.fat = fat;
        }

        public Double getCarbohydrates() {
            return carbohydrates;
        }

        public void setCarbohydrates(Double carbohydrates) {
            this.carbohydrates = carbohydrates;
        }
    }
}