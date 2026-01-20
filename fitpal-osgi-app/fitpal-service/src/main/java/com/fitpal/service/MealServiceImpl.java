package com.fitpal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitpal.api.MealService;
import com.fitpal.api.User;
import com.fitpal.api.dtos.MealSearchResponse;
import com.fitpal.api.dtos.MealSearchResult;
import com.fitpal.api.dtos.spoonacular.RecipeAutocomplete;
import com.fitpal.api.dtos.spoonacular.RecipeInformation;
import com.fitpal.service.db.UserRepository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(service = MealService.class, configurationPid = "com.fitpal.app")
public class MealServiceImpl implements MealService {

    @Reference
    private UserRepository userRepository;

    private String spoonacularApiKey;
    private static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com";
    private static final int PAGE_SIZE = 6;
    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;

    @Activate
    public void activate(Map<String, Object> properties) {
        this.spoonacularApiKey = (String) properties.get("spoonacular.api.key");
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        // Configure to ignore unknown properties (like Spring Boot does)
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Deactivate
    public void deactivate() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    @Override
    public MealSearchResponse searchMeal(String query, Integer page) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/autocomplete" +
                    "?number=24" +
                    "&query=" + query +
                    "&apiKey=" + spoonacularApiKey;

            String response = executeGetRequest(url);
            List<RecipeAutocomplete> results = objectMapper.readValue(response,
                    new TypeReference<List<RecipeAutocomplete>>() {});

            if (results == null) {
                results = List.of();
            }

            int pageNumber = page != null ? page : 1;
            int skip = (pageNumber - 1) * PAGE_SIZE;
            int total = results.size();

            List<MealSearchResult> searchResults = results.stream()
                    .skip(skip)
                    .limit(PAGE_SIZE)
                    .map(result -> new MealSearchResult(
                            result.getId(),
                            result.getTitle(),
                            "https://img.spoonacular.com/recipes/" + result.getId() + "-312x231.jpg"
                    ))
                    .collect(Collectors.toList());

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

    @Override
    public MealSearchResult getMealById(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/information?includeNutrition=false&apiKey=" + spoonacularApiKey;

            String response = executeGetRequest(url);
            System.out.println(response);
            RecipeInformation recipe = objectMapper.readValue(response, RecipeInformation.class);

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

    @Override
    public byte[] getNutritionImage(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/nutritionLabel.png?apiKey=" + spoonacularApiKey;

            return executeGetRequestBytes(url);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching nutrition image: " + e.getMessage(), e);
        }
    }

    @Override
    public String getRecipeImage(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/card?apiKey=" + spoonacularApiKey;

            return executeGetRequest(url);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching recipe image: " + e.getMessage(), e);
        }
    }

    @Override
    public User addMealToFavourite(String userId, Integer mealId, String foodName, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean exists = user.getFavouriteFoods().stream()
                .anyMatch(fav -> fav.getMealId().equals(mealId));

        if (exists) {
            throw new RuntimeException("Meal already in favourites");
        }

        User.FavouriteFood favourite = new User.FavouriteFood(mealId, foodName, imageUrl);
        user.getFavouriteFoods().add(favourite);

        return userRepository.save(user);
    }

    @Override
    public void removeMealFromFavourite(String userId, Integer mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getFavouriteFoods().removeIf(fav -> fav.getMealId().equals(mealId));

        userRepository.save(user);
    }

    @Override
    public MealSearchResponse getFavouriteMeals(String userId, Integer page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Fetching favourite meals: " + user.getFavouriteFoods());
        List<User.FavouriteFood> favourites = user.getFavouriteFoods();

        int pageNumber = page != null ? page : 1;
        int skip = (pageNumber - 1) * PAGE_SIZE;
        int total = favourites.size();

        List<MealSearchResult> results = favourites.stream()
                .skip(skip)
                .limit(PAGE_SIZE)
                .map(fav -> new MealSearchResult(
                        fav.getMealId(),
                        fav.getFoodName(),
                        fav.getImageUrl()
                ))
                .collect(Collectors.toList());

        MealSearchResponse.Pagination pagination = new MealSearchResponse.Pagination(
                total,
                pageNumber,
                (int) Math.ceil((double) total / PAGE_SIZE)
        );

        return new MealSearchResponse(results, pagination);
    }

    private String executeGetRequest(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private byte[] executeGetRequestBytes(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toByteArray(response.getEntity());
        }
    }
}


