package com.fitpal.api;

import com.fitpal.api.dtos.LogStepsRequest;

public interface StepsService {
    Steps logSteps(LogStepsRequest request, String userId);
    int getDailySteps(String userId, String date);
    int getWeeklySteps(String userId, String date);
    int getMonthlySteps(String userId, String month);
    int getTotalSteps(String userId);
}
