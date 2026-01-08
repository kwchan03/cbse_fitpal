package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.dtos.*;
import com.fitpal.fitpalspringbootapp.dtos.spoonacular.RecipeAutocomplete;
import com.fitpal.fitpalspringbootapp.dtos.spoonacular.RecipeInformation;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spoonacular.api.key}")
    private String spoonacularApiKey;

    private static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com";
    private static final int PAGE_SIZE = 6;

    /**
     * Search for meals using Spoonacular API
     */
    public MealSearchResponse searchMeal(String query, Integer page) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/autocomplete" +
                    "?number=24" +
                    "&query=" + query +
                    "&apiKey=" + spoonacularApiKey;

            // Fetch results from Spoonacular
            ResponseEntity<List<RecipeAutocomplete>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RecipeAutocomplete>>() {}
            );

            List<RecipeAutocomplete> results = response.getBody();
            if (results == null) {
                results = List.of();
            }

            // Pagination logic
            int pageNumber = page != null ? page : 1;
            int skip = (pageNumber - 1) * PAGE_SIZE;
            int total = results.size();

            // Slice results for pagination
            List<MealSearchResult> searchResults = results.stream()
                    .skip(skip)
                    .limit(PAGE_SIZE)
                    .map(result -> new MealSearchResult(
                            result.getId(),
                            result.getTitle(),
                            "https://img.spoonacular.com/recipes/" + result.getId() + "-312x231.jpg"
                    ))
                    .collect(Collectors.toList());

            // Build response with pagination
            MealSearchResponse.Pagination pagination = new MealSearchResponse.Pagination(
                    total,
                    pageNumber,
                    (int) Math.ceil((double) total / PAGE_SIZE)
            );

            return new MealSearchResponse(searchResults, pagination);

        } catch (Exception e) {
            throw new RuntimeException("Error searching meals: " + e.getMessage(), e);
        }
    }

    /**
     * Get meal details by ID
     */
    public MealSearchResult getMealById(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/information?includeNutrition=false&apiKey=" + spoonacularApiKey;

            RecipeInformation recipe = restTemplate.getForObject(url, RecipeInformation.class);

            if (recipe == null) {
                throw new RuntimeException("Meal not found");
            }

            return new MealSearchResult(
                    recipe.getId(),
                    recipe.getTitle(),
                    "https://img.spoonacular.com/recipes/" + mealId + "-312x231.jpg"
            );

        } catch (Exception e) {
            throw new RuntimeException("Error fetching meal details: " + e.getMessage(), e);
        }
    }

    /**
     * Get nutrition image for a meal
     */
    public byte[] getNutritionImage(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/nutritionLabel.png?apiKey=" + spoonacularApiKey;

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching nutrition image: " + e.getMessage(), e);
        }
    }

    /**
     * Get recipe card image
     */
    public String getRecipeImage(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/card?apiKey=" + spoonacularApiKey;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching recipe image: " + e.getMessage(), e);
        }
    }

    /**
     * Add meal to user's favourites
     */
    public User addMealToFavourite(String userId, Integer mealId, String foodName, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if meal already exists in favourites
        boolean exists = user.getFavouriteFood().stream()
                .anyMatch(fav -> fav.getMealId().equals(mealId));

        if (exists) {
            throw new RuntimeException("Meal already in favourites");
        }

        // Add to favourites
        User.FavouriteFood favourite = new User.FavouriteFood(mealId, foodName, imageUrl);
        user.getFavouriteFood().add(favourite);

        return userRepository.save(user);
    }

    /**
     * Remove meal from user's favourites
     */
    public void removeMealFromFavourite(String userId, Integer mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove the meal
        user.getFavouriteFood().removeIf(fav -> fav.getMealId().equals(mealId));

        userRepository.save(user);
    }

    /**
     * Get user's favourite meals with pagination
     */
    public MealSearchResponse getFavouriteMeals(String userId, Integer page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User.FavouriteFood> favourites = user.getFavouriteFood();

        // Pagination logic
        int pageNumber = page != null ? page : 1;
        int skip = (pageNumber - 1) * PAGE_SIZE;
        int total = favourites.size();

        // Slice results for pagination
        List<MealSearchResult> results = favourites.stream()
                .skip(skip)
                .limit(PAGE_SIZE)
                .map(fav -> new MealSearchResult(
                        fav.getMealId(),
                        fav.getFoodName(),
                        fav.getImageUrl()
                ))
                .collect(Collectors.toList());

        // Build response with pagination
        MealSearchResponse.Pagination pagination = new MealSearchResponse.Pagination(
                total,
                pageNumber,
                (int) Math.ceil((double) total / PAGE_SIZE)
        );

        return new MealSearchResponse(results, pagination);
    }
}