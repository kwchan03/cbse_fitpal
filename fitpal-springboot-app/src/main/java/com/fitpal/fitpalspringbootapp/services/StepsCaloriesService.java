package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StepsCaloriesService {

    @Autowired
    private DistanceService distanceService;

    @Autowired
    private UserService userService;

    public double getDailyCalories(String userId, String date) {
        double distance = distanceService.getDailyDistance(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateCalories(distance, user.getWeight());
    }

    public double getWeeklyCalories(String userId, String date) {
        double distance = distanceService.getWeeklyDistance(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateCalories(distance, user.getWeight());
    }

    public double getMonthlyCalories(String userId, String month) {
        double distance = distanceService.getMonthlyDistance(userId, month);
        User user = userService.getUserInfo(userId);
        return calculateCalories(distance, user.getWeight());
    }

    private double calculateCalories(double distance, Double weight) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight is required to calculate calories.");
        }
        return distance * weight * 0.57;
    }
}