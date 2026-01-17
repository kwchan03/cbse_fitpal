package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.exercise.*;
import com.fitpal.fitpalspringbootapp.models.ExerciseLog;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<?> createExercise(
            @RequestAttribute("userId") String userId,
            @RequestBody LogExerciseRequest request
    ) {
        try {
            ExerciseLog log = exerciseService.createExercise(userId, request);
            return ResponseEntity.ok(log);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to log exercise"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getExercises(@RequestAttribute("userId") String userId) {
        try {
            return ResponseEntity.ok(exerciseService.getExercises(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch exercises"));
        }
    }

    @PutMapping("/target")
    public ResponseEntity<?> setDailyTarget(
            @RequestAttribute("userId") String userId,
            @RequestBody UpdateTargetsRequest request
    ) {
        try {
            User updated = exerciseService.updateTargets(userId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    @PostMapping("/steps")
    public ResponseEntity<?> logDailySteps(
            @RequestAttribute("userId") String userId,
            @RequestBody LogStepsRequest request
    ) {
        try {
            ExerciseLog updated = exerciseService.logSteps(userId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to log daily steps"));
        }
    }

    @GetMapping("/steps/today")
    public ResponseEntity<?> fetchStepsToday(@RequestAttribute("userId") String userId) {
        try {
            return ResponseEntity.ok(exerciseService.fetchStepsToday(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    @GetMapping("/cardio/duration")
    public ResponseEntity<?> fetchCardioDuration(@RequestAttribute("userId") String userId) {
        try {
            return ResponseEntity.ok(exerciseService.fetchCardioDurationToday(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    @GetMapping("/calories/burned")
    public ResponseEntity<?> fetchCaloriesBurned(@RequestAttribute("userId") String userId) {
        try {
            return ResponseEntity.ok(exerciseService.fetchCaloriesBurnedToday(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    // Frontend calls /summary/weekly?dates=...
    @GetMapping("/summary/weekly")
    public ResponseEntity<?> fetchWeeklyAverages(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) String dates
    ) {
        try {
            return ResponseEntity.ok(exerciseService.fetchWeeklySummary(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    // Aggregations used by hooks
    @GetMapping("/calorie-out-summary")
    public ResponseEntity<?> getCalorieOutSummary(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false, defaultValue = "daily") String mode
    ) {
        try {
            List<Document> res = exerciseService.getCalorieOutSummary(userId, mode);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to summarize calories out"));
        }
    }

    @GetMapping("/cardio-vs-workout-summary")
    public ResponseEntity<?> getCardioVsWorkoutSummary(
            @RequestAttribute("userId") String userId,
            @RequestParam String mode,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            List<Document> res = exerciseService.getCardioVsWorkoutSummary(
                    userId,
                    mode,
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate)
            );
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch summary"));
        }
    }

    // Update/Delete subdocs
    @PutMapping("/update/cardio/{id}")
    public ResponseEntity<?> updateCardioExercise(
            @RequestAttribute("userId") String userId,
            @PathVariable("id") String id,
            @RequestBody UpdateCardioRequest request
    ) {
        try {
            return ResponseEntity.ok(exerciseService.updateCardioExercise(userId, id, request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update exercise"));
        }
    }

    @PutMapping("/update/workout/{id}")
    public ResponseEntity<?> updateWorkoutExercise(
            @RequestAttribute("userId") String userId,
            @PathVariable("id") String id,
            @RequestBody UpdateWorkoutRequest request
    ) {
        try {
            return ResponseEntity.ok(exerciseService.updateWorkoutExercise(userId, id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update workout exercise"));
        }
    }


    @DeleteMapping("/delete/cardio/{id}")
    public ResponseEntity<?> deleteCardioExercise(
            @RequestAttribute("userId") String userId,
            @PathVariable("id") String id
    ) {
        try {
            ExerciseLog updated = exerciseService.deleteCardioExercise(userId, id);
            return ResponseEntity.ok(Map.of(
                    "message", "Cardio exercise deleted successfully",
                    "deletedExercise", updated
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to delete exercise"));
        }
    }

    @DeleteMapping("/delete/workout/{id}")
    public ResponseEntity<?> deleteWorkoutExercise(
            @RequestAttribute("userId") String userId,
            @PathVariable("id") String id
    ) {
        try {
            ExerciseLog updated = exerciseService.deleteWorkoutExercise(userId, id);
            return ResponseEntity.ok(Map.of(
                    "message", "Workout exercise deleted successfully",
                    "deletedExercise", updated
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to delete workout exercise"));
        }
    }
}
