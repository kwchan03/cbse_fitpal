package com.fitpal.api;

import java.util.List;
import java.util.Map;

public interface FoodDiaryService {

    /**
     * Recommend meals using Spoonacular API and save to diary
     */
    List<FoodDiary.Meal> recommendMeal(String userId, Integer targetCalories, String dateStr);

    /**
     * Get food diary by date
     */
    FoodDiary getDiaryByDate(String userId, String dateStr);

    /**
     * Add food to diary
     */
    FoodDiary.Meal addFoodToDiary(String userId, String dateStr, String type,
                                  Integer mealId, String foodName);

    /**
     * Remove food from diary
     */
    void removeFoodFromDiary(String userId, String dateStr, String type, Integer mealId);

    /**
     * Get calorie summary for a specific day
     */
    Integer getCalorieSummaryByDay(String userId, String dateStr);

    /**
     * Get calorie summary (daily or weekly aggregation)
     */
    List<Map> getCalorieSummary(String userId, String mode,
                                String startDate, String endDate,
                                Integer month, Integer year);
}
