package com.fitpal.service;

import com.fitpal.api.DistanceService;
import com.fitpal.api.StepsService;
import com.fitpal.api.User;
import com.fitpal.api.UserService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = DistanceService.class)
public class DistanceServiceImpl implements DistanceService {

    @Reference
    private StepsService stepsService;

    @Reference
    private UserService userService;

    @Override
    public double getDailyDistance(String userId, String date) {
        int steps = stepsService.getDailySteps(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    @Override
    public double getWeeklyDistance(String userId, String date) {
        int steps = stepsService.getWeeklySteps(userId, date);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    @Override
    public double getMonthlyDistance(String userId, String month) {
        int steps = stepsService.getMonthlySteps(userId, month);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    @Override
    public double getTotalDistance(String userId) {
        int steps = stepsService.getTotalSteps(userId);
        User user = userService.getUserInfo(userId);
        return calculateDistance(steps, user.getHeight());
    }

    private double calculateDistance(int steps, Double height) {
        if (height == null) {
            throw new IllegalArgumentException("Height is required to calculate distance.");
        }
        // The formula to calculate distance in km is: (steps * stride_length_in_m) / 1000
        // A common estimation for stride length is height_in_cm * 0.414
        // So, stride_length_in_m = (height_in_cm * 0.414) / 100
        // distance_km = (steps * (height_in_cm * 0.414 / 100)) / 1000
        // distance_km = (steps * height_in_cm * 0.414) / 100000
        return (steps * height * 0.414) / 100000.0;
    }
}
