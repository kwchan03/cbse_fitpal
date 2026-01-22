package com.fitpal.service;

import com.fitpal.api.DistanceService;
import com.fitpal.api.Steps;
import com.fitpal.api.User;
import com.fitpal.service.db.UserRepository;
import com.fitpal.service.db.StepsRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component(service = DistanceService.class)
public class DistanceServiceImpl implements DistanceService {

    @Reference
    private StepsRepository stepsRepository;

    @Reference
    private UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public double getDailyDistance(String userId, String date) {
        List<Steps> stepsList = stepsRepository.findByUserIdAndDate(userId, date);
        return stepsList.stream()
                .mapToDouble(Steps::getDistance)
                .sum();
    }

    @Override
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

    @Override
    public double getMonthlyDistance(String userId, String month) {
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
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return calculateDistance(steps, user.getHeight());
    }

    private double calculateDistance(int steps, Double height) {
        if (height == null) {
            throw new IllegalArgumentException("Height is required to calculate distance.");
        }
        return (steps * height * 0.414) / 100000.0;
    }
}
