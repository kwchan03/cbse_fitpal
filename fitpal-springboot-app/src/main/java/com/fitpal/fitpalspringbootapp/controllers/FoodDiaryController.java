package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.*;
import com.fitpal.fitpalspringbootapp.models.FoodDiary;
import com.fitpal.fitpalspringbootapp.services.FoodDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food-diary")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FoodDiaryController {

    @Autowired
    private FoodDiaryService foodDiaryService;

    /**
     * Recommend meals for a day
     */
    @PostMapping("/recommend-food")
    public ResponseEntity<?> recommendMeal(
            @RequestAttribute("userId") String userId,
            @RequestBody RecommendMealRequest request) {
        try {
            List<FoodDiary.Meal> meals = foodDiaryService.recommendMeal(
                    userId,
                    request.getTargetCalories(),
                    request.getDate()
            );
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get food diary by date
     */
    @GetMapping
    public ResponseEntity<?> getDiaryByDate(
            @RequestAttribute("userId") String userId,
            @RequestParam String date) {
        try {
            FoodDiary diary = foodDiaryService.getDiaryByDate(userId, date);
            return ResponseEntity.ok(diary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Add food to diary
     */
    @PostMapping
    public ResponseEntity<?> addFoodToDiary(
            @RequestAttribute("userId") String userId,
            @RequestBody AddFoodRequest request) {
        try {
            FoodDiary.Meal meal = foodDiaryService.addFoodToDiary(
                    userId,
                    request.getDate(),
                    request.getType(),
                    request.getMealId(),
                    request.getFoodName()
            );
            return ResponseEntity.ok(meal);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Remove food from diary
     */
    @DeleteMapping
    public ResponseEntity<?> removeFoodFromDiary(
            @RequestAttribute("userId") String userId,
            @RequestParam String date,
            @RequestParam String type,
            @RequestParam Integer mealId) {
        try {
            foodDiaryService.removeFoodFromDiary(userId, date, type, mealId);
            return ResponseEntity.ok(Map.of("message", "Meal removed from diary"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not available")) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", e.getMessage()));
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get calorie summary for a specific day
     */
    @GetMapping("/calorie-summary-day")
    public ResponseEntity<?> getCalorieSummaryByDay(
            @RequestAttribute("userId") String userId,
            @RequestParam String date) {
        try {
            Integer totalCalories = foodDiaryService.getCalorieSummaryByDay(userId, date);
            return ResponseEntity.ok(new CalorieSummaryResponse(totalCalories));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get calorie summary (daily or weekly)
     */
    @GetMapping("/calorie-summary")
    public ResponseEntity<?> getCalorieSummary(
            @RequestAttribute("userId") String userId,
            @RequestParam String mode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            List<Map> summary = foodDiaryService.getCalorieSummary(
                    userId, mode, startDate, endDate, month, year
            );
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Something went wrong"));
        }
    }
}