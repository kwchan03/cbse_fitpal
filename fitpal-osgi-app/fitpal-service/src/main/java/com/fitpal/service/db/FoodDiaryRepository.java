package com.fitpal.service.db;

import com.fitpal.api.FoodDiary;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component(service = FoodDiaryRepository.class)
public class FoodDiaryRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getFoodDiaryCollection() {
        return mongoService.getDatabase().getCollection("fooddiaries");
    }

    /**
     * Save food diary (insert new or update existing)
     */
    public FoodDiary save(FoodDiary foodDiary) {
        if (foodDiary.getId() == null) {
            // Insert new
            Document doc = mapFoodDiaryToDocument(foodDiary);
            ObjectId newId = new ObjectId();
            doc.append("_id", newId);
            foodDiary.setId(newId.toString());
            getFoodDiaryCollection().insertOne(doc);
        } else {
            // Update existing
            update(foodDiary);
        }
        return foodDiary;
    }

    /**
     * Update existing food diary
     */
    private void update(FoodDiary foodDiary) {
        if (foodDiary.getId() == null) {
            throw new IllegalArgumentException("FoodDiary ID cannot be null for update");
        }
        Document doc = mapFoodDiaryToDocument(foodDiary);
        getFoodDiaryCollection().replaceOne(
                Filters.eq("_id", new ObjectId(foodDiary.getId())),
                doc
        );
    }

    /**
     * Find food diary by user ID and date
     */
    public Optional<FoodDiary> findByUserAndDate(String userId, LocalDate date) {
        if (userId == null || date == null) {
            return Optional.empty();
        }

        // Convert LocalDate to Date for MongoDB query
        Date dateAsDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            Document doc = getFoodDiaryCollection()
                    .find(Filters.and(
                            Filters.eq("user", new ObjectId(userId)),  // Query by ObjectId
                            Filters.eq("date", dateAsDate)
                    ))
                    .first();

            return (doc != null) ? Optional.of(mapDocumentToFoodDiary(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            // Invalid ObjectId format
            return Optional.empty();
        }
    }

    /**
     * Find food diaries by user ID and date range
     */
    public List<FoodDiary> findByUserAndDateBetween(String userId, LocalDate startDate, LocalDate endDate) {
        if (userId == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }

        // Convert LocalDate to Date for MongoDB query
        Date startAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            List<FoodDiary> results = new ArrayList<>();
            getFoodDiaryCollection()
                    .find(Filters.and(
                            Filters.eq("user", new ObjectId(userId)),  // Query by ObjectId
                            Filters.gte("date", startAsDate),
                            Filters.lt("date", endAsDate)
                    ))
                    .into(new ArrayList<>())
                    .forEach(doc -> results.add(mapDocumentToFoodDiary(doc)));

            return results;
        } catch (IllegalArgumentException e) {
            // Invalid ObjectId format
            return new ArrayList<>();
        }
    }

    /**
     * Find food diary by ID
     */
    public Optional<FoodDiary> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Document doc = getFoodDiaryCollection()
                    .find(Filters.eq("_id", new ObjectId(id)))
                    .first();
            return (doc != null) ? Optional.of(mapDocumentToFoodDiary(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Delete food diary by ID
     */
    public void deleteById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("FoodDiary ID cannot be null");
        }
        try {
            getFoodDiaryCollection().deleteOne(Filters.eq("_id", new ObjectId(id)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FoodDiary ID format", e);
        }
    }

    // ========== Mappers ==========

    private Document mapFoodDiaryToDocument(FoodDiary foodDiary) {
        Document doc = new Document();
        if (foodDiary.getId() != null) {
            doc.append("_id", new ObjectId(foodDiary.getId()));
        }

        // Store user as ObjectId (not String)
        if (foodDiary.getUser() != null) {
            try {
                doc.append("user", new ObjectId(foodDiary.getUser()));
            } catch (IllegalArgumentException e) {
                // If it's not a valid ObjectId, store as string (fallback)
                doc.append("user", foodDiary.getUser());
            }
        }

        // Convert LocalDate to Date for MongoDB
        if (foodDiary.getDate() != null) {
            Date date = Date.from(foodDiary.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            doc.append("date", date);
        }

        // Map List<Meal> -> List<Document>
        List<FoodDiary.Meal> meals = foodDiary.getMeals();
        if (meals != null && !meals.isEmpty()) {
            List<Document> mealDocs = new ArrayList<>();
            for (FoodDiary.Meal meal : meals) {
                Document mealDoc = new Document();
                mealDoc.append("mealId", meal.getMealId());
                mealDoc.append("foodName", meal.getFoodName());
                mealDoc.append("imageUrl", meal.getImageUrl());
                mealDoc.append("calories", meal.getCalories());
                mealDoc.append("protein", meal.getProtein());
                mealDoc.append("fat", meal.getFat());
                mealDoc.append("carbs", meal.getCarbs());
                mealDoc.append("mealType", meal.getMealType());
                mealDocs.add(mealDoc);
            }
            doc.append("meals", mealDocs);
        } else {
            doc.append("meals", new ArrayList<>());
        }

        return doc;
    }

    private FoodDiary mapDocumentToFoodDiary(Document doc) {
        FoodDiary foodDiary = new FoodDiary();
        foodDiary.setId(doc.getObjectId("_id").toString());

        // Read user field - it can be ObjectId or String
        Object userField = doc.get("user");
        if (userField instanceof ObjectId) {
            foodDiary.setUser(((ObjectId) userField).toString());
        } else if (userField instanceof String) {
            foodDiary.setUser((String) userField);
        }

        // Convert Date to LocalDate
        Date date = doc.getDate("date");
        if (date != null) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            foodDiary.setDate(localDate);
        }

        // Map List<Document> -> List<Meal>
        List<Document> mealDocs = doc.getList("meals", Document.class);
        if (mealDocs != null) {
            List<FoodDiary.Meal> meals = new ArrayList<>();
            for (Document mealDoc : mealDocs) {
                FoodDiary.Meal meal = new FoodDiary.Meal();
                meal.setMealId(mealDoc.getInteger("mealId"));
                meal.setFoodName(mealDoc.getString("foodName"));
                meal.setImageUrl(mealDoc.getString("imageUrl"));
                meal.setCalories(mealDoc.getInteger("calories"));
                meal.setProtein(mealDoc.getInteger("protein"));
                meal.setFat(mealDoc.getInteger("fat"));
                meal.setCarbs(mealDoc.getInteger("carbs"));
                meal.setMealType(mealDoc.getString("mealType"));
                meals.add(meal);
            }
            foodDiary.setMeals(meals);
        } else {
            foodDiary.setMeals(new ArrayList<>());
        }

        return foodDiary;
    }
}