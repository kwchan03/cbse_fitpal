package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.models.Steps;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import com.fitpal.fitpalspringbootapp.repositories.StepsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class StepsCaloriesService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StepsRepository stepsRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public double getDailyCalories(String userId, String date) {
        List<Steps> dailySteps = stepsRepository.findByUserIdAndDate(userId, date);
        
        return dailySteps.stream()
                .mapToDouble(Steps::getCalories)
                .sum();
    }

    public double getWeeklyCalories(String userId, String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        String startStr = weekStart.format(DATE_FORMATTER);
        String endStr = weekEnd.format(DATE_FORMATTER);
        
        List<Steps> weeklySteps = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        
        return weeklySteps.stream()
                .mapToDouble(Steps::getCalories)
                .sum();
    }

    public double getMonthlyCalories(String userId, String month) {
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        String startStr = startDate.format(DATE_FORMATTER);
        String endStr = endDate.format(DATE_FORMATTER);
        
        List<Steps> monthlySteps = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        
        return monthlySteps.stream()
                .mapToDouble(Steps::getCalories)
                .sum();
    }

    public double calculateCaloriesForDistance(double distance, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return calculateCalories(distance, user.getWeight());
    }

    private double calculateCalories(double distance, Double weight) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight is required to calculate calories.");
        }
        return distance * weight * 0.57;
    }
}