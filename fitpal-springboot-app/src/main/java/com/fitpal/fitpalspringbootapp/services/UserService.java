package com.fitpal.fitpalspringbootapp.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fitpal.fitpalspringbootapp.dtos.CreatePhysicalInfoRequest;
import com.fitpal.fitpalspringbootapp.dtos.CreateProfileRequest;
import com.fitpal.fitpalspringbootapp.dtos.RegisterRequest;
import com.fitpal.fitpalspringbootapp.dtos.UpdateProfileRequest;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import com.fitpal.fitpalspringbootapp.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Cloudinary cloudinary;

    // TODO: Implement Cloudinary service for image uploads
    // @Autowired
    // private CloudinaryService cloudinaryService;

    /**
     * Register a new user
     * TODO: To be completed
     */
    public User registerUser(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setDeactivated(false);

        // Save user
        return userRepository.save(user);
    }

    /**
     * Register/Update user profile information
     * TODO: To be completed - handle profile picture upload to Cloudinary
     */
    public User registerUserProfile(String userId, CreateProfileRequest profileData, MultipartFile imageFile) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        // Update profile fields
        user.setFirstName(profileData.getFirstName());
        user.setLastName(profileData.getLastName());
        user.setGender(profileData.getGender());
        user.setDob(profileData.getDob());

        // TODO: Upload image to Cloudinary and set profilePictureUrl
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadImagesToCloudinary(imageFile);
            user.setProfilePictureUrl(imageUrl);
        }

        return userRepository.save(user);
    }

    /**
     * Register/Update user physical information and calculate daily targets
     * TODO: To be completed
     */
    public User registerUserPhysicalInfo(String userId, CreatePhysicalInfoRequest physicalData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        // Update physical info
        user.setWeight(physicalData.getWeight());
        user.setHeight(physicalData.getHeight());
        user.setActivityLevel(physicalData.getActivityLevel());
        user.setWeightGoal(Integer.valueOf(physicalData.getWeightGoal()));

        // Calculate BMR and target calorie
        int age = calculateAge(user.getDob());
        double bmr = 10 * user.getWeight()
                + 6.25 * user.getHeight()
                - 5 * age
                + ("Male".equals(user.getGender()) ? 5 : -161);

        double targetCalorie = Math.ceil(bmr * user.getActivityLevel() + user.getWeightGoal());
        user.setDailyTargetCalorie(targetCalorie);
        user.setDailyTargetSteps(0);
        user.setDailyTargetActivity(0);

        return userRepository.save(user);
    }

    /**
     * Get user information by ID (excluding password)
     */
    public User getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        // Clear password before returning
        user.setPassword(null);
        return user;
    }

    /**
     * Update user profile
     * TODO: To be completed - handle profile picture upload
     */
    public User updateUserProfile(String userId, UpdateProfileRequest profileData, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        // Update profile fields
        if (profileData.getFirstName() != null) user.setFirstName(profileData.getFirstName());
        if (profileData.getLastName() != null) user.setLastName(profileData.getLastName());
        if (profileData.getGender() != null) user.setGender(profileData.getGender());
        if (profileData.getDob() != null) user.setDob(profileData.getDob());
        if (profileData.getWeight() != null) user.setWeight(profileData.getWeight());
        if (profileData.getHeight() != null) user.setHeight(profileData.getHeight());
        if (profileData.getActivityLevel() != null) user.setActivityLevel(profileData.getActivityLevel());
        if (profileData.getWeightGoal() != null) user.setWeightGoal(Integer.valueOf(profileData.getWeightGoal()));

        // TODO: Upload image to Cloudinary if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadImagesToCloudinary(imageFile);
            user.setProfilePictureUrl(imageUrl);
        }

        // Recalculate BMR and target calorie if physical data changed
        if (user.getDob() != null && user.getWeight() != null &&
                user.getHeight() != null && user.getGender() != null) {
            int age = calculateAge(user.getDob());
            double bmr = 10 * user.getWeight()
                    + 6.25 * user.getHeight()
                    - 5 * age
                    + ("Male".equals(user.getGender()) ? 5 : -161);

            double targetCalorie = bmr * user.getActivityLevel() + user.getWeightGoal();
            user.setDailyTargetCalorie(targetCalorie);
        }

        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        // Update with new password (hashed)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Deactivate user account
     */
    public void deactivateAccount(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        user.setDeactivated(true);
        userRepository.save(user);
    }

    /**
     * Reactivate user account
     */
    public String reactivateAccount(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        // Check if already active
        if (!user.getDeactivated()) {
            throw new RuntimeException("Account is already active");
        }

        user.setDeactivated(false);
        userRepository.save(user);

        // Generate JWT token
        return jwtUtil.generateToken(user.getId());
    }

    /**
     * Delete user account permanently
     */
    public void deleteAccount(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        userRepository.deleteById(userId);
    }

    private String uploadImagesToCloudinary(MultipartFile imageFile) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                throw new RuntimeException("Image file is empty");
            }

            // FIXED: Upload bytes directly instead of constructing a Data URI
            Map<String, Object> params = new HashMap<>();
            params.put("resource_type", "auto");

            Map uploadResponse = cloudinary.uploader().upload(imageFile.getBytes(), params);
            return (String) uploadResponse.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    /**
     * Get user daily goals
     */
    public Map<String, Object> getUserGoals(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> goals = new HashMap<>();
        goals.put("calories", user.getDailyTargetCalorie() != null ? user.getDailyTargetCalorie() : 0);
        goals.put("steps", user.getDailyTargetSteps() != null ? user.getDailyTargetSteps() : 0);
        goals.put("activity", user.getDailyTargetActivity() != null ? user.getDailyTargetActivity() : 0);

        return goals;
    }

    /**
     * Calculate age from date of birth
     */
    private int calculateAge(LocalDate dob) {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }
}