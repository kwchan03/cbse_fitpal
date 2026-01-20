package com.fitpal.api.dtos;

import com.fitpal.api.FoodDiary;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for REST responses - uses String for date instead of LocalDate
 * to avoid Jackson JSR310 serialization issues
 */
public class FoodDiaryDTO {

    private String id;
    private String user;
    private String date; // String instead of LocalDate
    private List<FoodDiary.Meal> meals = new ArrayList<>();

    public FoodDiaryDTO() {
    }

    public FoodDiaryDTO(String id, String user, String date, List<FoodDiary.Meal> meals) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<FoodDiary.Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<FoodDiary.Meal> meals) {
        this.meals = meals;
    }

    /**
     * Convert from FoodDiary entity to DTO
     */
    public static FoodDiaryDTO fromEntity(FoodDiary foodDiary) {
        if (foodDiary == null) {
            return null;
        }

        FoodDiaryDTO dto = new FoodDiaryDTO();
        dto.setId(foodDiary.getId());
        dto.setUser(foodDiary.getUser());
        dto.setDate(foodDiary.getDate() != null ? foodDiary.getDate().toString() : null);
        dto.setMeals(foodDiary.getMeals());

        return dto;
    }
}