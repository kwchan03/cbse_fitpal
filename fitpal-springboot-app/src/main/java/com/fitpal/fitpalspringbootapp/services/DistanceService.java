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
public class DistanceService {

    @Autowired
    private StepsRepository stepsRepository;

    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public double getDailyDistance(String userId, String date) {
        List<Steps> stepsList = stepsRepository.findByUserIdAndDate(userId, date);
        return stepsList.stream()
                .mapToDouble(Steps::getDistance)
                .sum();
    }

    public double getWeeklyDistance(String userId, String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String startStr = weekStart.format(DATE_FORMATTER);
        String endStr = weekEnd.format(DATE_FORMATTER);
        List<Steps> stepsList = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        return stepsList.stream()
                .mapToDouble(Steps::getDistance)
                .sum();
    }

    public double getMonthlyDistance(String userId, String month) {
        // Assume month is "yyyy-MM"
        LocalDate start = LocalDate.parse(month + "-01", DATE_FORMATTER);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        String startStr = start.format(DATE_FORMATTER);
        String endStr = end.format(DATE_FORMATTER);
        List<Steps> stepsList = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        return stepsList.stream()
                .mapToDouble(Steps::getDistance)
                .sum();
    }

    public double calculateDistanceForSteps(int steps, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return calculateDistance(steps, user.getHeight());
    }

    private double calculateDistance(int steps, Double height) {
        if (height == null) {
            throw new IllegalArgumentException("Height is required to calculate distance.");
        }
        return (steps * height * 0.414) / 100000.0;
    }
}