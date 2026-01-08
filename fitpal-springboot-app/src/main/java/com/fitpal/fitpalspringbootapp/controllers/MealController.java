package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.*;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.services.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meal")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MealController {

    @Autowired
    private MealService mealService;

    /**
     * Search for meals
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchMeal(
            @RequestAttribute("userId") String userId,
            @RequestParam String query,
            @RequestParam(required = false) Integer page) {
        try {
            MealSearchResponse response = mealService.searchMeal(query, page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get meal details by ID
     */
    @GetMapping("/{mealId}")
    public ResponseEntity<?> getMealById(
            @RequestAttribute("userId") String userId,
            @PathVariable Integer mealId) {
        try {
            MealSearchResult meal = mealService.getMealById(mealId);
            return ResponseEntity.ok(meal);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get nutrition label image
     */
    @GetMapping("/nutrition/{mealId}")
    public ResponseEntity<?> getNutritionImage(
            @RequestAttribute("userId") String userId,
            @PathVariable Integer mealId) {
        try {
            byte[] image = mealService.getNutritionImage(mealId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(image.length);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get recipe card
     */
    @GetMapping("/recipe/{mealId}")
    public ResponseEntity<?> getRecipeImage(
            @RequestAttribute("userId") String userId,
            @PathVariable Integer mealId) {
        try {
            String recipeCard = mealService.getRecipeImage(mealId);
            return ResponseEntity.ok(recipeCard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get user's favourite meals
     */
    @GetMapping("/favourites")
    public ResponseEntity<?> getFavouriteMeals(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) Integer page) {
        try {
            MealSearchResponse response = mealService.getFavouriteMeals(userId, page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Add meal to favourites
     */
    @PostMapping("/favourites")
    public ResponseEntity<?> addMealToFavourite(
            @RequestAttribute("userId") String userId,
            @RequestBody AddToFavouriteRequest request) {
        try {
            User user = mealService.addMealToFavourite(
                    userId,
                    request.getMealId(),
                    request.getFoodName(),
                    request.getImageUrl()
            );
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", e.getMessage()));
            } else if (e.getMessage().equals("Meal already in favourites")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Remove meal from favourites
     */
    @DeleteMapping("/favourites/{mealId}")
    public ResponseEntity<?> removeMealFromFavourite(
            @RequestAttribute("userId") String userId,
            @PathVariable Integer mealId) {
        try {
            mealService.removeMealFromFavourite(userId, mealId);
            return ResponseEntity.ok(Map.of("message", "Meal removed from favourites"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }
}