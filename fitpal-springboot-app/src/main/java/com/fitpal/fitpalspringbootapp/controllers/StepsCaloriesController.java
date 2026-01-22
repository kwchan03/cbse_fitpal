package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.StepsCaloriesResponse;
import com.fitpal.fitpalspringbootapp.services.StepsCaloriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/steps-calories")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StepsCaloriesController {

    @Autowired
    private StepsCaloriesService caloriesService;

    @GetMapping("/daily")
    public ResponseEntity<StepsCaloriesResponse> getDailyCalories(
        @RequestParam String userId, @RequestParam String date) {
        double calories = caloriesService.getDailyCalories(userId, date);
        return ResponseEntity.ok(new StepsCaloriesResponse(calories));
    }

    @GetMapping("/weekly")
    public ResponseEntity<StepsCaloriesResponse> getWeeklyCalories(
        @RequestParam String userId, @RequestParam String date) {
        double calories = caloriesService.getWeeklyCalories(userId, date);
        return ResponseEntity.ok(new StepsCaloriesResponse(calories));
    }

    @GetMapping("/monthly")
    public ResponseEntity<StepsCaloriesResponse> getMonthlyCalories(
        @RequestParam String userId, @RequestParam String month) {
        double calories = caloriesService.getMonthlyCalories(userId, month);
        return ResponseEntity.ok(new StepsCaloriesResponse(calories));
    }
}