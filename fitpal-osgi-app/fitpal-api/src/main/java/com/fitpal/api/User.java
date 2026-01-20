package com.fitpal.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String id;               // MongoDB _id
    private String email;            // required, unique
    private String password;         // required
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String gender;
    private Date dob;
    private Double weight;           // in kg
    private Double height;
    private Double activityLevel;
    private Integer weightGoal;      // enum: -500, 0, 500
    private Date createdAt;
    private boolean deactivated;

    // Daily Targets
    private Double dailyTargetCalorie;
    private Integer dailyTargetSteps;
    private Double dailyTargetActivity;

    // Complex Type
    private List<FavouriteFood> favouriteFood = new ArrayList<>();

    public User() {
        this.createdAt = new Date(); // Default to now
        this.deactivated = false;    // Default to false
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getActivityLevel() { return activityLevel; }
    public void setActivityLevel(Double activityLevel) { this.activityLevel = activityLevel; }

    public Integer getWeightGoal() { return weightGoal; }
    public void setWeightGoal(Integer weightGoal) { this.weightGoal = weightGoal; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isDeactivated() { return deactivated; }
    public void setDeactivated(boolean deactivated) { this.deactivated = deactivated; }

    public Double getDailyTargetCalorie() { return dailyTargetCalorie; }
    public void setDailyTargetCalorie(Double dailyTargetCalorie) { this.dailyTargetCalorie = dailyTargetCalorie; }

    public Integer getDailyTargetSteps() { return dailyTargetSteps; }
    public void setDailyTargetSteps(Integer dailyTargetSteps) { this.dailyTargetSteps = dailyTargetSteps; }

    public Double getDailyTargetActivity() { return dailyTargetActivity; }
    public void setDailyTargetActivity(Double dailyTargetActivity) { this.dailyTargetActivity = dailyTargetActivity; }

    public List<FavouriteFood> getFavouriteFoods() { return favouriteFood; }
    public void setFavouriteFoods(List<FavouriteFood> favouriteFoods) { this.favouriteFood = favouriteFoods; }

    // --- Inner Class for Favourite Food ---
    public static class FavouriteFood {
        private Integer mealId;
        private String foodName;
        private String imageUrl;

        public FavouriteFood() {}
        public FavouriteFood(Integer mealId, String foodName, String imageUrl) {
            this.mealId = mealId;
            this.foodName = foodName;
            this.imageUrl = imageUrl;
        }

        public Integer getMealId() { return mealId; }
        public void setMealId(Integer mealId) { this.mealId = mealId; }
        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}

