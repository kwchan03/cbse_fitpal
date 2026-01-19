package com.fitpal.api;

import java.util.Map;

public interface UserService {
    // --- Onboarding Flow ---
    User registerUser(User user);
    User registerUserProfile(String userId, User profileData, byte[] imageBytes);
    User registerUserPhysicalInfo(String userId, User physicalData);

    // --- Profile Management ---
    User getUserInfo(String userId);
    User updateUserProfile(String userId, User profileData, byte[] imageBytes);

    // --- Account Management (Auth checks removed) ---
    void changePassword(String userId, String currentPassword, String newPassword) throws Exception;
    void deactivateAccount(String userId, String password) throws Exception;

    // --- Reactivate Account ---
    // Changed return type from String (token) to void
    void reactivateAccount(String email, String password);

    void deleteAccount(String userId, String password) throws Exception;

    // --- Data ---
    Map<String, Object> getUserGoals(String userId);
}