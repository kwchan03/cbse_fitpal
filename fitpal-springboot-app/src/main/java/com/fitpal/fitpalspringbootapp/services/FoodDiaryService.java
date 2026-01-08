package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.dtos.spoonacular.MealPlanResponse;
import com.fitpal.fitpalspringbootapp.dtos.spoonacular.NutritionResponse;
import com.fitpal.fitpalspringbootapp.models.FoodDiary;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.FoodDiaryRepository;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoodDiaryService {

    @Autowired
    private FoodDiaryRepository foodDiaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spoonacular.api.key}")
    private String spoonacularApiKey;

    private static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com";

    /**
     * Recommend meals using Spoonacular API and save to diary
     */
    public List<FoodDiary.Meal> recommendMeal(String userId, Integer targetCalories, String dateStr) {
        try {
            // Get meal plan from Spoonacular
            String url = SPOONACULAR_BASE_URL + "/mealplanner/generate" +
                    "?timeFrame=day" +
                    "&targetCalories=" + (targetCalories != null ? targetCalories : 2000) +
                    "&apiKey=" + spoonacularApiKey;

            MealPlanResponse mealPlan = restTemplate.getForObject(url, MealPlanResponse.class);

            if (mealPlan == null || mealPlan.getMeals() == null) {
                throw new RuntimeException("Failed to fetch meal recommendations");
            }

            // Fetch nutrition for each meal
            String[] mealTypes = {"breakfast", "lunch", "dinner"};
            List<FoodDiary.Meal> mealData = new ArrayList<>();

            for (int i = 0; i < mealPlan.getMeals().size(); i++) {
                MealPlanResponse.Meal meal = mealPlan.getMeals().get(i);
                Map<String, Integer> nutrition = fetchNutrition(meal.getId());

                FoodDiary.Meal mealObj = new FoodDiary.Meal();
                mealObj.setMealId(meal.getId());
                mealObj.setFoodName(meal.getTitle());
                mealObj.setImageUrl("https://img.spoonacular.com/recipes/" + meal.getId() + "-312x231.jpg");
                mealObj.setCalories(nutrition.get("calories"));
                mealObj.setProtein(nutrition.get("protein"));
                mealObj.setFat(nutrition.get("fat"));
                mealObj.setCarbs(nutrition.get("carbs"));
                mealObj.setMealType(i < mealTypes.length ? mealTypes[i] : "dinner");

                mealData.add(mealObj);
            }

            // Save to food diary
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate date = LocalDate.parse(dateStr);
            FoodDiary foodDiary = foodDiaryRepository.findByUserAndDate(user, date)
                    .orElse(new FoodDiary());

            if (foodDiary.getUser() == null) {
                foodDiary.setUser(user);
                foodDiary.setDate(date);
                foodDiary.setMeals(new ArrayList<>());
            }

            foodDiary.getMeals().addAll(mealData);
            foodDiaryRepository.save(foodDiary);

            return mealData;

        } catch (Exception e) {
            throw new RuntimeException("Error recommending meals: " + e.getMessage(), e);
        }
    }

    /**
     * Get food diary by date
     */
    public FoodDiary getDiaryByDate(String userId, String dateStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate date = LocalDate.parse(dateStr);

        Optional<FoodDiary> diary = foodDiaryRepository.findByUserAndDate(user, date);

        if (diary.isEmpty()) {
            // Return empty diary
            FoodDiary emptyDiary = new FoodDiary();
            emptyDiary.setMeals(new ArrayList<>());
            return emptyDiary;
        }

        return diary.get();
    }

    /**
     * Add food to diary
     */
    public FoodDiary.Meal addFoodToDiary(String userId, String dateStr, String type,
                                         Integer mealId, String foodName) {
        try {
            // Fetch nutrition
            Map<String, Integer> nutrition = fetchNutrition(mealId);

            FoodDiary.Meal meal = new FoodDiary.Meal();
            meal.setMealId(mealId);
            meal.setFoodName(foodName);
            meal.setImageUrl("https://img.spoonacular.com/recipes/" + mealId + "-312x231.jpg");
            meal.setCalories(nutrition.get("calories"));
            meal.setProtein(nutrition.get("protein"));
            meal.setFat(nutrition.get("fat"));
            meal.setCarbs(nutrition.get("carbs"));
            meal.setMealType(type);

            // Find or create food diary
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate date = LocalDate.parse(dateStr);
            FoodDiary foodDiary = foodDiaryRepository.findByUserAndDate(user, date)
                    .orElse(new FoodDiary());

            if (foodDiary.getUser() == null) {
                foodDiary.setUser(user);
                foodDiary.setDate(date);
                foodDiary.setMeals(new ArrayList<>());
            }

            foodDiary.getMeals().add(meal);
            foodDiaryRepository.save(foodDiary);

            return meal;

        } catch (Exception e) {
            throw new RuntimeException("Error adding food to diary: " + e.getMessage(), e);
        }
    }

    /**
     * Remove food from diary
     */
    public void removeFoodFromDiary(String userId, String dateStr, String type, Integer mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate date = LocalDate.parse(dateStr);
        FoodDiary foodDiary = foodDiaryRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new RuntimeException("Food Diary for this date is not available"));

        // Find the meal to remove
        int indexToRemove = -1;
        List<FoodDiary.Meal> meals = foodDiary.getMeals();

        for (int i = 0; i < meals.size(); i++) {
            FoodDiary.Meal meal = meals.get(i);
            if (meal.getMealId().equals(mealId) && meal.getMealType().equals(type)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            throw new RuntimeException("Meal not found in diary for specified type");
        }

        meals.remove(indexToRemove);
        foodDiaryRepository.save(foodDiary);
    }

    /**
     * Get calorie summary for a specific day
     */
    public Integer getCalorieSummaryByDay(String userId, String dateStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate date = LocalDate.parse(dateStr);
        Optional<FoodDiary> diaryEntry = foodDiaryRepository.findByUserAndDate(user, date);

        if (diaryEntry.isEmpty()) {
            return 0;
        }

        return diaryEntry.get().getMeals().stream()
                .mapToInt(FoodDiary.Meal::getCalories)
                .sum();
    }

    /**
     * Get calorie summary (daily or weekly aggregation)
     */
    public List<Map> getCalorieSummary(String userId, String mode,
                                       String startDate, String endDate,
                                       Integer month, Integer year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Criteria criteria = Criteria.where("user.$id").is(user.getId());

        // Daily mode
        if ("daily".equals(mode) && startDate != null && endDate != null) {
            LocalDate from = LocalDate.parse(startDate);
            LocalDate to = LocalDate.parse(endDate).plusDays(1); // exclusive end

            criteria = criteria.and("date").gte(from).lt(to);
        }

        // Weekly mode
        if ("weekly".equals(mode) && month != null && year != null) {
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1);

            criteria = criteria.and("date").gte(startOfMonth).lt(endOfMonth);
        }

        // Build aggregation pipeline
        MatchOperation matchOperation = Aggregation.match(criteria);
        UnwindOperation unwindOperation = Aggregation.unwind("meals");

        GroupOperation groupOperation;
        SortOperation sortOperation;

        if ("weekly".equals(mode)) {
            groupOperation = Aggregation.group(
                    Fields.fields()
                            .and("week", DateOperators.IsoWeek.isoWeekOf("date").toString())
                            .and("year", DateOperators.IsoWeekYear.isoWeekYearOf("date").toString())
            ).sum("meals.calories").as("totalCalories");

            sortOperation = Aggregation.sort(Sort.by("_id.week").ascending());
        } else {
            groupOperation = Aggregation.group("date")
                    .sum("meals.calories").as("totalCalories");

            sortOperation = Aggregation.sort(Sort.by("_id").ascending());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                unwindOperation,
                groupOperation,
                sortOperation
        );

        return mongoTemplate.aggregate(aggregation, "fooddiaries", Map.class)
                .getMappedResults();
    }

    /**
     * Fetch nutrition information from Spoonacular API
     */
    private Map<String, Integer> fetchNutrition(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/nutritionWidget.json?apiKey=" + spoonacularApiKey;

            NutritionResponse nutrition = restTemplate.getForObject(url, NutritionResponse.class);

            if (nutrition == null || nutrition.getNutrients() == null) {
                throw new RuntimeException("Failed to fetch nutrition data");
            }

            Map<String, Integer> result = new HashMap<>();
            result.put("calories", getNutrientValue(nutrition, "Calories"));
            result.put("protein", getNutrientValue(nutrition, "Protein"));
            result.put("fat", getNutrientValue(nutrition, "Fat"));
            result.put("carbs", getNutrientValue(nutrition, "Carbohydrates"));

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching nutrition: " + e.getMessage(), e);
        }
    }

    private Integer getNutrientValue(NutritionResponse nutrition, String name) {
        return nutrition.getNutrients().stream()
                .filter(n -> n.getName().equals(name))
                .findFirst()
                .map(n -> (int) Math.round(n.getAmount()))
                .orElse(0);
    }
}