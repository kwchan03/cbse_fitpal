package com.fitpal.service;

import com.fitpal.api.Steps;
import com.fitpal.api.StepsService;
import com.fitpal.api.dtos.LogStepsRequest;
import com.fitpal.service.db.StepsRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component(service = StepsService.class)
public class StepsServiceImpl implements StepsService {

    @Reference
    private StepsRepository stepsRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Steps logSteps(LogStepsRequest request, String userId) {
        if (request.getSteps() <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }
        String date = request.getDate();
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().toString();
        }
        Steps stepLog = new Steps(userId, date, request.getSteps());
        return stepsRepository.save(stepLog);
    }

    @Override
    public int getDailySteps(String userId, String date) {
        List<Steps> stepsList = stepsRepository.findByUserIdAndDate(userId, date);
        return stepsList.stream()
                .mapToInt(Steps::getSteps)
                .sum();
    }

    @Override
    public int getWeeklySteps(String userId, String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        String startStr = weekStart.format(DATE_FORMATTER);
        String endStr = weekEnd.format(DATE_FORMATTER);
        List<Steps> steps = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        return steps.stream().mapToInt(Steps::getSteps).sum();
    }

    @Override
    public int getMonthlySteps(String userId, String month) {
        // Assume month is "yyyy-MM"
        LocalDate start = LocalDate.parse(month + "-01", DATE_FORMATTER);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        String startStr = start.format(DATE_FORMATTER);
        String endStr = end.format(DATE_FORMATTER);
        List<Steps> steps = stepsRepository.findByUserIdAndDateBetween(userId, startStr, endStr);
        return steps.stream().mapToInt(Steps::getSteps).sum();
    }

    @Override
    public int getTotalSteps(String userId) {
        List<Steps> steps = stepsRepository.findByUserId(userId);
        return steps.stream().mapToInt(Steps::getSteps).sum();
    }
}
