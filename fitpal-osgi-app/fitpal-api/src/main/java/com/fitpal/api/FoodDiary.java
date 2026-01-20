package com.fitpal.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FoodDiary {

    private String id;
    private String user; // Reference to User ObjectId as String (matches MongoDB field name)
    private LocalDate date;
    private List<Meal> meals = new ArrayList<>();

    public FoodDiary() {
    }

    public FoodDiary(String id, String user, LocalDate date, List<Meal> meals) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.meals = meals;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    // Inner class: Meal
    public static class Meal {
        private Integer mealId;
        private String foodName;
        private String imageUrl;
        private Integer calories;
        private Integer protein;
        private Integer fat;
        private Integer carbs;
        private String mealType; // breakfast, lunch, dinner

        public Meal() {
        }

        public Meal(Integer mealId, String foodName, String imageUrl, Integer calories,
                    Integer protein, Integer fat, Integer carbs, String mealType) {
            this.mealId = mealId;
            this.foodName = foodName;
            this.imageUrl = imageUrl;
            this.calories = calories;
            this.protein = protein;
            this.fat = fat;
            this.carbs = carbs;
            this.mealType = mealType;
        }

        // Getters and Setters
        public Integer getMealId() {
            return mealId;
        }

        public void setMealId(Integer mealId) {
            this.mealId = mealId;
        }

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getCalories() {
            return calories;
        }

        public void setCalories(Integer calories) {
            this.calories = calories;
        }

        public Integer getProtein() {
            return protein;
        }

        public void setProtein(Integer protein) {
            this.protein = protein;
        }

        public Integer getFat() {
            return fat;
        }

        public void setFat(Integer fat) {
            this.fat = fat;
        }

        public Integer getCarbs() {
            return carbs;
        }

        public void setCarbs(Integer carbs) {
            this.carbs = carbs;
        }

        public String getMealType() {
            return mealType;
        }

        public void setMealType(String mealType) {
            this.mealType = mealType;
        }
    }
}