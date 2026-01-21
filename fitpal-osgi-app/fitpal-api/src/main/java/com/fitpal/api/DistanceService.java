package com.fitpal.api;

public interface DistanceService {
    double getDailyDistance(String userId, String date);
    double getWeeklyDistance(String userId, String date);
    double getMonthlyDistance(String userId, String month);
    double getTotalDistance(String userId);
    double calculateDistanceForSteps(int steps, String userId);
}
