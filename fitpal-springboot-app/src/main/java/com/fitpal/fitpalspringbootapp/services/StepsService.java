package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.models.Steps;
import com.fitpal.fitpalspringbootapp.repositories.StepsRepository;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Steps logSteps(String userId, String date, int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }
        Steps stepLog = new Steps(userId, date, steps);
        return stepsRepository.save(stepLog);
    }

    public int getDailySteps(String userId, String date) {
        List<Steps> steps = stepsRepository.findByUserIdAndDateBetween(userId, date, date);
        return steps.stream().mapToInt(Steps::getSteps).sum();
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
}
