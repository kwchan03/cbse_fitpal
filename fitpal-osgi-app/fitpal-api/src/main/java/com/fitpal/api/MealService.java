package com.fitpal.api;

import com.fitpal.api.dtos.MealSearchResponse;
import com.fitpal.api.dtos.MealSearchResult;

public interface MealService {

    /**
     * Search for meals using Spoonacular API
     */
    MealSearchResponse searchMeal(String query, Integer page);

    /**
     * Get meal details by ID
     */
    MealSearchResult getMealById(Integer mealId);

    /**
     * Get nutrition image for a meal
     */
    byte[] getNutritionImage(Integer mealId);

    /**
     * Get recipe card image
     */
    String getRecipeImage(Integer mealId);

    /**
     * Add meal to user's favourites
     */
    User addMealToFavourite(String userId, Integer mealId, String foodName, String imageUrl);

    /**
     * Remove meal from user's favourites
     */
    void removeMealFromFavourite(String userId, Integer mealId);

    /**
     * Get user's favourite meals with pagination
     */
    MealSearchResponse getFavouriteMeals(String userId, Integer page);
}