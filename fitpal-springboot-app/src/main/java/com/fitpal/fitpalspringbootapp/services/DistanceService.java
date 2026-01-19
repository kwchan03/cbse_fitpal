package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistanceService {

    @Autowired
    private StepsService stepsService;

    @Autowired
    private UserService userService;

    public double getDailyDistance(String userId, String date) {
        int steps = stepsService.getDailySteps(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    public double getWeeklyDistance(String userId, String date) {
        int steps = stepsService.getWeeklySteps(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    public double getMonthlyDistance(String userId, String month) {
        int steps = stepsService.getMonthlySteps(userId, month);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    public double getTotalDistance(String userId) {
        int steps = stepsService.getTotalSteps(userId);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    private double calculateDistance(int steps, Double height) {
        if (height == null) {
            throw new IllegalArgumentException("Height is required to calculate distance.");
        }
        return (steps * height * 0.414) / 100000.0;
    }
}