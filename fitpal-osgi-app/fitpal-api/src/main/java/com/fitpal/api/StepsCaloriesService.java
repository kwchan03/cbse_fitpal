package com.fitpal.api;

public interface StepsCaloriesService {
    double getDailyCalories(String userId, String date);
    double getWeeklyCalories(String userId, String date);
    double getMonthlyCalories(String userId, String month);
    double calculateCaloriesForDistance(double distance, String userId);
}
