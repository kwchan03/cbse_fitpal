package com.fitpal.service;

import com.fitpal.api.Steps;
import com.fitpal.api.StepsCaloriesService;
import com.fitpal.api.User;
import com.fitpal.service.db.StepsRepository;
import com.fitpal.service.db.UserRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component(service = StepsCaloriesService.class)
public class StepsCaloriesServiceImpl implements StepsCaloriesService {

    @Reference
    private StepsRepository stepsRepository;

    @Reference
    private UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public double getDailyCalories(String userId, String date) {
        List<Steps> dailySteps = stepsRepository.findByUserIdAndDate(userId, date);
        
        return dailySteps.stream()
                .mapToDouble(Steps::getCalories)
                .sum();
    }

    @Override
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

    @Override
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

    @Override
    public double calculateCaloriesForDistance(double distance, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return calculateCalories(distance, user.getWeight());
    }

    private double calculateCalories(double distance, Double weight) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight is required to calculate calories.");
        }
        return distance * weight * 0.57;
    }
}
