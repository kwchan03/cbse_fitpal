package com.fitpal.api.dtos;

public class AddFoodRequest {
    private String date;
    private String type; // breakfast, lunch, dinner
    private Integer mealId;
    private String foodName;

    public AddFoodRequest() {
    }

    public AddFoodRequest(String date, String type, Integer mealId, String foodName) {
        this.date = date;
        this.type = type;
        this.mealId = mealId;
        this.foodName = foodName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
}