package com.fitpal.api;

// import com.fitpal.api.ExerciseLog;
// import com.fitpal.api.User;
import com.fitpal.api.dtos.*;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExerciseService {

    ExerciseLog createExercise(String userId, LogExerciseRequest req);
    
    List<ExerciseLog> getExercises(String userId);
    
    User updateTargets(String userId, UpdateTargetsRequest req);
    
    Map<String, Integer> fetchCardioDurationToday(String userId);
    
    Map<String, Integer> fetchCaloriesBurnedToday(String userId);
    
    ExerciseLog recalcCardioCaloriesForDate(String userId, LocalDate date);
    
    WeeklySummaryDto fetchWeeklySummary(String userId);
    
    ExerciseLog updateCardioExercise(String userId, String cardioId, UpdateCardioRequest req);
    
    ExerciseLog updateWorkoutExercise(String userId, String workoutId, UpdateWorkoutRequest req);
    
    ExerciseLog deleteCardioExercise(String userId, String cardioId);
    
    ExerciseLog deleteWorkoutExercise(String userId, String workoutId);
    
    List<Document> getCalorieOutSummary(String userId, String mode);
    
    List<Document> getCardioVsWorkoutSummary(String userId, String mode, LocalDate startDate, LocalDate endDate);
}