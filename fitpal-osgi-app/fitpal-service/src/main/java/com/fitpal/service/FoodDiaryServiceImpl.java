package com.fitpal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitpal.api.FoodDiary;
import com.fitpal.api.FoodDiaryService;
import com.fitpal.api.User;
import com.fitpal.api.dtos.spoonacular.MealPlanResponse;
import com.fitpal.api.dtos.spoonacular.NutritionResponse;
import com.fitpal.service.db.FoodDiaryRepository;
import com.fitpal.service.db.UserRepository;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.util.*;

@Component(service = FoodDiaryService.class, configurationPid = "com.fitpal.app")
public class FoodDiaryServiceImpl implements FoodDiaryService {

    @Reference
    private FoodDiaryRepository foodDiaryRepository;

    @Reference
    private UserRepository userRepository;

    @Reference
    private com.fitpal.service.db.MongoService mongoService;

    private String spoonacularApiKey;
    private static final String SPOONACULAR_BASE_URL = "https://api.spoonacular.com";
    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;

    @Activate
    public void activate(Map<String, Object> properties) {
        this.spoonacularApiKey = (String) properties.get("spoonacular.api.key");
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
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
    public List<FoodDiary.Meal> recommendMeal(String userId, Integer targetCalories, String dateStr) {
        try {
            String url = SPOONACULAR_BASE_URL + "/mealplanner/generate" +
                    "?timeFrame=day" +
                    "&targetCalories=" + (targetCalories != null ? targetCalories : 2000) +
                    "&apiKey=" + spoonacularApiKey;

            String response = executeGetRequest(url);
            MealPlanResponse mealPlan = objectMapper.readValue(response, MealPlanResponse.class);

            if (mealPlan == null || mealPlan.getMeals() == null) {
                throw new RuntimeException("Failed to fetch meal recommendations");
            }

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

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate date = LocalDate.parse(dateStr);
            FoodDiary foodDiary = foodDiaryRepository.findByUserAndDate(userId, date)
                    .orElse(new FoodDiary());

            if (foodDiary.getUser() == null) {
                foodDiary.setUser(user.getId());
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

    @Override
    public FoodDiary getDiaryByDate(String userId, String dateStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate date = LocalDate.parse(dateStr);
        Optional<FoodDiary> diary = foodDiaryRepository.findByUserAndDate(userId, date);

        if (diary.isEmpty()) {
            FoodDiary emptyDiary = new FoodDiary();
            emptyDiary.setMeals(new ArrayList<>());
            return emptyDiary;
        }

        return diary.get();
    }

    @Override
    public FoodDiary.Meal addFoodToDiary(String userId, String dateStr, String type,
                                         Integer mealId, String foodName) {
        try {
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

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate date = LocalDate.parse(dateStr);
            FoodDiary foodDiary = foodDiaryRepository.findByUserAndDate(userId, date)
                    .orElse(new FoodDiary());

            if (foodDiary.getUser() == null) {
                foodDiary.setUser(user.getId());
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

    @Override
    public void removeFoodFromDiary(String userId, String dateStr, String type, Integer mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate date = LocalDate.parse(dateStr);
        FoodDiary foodDiary = foodDiaryRepository.findByUserAndDate(userId, date)
                .orElseThrow(() -> new RuntimeException("Food Diary for this date is not available"));

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

    @Override
    public Integer getCalorieSummaryByDay(String userId, String dateStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate date = LocalDate.parse(dateStr);
        Optional<FoodDiary> diaryEntry = foodDiaryRepository.findByUserAndDate(userId, date);

        if (diaryEntry.isEmpty()) {
            return 0;
        }

        return diaryEntry.get().getMeals().stream()
                .mapToInt(FoodDiary.Meal::getCalories)
                .sum();
    }

    @Override
    public List<Map> getCalorieSummary(String userId, String mode,
                                       String startDate, String endDate,
                                       Integer month, Integer year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Document> pipeline = new ArrayList<>();

        // Match by user ObjectId
        Document matchCriteria;
        try {
            matchCriteria = new Document("user", new ObjectId(userId));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user ID format");
        }

        if ("daily".equals(mode) && startDate != null && endDate != null) {
            LocalDate from = LocalDate.parse(startDate);
            LocalDate to = LocalDate.parse(endDate).plusDays(1);

            Date fromDate = java.sql.Date.valueOf(from);
            Date toDate = java.sql.Date.valueOf(to);

            matchCriteria.append("date", new Document("$gte", fromDate).append("$lt", toDate));
        }

        if ("weekly".equals(mode) && month != null && year != null) {
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1);

            Date startDate1 = java.sql.Date.valueOf(startOfMonth);
            Date endDate1 = java.sql.Date.valueOf(endOfMonth);

            matchCriteria.append("date", new Document("$gte", startDate1).append("$lt", endDate1));
        }

        pipeline.add(new Document("$match", matchCriteria));
        pipeline.add(new Document("$unwind", "$meals"));

        Document groupStage;
        if ("weekly".equals(mode)) {
            groupStage = new Document("$group", new Document()
                    .append("_id", new Document()
                            .append("week", new Document("$isoWeek", "$date"))
                            .append("year", new Document("$isoWeekYear", "$date")))
                    .append("totalCalories", new Document("$sum", "$meals.calories")));
        } else {
            groupStage = new Document("$group", new Document()
                    .append("_id", "$date")
                    .append("totalCalories", new Document("$sum", "$meals.calories")));
        }
        pipeline.add(groupStage);
        pipeline.add(new Document("$sort", new Document("_id", 1)));

        MongoCollection<Document> collection = mongoService.getDatabase().getCollection("fooddiaries");
        AggregateIterable<Document> results = collection.aggregate(pipeline);

        List<Map> mappedResults = new ArrayList<>();
        for (Document doc : results) {
            mappedResults.add(new HashMap<>(doc));
        }

        return mappedResults;
    }

    private Map<String, Integer> fetchNutrition(Integer mealId) {
        try {
            String url = SPOONACULAR_BASE_URL + "/recipes/" + mealId +
                    "/nutritionWidget.json?apiKey=" + spoonacularApiKey;

            String response = executeGetRequest(url);
            NutritionResponse nutrition = objectMapper.readValue(response, NutritionResponse.class);

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

    private String executeGetRequest(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }
}