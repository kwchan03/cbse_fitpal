package com.fitpal.service.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.fitpal.api.User;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component(service = UserRepository.class)
public class UserRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getUserCollection() {
        return mongoService.getDatabase().getCollection("users");
    }

    // ========== CRUD Operations ==========

    /**
     * Save user (insert new or update existing)
     * Mirrors Spring Boot's save() behavior - handles both insert and update
     */
    public User save(User user) {
        if (user.getId() == null) {
            // Insert new user
            Document doc = mapUserToDocument(user);
            ObjectId newId = new ObjectId();
            doc.append("_id", newId);
            user.setId(newId.toString());
            getUserCollection().insertOne(doc);
        } else {
            // Update existing user
            update(user);
        }
        return user;
    }

    /**
     * Update existing user (internal use)
     */
    private void update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        Document doc = mapUserToDocument(user);
        getUserCollection().replaceOne(
                Filters.eq("_id", new ObjectId(user.getId())),
                doc
        );
    }

    /**
     * Delete user by ID
     */
    public void deleteById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        try {
            getUserCollection().deleteOne(Filters.eq("_id", new ObjectId(id)));
        } catch (IllegalArgumentException e) {
            // Invalid ObjectId format
            throw new IllegalArgumentException("Invalid user ID format", e);
        }
    }

    /**
     * Find user by ID
     * Returns Optional for null-safe handling (matches Spring Boot)
     */
    public Optional<User> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Document doc = getUserCollection()
                    .find(Filters.eq("_id", new ObjectId(id)))
                    .first();
            return (doc != null) ? Optional.of(mapDocumentToUser(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            // Invalid ObjectId format
            return Optional.empty();
        }
    }

    /**
     * Find user by email
     * Returns Optional for null-safe handling (matches Spring Boot)
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return Optional.empty();
        }
        Document doc = getUserCollection()
                .find(Filters.eq("email", email))
                .first();
        return (doc != null) ? Optional.of(mapDocumentToUser(doc)) : Optional.empty();
    }

    /**
     * Check if user exists by email (NEW - matches Spring Boot)
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return getUserCollection()
                .countDocuments(Filters.eq("email", email)) > 0;
    }

    /**
     * Backward compatibility: Get user by ID (returns null instead of Optional)
     */
    public User getUserById(String id) {
        return findById(id).orElse(null);
    }

    /**
     * Backward compatibility: Get user by email (returns null instead of Optional)
     */
    public User getUserByEmail(String email) {
        return findByEmail(email).orElse(null);
    }

    // ========== Mappers ==========

    private Document mapUserToDocument(User user) {
        Document doc = new Document();
        if (user.getId() != null) {
            doc.append("_id", new ObjectId(user.getId()));
        }
        doc.append("email", user.getEmail());
        doc.append("password", user.getPassword());
        doc.append("firstName", user.getFirstName());
        doc.append("lastName", user.getLastName());
        doc.append("profilePictureUrl", user.getProfilePictureUrl());
        doc.append("gender", user.getGender());
        doc.append("dob", user.getDob());
        doc.append("weight", user.getWeight());
        doc.append("height", user.getHeight());
        doc.append("activityLevel", user.getActivityLevel());
        doc.append("weightGoal", user.getWeightGoal());
        doc.append("createdAt", user.getCreatedAt());
        doc.append("deactivated", user.isDeactivated());

        // Daily Targets
        doc.append("dailyTargetCalorie", user.getDailyTargetCalorie());
        doc.append("dailyTargetSteps", user.getDailyTargetSteps());
        doc.append("dailyTargetActivity", user.getDailyTargetActivity());

        // Map List<FavouriteFood> -> List<Document>
        List<User.FavouriteFood> foods = user.getFavouriteFoods();
        if (foods != null && !foods.isEmpty()) {
            List<Document> foodDocs = new ArrayList<>();
            for (User.FavouriteFood food : foods) {
                Document fDoc = new Document();
                fDoc.append("mealId", food.getMealId());
                fDoc.append("foodName", food.getFoodName());
                fDoc.append("imageUrl", food.getImageUrl());
                foodDocs.add(fDoc);
            }
            doc.append("favouriteFood", foodDocs);
        }

        return doc;
    }

    private User mapDocumentToUser(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id").toString());
        user.setEmail(doc.getString("email"));
        user.setPassword(doc.getString("password"));
        user.setFirstName(doc.getString("firstName"));
        user.setLastName(doc.getString("lastName"));
        user.setProfilePictureUrl(doc.getString("profilePictureUrl"));
        user.setGender(doc.getString("gender"));
        user.setDob(doc.getDate("dob"));

        // Handle Doubles safely
        user.setWeight(getDoubleSafe(doc, "weight"));
        user.setHeight(getDoubleSafe(doc, "height"));
        user.setActivityLevel(getDoubleSafe(doc, "activityLevel")); // This is a Double (e.g., 1.2)
        user.setWeightGoal(getIntSafe(doc, "weightGoal"));       // This is a Double (e.g., -0.5)
        user.setCreatedAt(doc.getDate("createdAt"));

        Boolean deactivated = doc.getBoolean("deactivated");
        user.setDeactivated(deactivated != null ? deactivated : false);

        // Daily Targets
        user.setDailyTargetCalorie(getIntSafe(doc, "dailyTargetCalorie"));
        user.setDailyTargetSteps(getIntSafe(doc, "dailyTargetSteps"));
        user.setDailyTargetActivity(getIntSafe(doc, "dailyTargetActivity"));

        // Map List<Document> -> List<FavouriteFood>
        List<Document> foodDocs = doc.getList("favouriteFood", Document.class);
        if (foodDocs != null) {
            List<User.FavouriteFood> foods = new ArrayList<>();
            for (Document fDoc : foodDocs) {
                User.FavouriteFood food = new User.FavouriteFood();
                food.setMealId(fDoc.getInteger("mealId"));
                food.setFoodName(fDoc.getString("foodName"));
                food.setImageUrl(fDoc.getString("imageUrl"));
                foods.add(food);
            }
            user.setFavouriteFoods(foods);
        }

        return user;
    }

    private Double getDoubleSafe(Document doc, String key) {
        Object val = doc.get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return null;
    }

    private Integer getIntSafe(Document doc, String key) {
        Object val = doc.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return 0; // Default to 0
    }
}
