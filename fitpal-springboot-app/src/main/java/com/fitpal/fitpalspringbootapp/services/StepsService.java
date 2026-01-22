package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.models.Steps;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.StepsRepository;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class StepsService {

    @Autowired
    private StepsRepository stepsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistanceService distanceService;

    @Autowired
    private StepsCaloriesService caloriesService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Steps logSteps(String userId, String date, int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }
        
        // Get user to update totalDistance
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Calculate distance for this step count
        double distance = distanceService.calculateDistanceForSteps(steps, userId);
        
        // Calculate calories based on the calculated distance
        double calories = caloriesService.calculateCaloriesForDistance(distance, userId);
        
        // Accumulate distance to user's totalDistance
        double currentTotal = user.getTotalDistance() != null ? user.getTotalDistance() : 0.0;
        user.setTotalDistance(currentTotal + distance);
        userRepository.save(user);
        
        Steps stepLog = new Steps(userId, date, steps, (double) distance, (double) calories);
        return stepsRepository.save(stepLog);
    }

    public int getDailySteps(String userId, String date) {
        List<Steps> stepsList = stepsRepository.findByUserIdAndDate(userId, date);
        
        return stepsList.stream()
                        .mapToInt(Steps::getSteps)
                        .sum(); 
    }

    public int getWeeklySteps(String userId, String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String startStr = weekStart.format(DATE_FORMATTER);
        String endStr = weekEnd.format(DATE_FORMATTER);
        List<Steps> steps = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        return steps.stream().mapToInt(Steps::getSteps).sum();
    }

    public int getMonthlySteps(String userId, String month) {
        // Assume month is "yyyy-MM"
        LocalDate start = LocalDate.parse(month + "-01", DATE_FORMATTER);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        String startStr = start.format(DATE_FORMATTER);
        String endStr = end.format(DATE_FORMATTER);
        List<Steps> steps = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        return steps.stream().mapToInt(Steps::getSteps).sum();
    }

    public int getTotalSteps(String userId) {
        List<Steps> steps = stepsRepository.findByUserId(userId);
        return steps.stream().mapToInt(Steps::getSteps).sum();
    }
}
