package com.fitpal.service;

import com.fitpal.api.DistanceService;
import com.fitpal.api.StepsCaloriesService;
import com.fitpal.api.User;
import com.fitpal.api.UserService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = StepsCaloriesService.class)
public class StepsCaloriesServiceImpl implements StepsCaloriesService {

    @Reference
    private DistanceService distanceService;

    @Reference
    private UserService userService;

    @Override
    public double getDailyCalories(String userId, String date) {
        double distance = distanceService.getDailyDistance(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateCalories(distance, user.getWeight());
    }

    @Override
    public double getWeeklyCalories(String userId, String date) {
        double distance = distanceService.getWeeklyDistance(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateCalories(distance, user.getWeight());
    }

    @Override
    public double getMonthlyCalories(String userId, String month) {
        double distance = distanceService.getMonthlyDistance(userId, month);
        User user = userService.getUserInfo(userId);
        return calculateCalories(distance, user.getWeight());
    }

    private double calculateCalories(double distance, Double weight) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight is required to calculate calories.");
        }
        // Calorie burn formula: distance (km) * weight (kg) * constant
        return distance * weight * 0.57;
    }
}
