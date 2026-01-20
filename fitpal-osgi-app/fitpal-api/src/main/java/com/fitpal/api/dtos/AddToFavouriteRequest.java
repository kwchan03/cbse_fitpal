package com.fitpal.api.dtos;

public class AddToFavouriteRequest {
    private Integer mealId;
    private String foodName;
    private String imageUrl;

    public AddToFavouriteRequest() {
    }

    public AddToFavouriteRequest(Integer mealId, String foodName, String imageUrl) {
        this.mealId = mealId;
        this.foodName = foodName;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}